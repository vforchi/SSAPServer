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

import org.apache.http.client.fluent.Request;
import org.eso.asp.ssap.domain.ParameterHandler;
import org.eso.asp.ssap.util.VOTableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.eso.asp.ssap.domain.SSAPConstants.*;

/**
 * This class implements SSAPService by translating SSA requests into ADQL queries
 * and send them to a TAP service. It is instantiated if ssap.use.tap=true
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Service
@Configurable
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class SSAPServiceTAPImpl implements SSAPService {

    private static final Logger log = LoggerFactory.getLogger(SSAPServiceTAPImpl.class);

    @Autowired
    ApplicationContext context;

    @Value("${ssap.tap.timeout:10}")
    private Integer timeoutSeconds;

    @Value("${ssap.tap.url}")
    private String tapURL;

    @Value("${ssap.tap.select.clause:*}")
    public String selectedColumns;

    @Value("${ssap.tap.table:dbo.ssa}")
    public String tapTable;

    @Value("#{${ssap.tap.utype.to.columns:{:}}}")
    public Map<String, String> utypeToColumns;

    @Value("${ssap.maxrec.default:1000}")
    private Integer defaultMaxrec;

    @Value("${ssap.maxrec.max:1000000}")
    private Integer maximumMaxrec;

    private String ssaMetadata;

    private Collection<ParameterHandler> parHandlers;

    @PostConstruct
    public void init() {
        /* if not initialized, map using the UCDs */
        try {
            parHandlers = context.getBeansOfType(ParameterHandler.class).values();

            if (utypeToColumns == null || utypeToColumns.size() == 0) {
                StringBuffer tapRequest = getAdqlURL();

                String query = "SELECT * FROM " + tapTable + " WHERE 1=0";
                tapRequest.append(URLEncoder.encode(query, "ISO-8859-1"));

                String tapResult = Request.Get(tapRequest.toString())
                        .connectTimeout(timeoutSeconds * 1000)
                        .socketTimeout(timeoutSeconds * 1000)
                        .execute().returnContent().asString();

                utypeToColumns = VOTableUtils.getUtypeToColumnsMappingsFromVOTable(tapResult);
                ssaMetadata = VOTableUtils.getSSAMetadata(parHandlers, tapResult);
            }

            for (ParameterHandler handler: parHandlers)
                handler.configure(utypeToColumns);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMetadata() {
        return ssaMetadata;
    }

    @Override
    public String queryData(Map<String, String> params) throws IOException, ParseException {
        StringBuffer tapRequest = getAdqlURL();

        /* query */
        tapRequest.append(createADQLQuery(params));

        /* MAXREC */
        Integer maxrec = Integer.valueOf(params.getOrDefault(MAXREC, defaultMaxrec.toString()));
        if (maxrec > maximumMaxrec)
            throw new ParseException("The maximum value for MAXREC is " + maximumMaxrec, 0);
        else
            tapRequest.append("&MAXREC=").append(maxrec);
        params.remove(MAXREC);

        String tapResult = Request.Get(tapRequest.toString())
                .connectTimeout(timeoutSeconds*1000)
                .socketTimeout(timeoutSeconds*1000)
                .execute().returnContent().asString();

        return VOTableUtils.convertTAPtoSSAP(tapResult);
    }

    protected String createADQLQuery(Map<String, String> params) throws ParseException, UnsupportedEncodingException {

        /* build query */
        StringBuffer adqlQuery = new StringBuffer();
        /* SELECT */
        adqlQuery.append("SELECT ");
        /* TOP */
        if (params.containsKey(TOP))
            adqlQuery.append(" TOP ").append(params.get(TOP)).append(" ");
        /* FROM */
        adqlQuery.append(selectedColumns)
                 .append(" FROM ")
                 .append(tapTable);
        /* WHERE */
        List<String> whereConditions = new ArrayList<>();
        for (ParameterHandler handler: parHandlers)
            whereConditions.add(handler.validateAndGenerateQueryCondition(params));

        if (whereConditions.size() > 0) {
            adqlQuery.append(" WHERE ");
            String whereCondition = whereConditions.stream().filter(Objects::nonNull).collect(Collectors.joining(" AND "));
            adqlQuery.append(whereCondition);
        }

        return URLEncoder.encode(adqlQuery.toString(), "ISO-8859-1");
    }

    private StringBuffer getAdqlURL() {
        StringBuffer buf = new StringBuffer(tapURL);

        buf.append("/sync?LANG=ADQL")
           .append("&FORMAT=votable%2Ftd")
           .append("&REQUEST=doQuery")
           .append("&QUERY=");

        return buf;
    }

}
