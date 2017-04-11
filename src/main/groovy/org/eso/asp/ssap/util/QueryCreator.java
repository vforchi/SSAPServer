package org.eso.asp.ssap.util;

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

import org.apache.commons.lang3.tuple.Pair;
import org.eso.asp.ssap.domain.ParameterMappings;
import org.eso.asp.ssap.domain.RangeListParameter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.eso.asp.ssap.domain.ParameterMappings.stringToMjdObsInterval;

/**
 * This class creates SQL WHERE conditions based on the input parameters
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Configurable
@Component
public class QueryCreator {

    /* static members cannot be initialized with @Value directly */
    private static String defaultSize;
    
    @Value("${ssap.size.default:1}")
    public void setDefaultSize(String size) {
        defaultSize = size;
    }

    public static String createPosQuery(String posColumn, String pos, String size) throws ParseException {
        if (size == null)
            size = defaultSize;

        RangeListParameter<Double> rlp = RangeListParameter.parse(pos, 2, Double::valueOf);
        if (rlp.getSingleEntries().size() != 2)
            throw new ParseException("", 0); //TOOO
        Double ra = rlp.getSingleEntries().get(0);
        Double dec = rlp.getSingleEntries().get(1);

        StringBuffer buf = new StringBuffer();
        buf.append("CONTAINS(");
        buf.append(posColumn);
        buf.append(", CIRCLE('',");
        buf.append(ra);
        buf.append(",");
        buf.append(dec);
        buf.append(",");
        buf.append(size);
        buf.append(")) = 1");
        return buf.toString();
    }

    private static String generateTimeCondition(List columns, Double start, Double end) {
        /*
         * If we have two intervals x1-x2 and y1-y2, they intersect if
         * x1 <= y2 and x2 <= y1
         */
        if (start == null) {
            return columns.get(0).toString() + " <= " + end;
        } else if (end == null) {
            return columns.get(1).toString() + " >= " + start;
        } else {
            return columns.get(0).toString() + " <= " + end + " AND " + columns.get(1).toString() + " >= " + start;
        }
    }

    public static String createTimeQuery(List columns, String value) throws ParseException {
        List<String> conditions = new ArrayList<>();

        RangeListParameter<String> rlp = RangeListParameter.parse(value, RangeListParameter.STRING_CONVERTER);

        for (String val: rlp.getSingleEntries()) {
            Pair<Double, Double> interval = ParameterMappings.stringToMjdObsInterval(val);
            conditions.add(generateTimeCondition(columns, interval.getLeft(), interval.getRight()));
        }

        for (Pair<String,String> range: rlp.getRangeEntries()) {
            Double start = stringToMjdObsInterval(range.getLeft()).getLeft();
            Double end   = stringToMjdObsInterval(range.getRight()).getRight();
            conditions.add(generateTimeCondition(columns, start, end));
        }

        return String.join(" OR ", conditions);
    }
}
