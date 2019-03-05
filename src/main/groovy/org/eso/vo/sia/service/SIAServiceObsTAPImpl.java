package org.eso.vo.sia.service;

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
 * Copyright 2019 - European Southern Observatory (ESO)
 */

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.eso.vo.dali.domain.DALIConstants;
import org.eso.vo.sia.domain.ParameterQueryBuilder;
import org.eso.vo.sia.domain.ParameterQueryBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class implements SIAService by translating SIA requests into ADQL queries
 * and send them to a TAP service. It is instantiated if sia.use.tap=true
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Service
@Configurable
@ConditionalOnProperty(value="sia.enabled", havingValue = "true")
public class SIAServiceObsTAPImpl implements SIAService {

    private static final Logger log = LoggerFactory.getLogger(SIAServiceObsTAPImpl.class);

    @Value("${sia.tap.timeout:10}")
    private Integer timeoutSeconds;

    @Value("${sia.tap.url}")
    private String tapURL;

    @Value("${sia.maxrec.default:1000}")
    private Integer defaultMaxrec;

    @Value("${sia.maxrec.max:1000000}")
    private Integer maximumMaxrec;

    @Value("${sia.base.query}")
    private String baseQuery;

    @Override
    public String query(MultiValueMap<String, String> params) throws IOException, ParseException {
        StringBuffer tapRequest = getAdqlURL();

        /* query */
        tapRequest.append(createADQLQuery(params));

        /* MAXREC */
        Integer maxrec = Integer.valueOf(params.getOrDefault(DALIConstants.MAXREC, Arrays.asList(defaultMaxrec.toString())).get(0));
        if (maxrec > maximumMaxrec)
            throw new ParseException("The maximum value for MAXREC is " + maximumMaxrec, 0);
        else
            tapRequest.append("&MAXREC=").append(maxrec);
        params.remove(DALIConstants.MAXREC);

        log.info("Executing TAP request: {}", tapRequest);

        long start = System.currentTimeMillis();

        String tapResult = Request.Get(tapRequest.toString())
                .connectTimeout(timeoutSeconds*1000)
                .socketTimeout(timeoutSeconds*1000)
                .execute()
                .handleResponse(r -> {
                    return new Content(EntityUtils.toByteArray(r.getEntity()), ContentType.getOrDefault(r.getEntity()));
                }).toString();

        long elapsed = System.currentTimeMillis() - start;

        log.info("TAP request executed in {}ms", elapsed);

        return tapResult;
    }

    protected StringBuffer getAdqlURL() {
        StringBuffer buf = new StringBuffer(tapURL);

        buf.append("/sync?LANG=ADQL")
                .append("&FORMAT=votable%2Ftd")
                .append("&REQUEST=doQuery")
                .append("&QUERY=");

        return buf;
    }

    protected String createADQLQuery(MultiValueMap<String, String> params) throws UnsupportedEncodingException {

        StringBuffer adqlQuery = new StringBuffer(baseQuery);

        String where = params.keySet().stream()
                .map( p -> {
                    ParameterQueryBuilder builder = ParameterQueryBuilderFactory.getBuilder(p);
                    if (builder != null)
                        return builder.buildQuery(params.get(p));
                    else
                        return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" AND "));

        if (where.length() > 0) {
            adqlQuery.append( " AND ");
            adqlQuery.append(where);
        }

        return URLEncoder.encode(adqlQuery.toString(), "ISO-8859-1");

    }

}

