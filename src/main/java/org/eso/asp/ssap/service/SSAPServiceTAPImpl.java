package org.eso.asp.ssap.service;

/*
 * This file is part of SSAPServer.
 *
 * SSAPServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SSAPServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SSAPServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2017 - European Southern Observatory (ESO)
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.eso.asp.ssap.domain.RangeListParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eso.asp.ssap.domain.ParametersMappings.*;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Service
@Configurable
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class SSAPServiceTAPImpl implements SSAPService {

    private static final Logger log = LoggerFactory.getLogger(SSAPServiceTAPImpl.class);

    @Value("${ssap.tap.timeout:10}")
    private Integer timeoutSeconds;

    @Value("${ssap.tap.url}")
    private String tapURL;

    @Value("${ssap.tap.select.clause:*}")
    public String selectedColumns;

    @Value("${ssap.tap.table:ivoa.ssa}")
    public String tapTable;

    @Value("#{${ssap.tap.params.to.columns:{:}}}")
    public Map<String, Object> paramsToColumns;

    @PostConstruct
    public void init() {
        /* if not initialized, map using the UCDs */
        if (paramsToColumns == null || paramsToColumns.size() == 0) {
            try {
                StringBuffer adqlURL = new StringBuffer(tapURL);

                String query = "SELECT * FROM TAP_SCHEMA.columns WHERE table_name = '" + tapTable + "'";

                /* TODO: json is not standard TAP. Use the VOTAble */
                adqlURL.append("/sync?LANG=ADQL")
                        .append("&FORMAT=json")
                        .append("&REQUEST=doQuery")
                        .append("&QUERY=")
                        .append(URLEncoder.encode(query, "ISO-8859-1"));

                String body = Request.Get(adqlURL.toString())
                        .connectTimeout(timeoutSeconds * 1000)
                        .socketTimeout(timeoutSeconds * 1000)
                        .execute().returnContent().asString();
                paramsToColumns = getParameterMappings(body);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object queryData(Map<String, String> params) throws IOException, ParseException {

        StringBuffer adqlURL = new StringBuffer(tapURL);

        adqlURL.append("/sync?LANG=ADQL")
               .append("&FORMAT=votable%2Ftd")
               .append("&REQUEST=doQuery")
               .append("&QUERY=")
               .append(createADQLQuery(params));

        if (params.containsKey(MAXREC)) {
            adqlURL.append("&MAXREC=").append(params.get(MAXREC));
            params.remove(MAXREC);
        }

        return Request.Get(adqlURL.toString())
                .connectTimeout(timeoutSeconds*1000)
                .socketTimeout(timeoutSeconds*1000)
                .execute().returnContent().asString();

    }

    protected String createADQLQuery(Map<String, String> params) throws ParseException, UnsupportedEncodingException {

        List<String> whereConditions = new ArrayList<>();
        for (Map.Entry<String,String> entry: params.entrySet()) {
            StringBuffer buf = new StringBuffer();
            String key   = entry.getKey();
            String value = entry.getValue();
            if (key.equals(POS)) {
                RangeListParameter rlp = RangeListParameter.parse(value, 2);
                if (rlp.getDoubleEntries().size() != 2)
                    throw new ParseException("", 0); //TOOO
                Double ra  = rlp.getDoubleEntries().get(0);
                Double dec = rlp.getDoubleEntries().get(1);
                String size = "1";

                if (params.containsKey(SIZE))
                    size = params.get(SIZE);
                buf.append("CONTAINS(");
                buf.append(paramsToColumns.get(POS));
                buf.append(", CIRCLE('',");
                buf.append(ra);
                buf.append(",");
                buf.append(dec);
                buf.append(",");
                buf.append(size);
                buf.append(")) = 1");
                whereConditions.add(buf.toString());
//            } else if (paramsToColumns.containsKey(key)) {
//                whereCondition.append(paramsToColumns.get(key));
//                whereCondition.append(" = ");
//                whereCondition.append(value);
//                whereConditions.add(whereCondition.toString());
            }
        }

        StringBuffer adqlQuery = new StringBuffer();
        adqlQuery.append("SELECT ");
        if (params.containsKey(TOP))
            adqlQuery.append(" TOP ").append(params.get(TOP)).append(" ");
        adqlQuery.append(selectedColumns)
                 .append(" FROM ")
                 .append(tapTable);
        if (whereConditions.size() > 0) {
            adqlQuery.append(" WHERE ");
            adqlQuery.append(StringUtils.join(whereConditions, " AND "));
        }

        return URLEncoder.encode(adqlQuery.toString(), "ISO-8859-1");
    }
}
