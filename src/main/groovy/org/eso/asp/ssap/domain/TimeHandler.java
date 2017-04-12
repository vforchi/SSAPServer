package org.eso.asp.ssap.domain;

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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Component
@Configurable
@ConditionalOnMissingBean(name = "myTimeHandler")
public class TimeHandler implements ParameterHandler {

    public static final String TIME = "TIME";

    @Value("${ssap.tap.utype.TIME.start:Char.TimeAxis.Coverage.Bounds.Start}")
    String timeStartUtype;

    @Value("${ssap.tap.utype.TIME.end:Char.TimeAxis.Coverage.Bounds.Stop}")
    String timeStopUtype;

    String timeStartColumn, timeEndColumn;

    @Value("${ssap.size.default:1}")
    private String defaultSize;

    @Override
    public void configure(Map<String, String> utypeToColumns) throws ConfigurationException {
        timeStartColumn = utypeToColumns.getOrDefault(timeStartUtype, null);
        timeEndColumn   = utypeToColumns.getOrDefault(timeStopUtype, null);
        if (timeStartColumn == null || timeEndColumn == null)
            throw new ConfigurationException("Couldn't find mapping for parameter TIME");
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(TIME))
            return null;

        String timeValue = params.get(TIME);

        List<String> conditions = new ArrayList<>();

        RangeListParameter<String> rlp = RangeListParameter.parse(timeValue, RangeListParameter.STRING_CONVERTER);

        for (String val: rlp.getSingleEntries()) {
            Pair<Double, Double> interval = stringToMjdObsInterval(val);
            conditions.add(generateTimeCondition(interval.getLeft(), interval.getRight()));
        }

        for (Pair<String,String> range: rlp.getRangeEntries()) {
            Double start = stringToMjdObsInterval(range.getLeft()).getLeft();
            Double end   = stringToMjdObsInterval(range.getRight()).getRight();
            conditions.add(generateTimeCondition(start, end));
        }

        return String.join(" OR ", conditions);
    }

    private String generateTimeCondition(Double start, Double end) {
        /*
         * If we have two intervals x1-x2 and y1-y2, they intersect if
         * x1 <= y2 and x2 <= y1
         */
        if (start == null) {
            return timeStartColumn + " <= " + end;
        } else if (end == null) {
            return timeEndColumn + " >= " + start;
        } else {
            return timeStartColumn + " <= " + end + " AND " + timeEndColumn + " >= " + start;
        }
    }

    /**
     * Convert one of the time instants defined in the TIME input parameter in an interval.
     * For example, if the input is 1999, the method returns two instants representing the
     * beginning and the end of year 1999. For more info, see the definition of TIME in SSA
     *
     * @param time
     * @return a pair containing the interval expressed in MJD
     * @throws ParseException
     */
    public static Pair<Double, Double> stringToMjdObsInterval(String time) throws ParseException {
        if (time == null)
            return new ImmutablePair<>(null, null);

        String inputTime = time;

        /* if the string is not complete to the second, add the missing part
           and define the width of the interval */
        TemporalUnit unit = ChronoUnit.SECONDS;
        if (time.matches("\\d{4}")) {
            unit = ChronoUnit.YEARS;
            time += "-01-01T00:00:00";
        } else if (time.matches("\\d{4}-\\d{2}")) {
            unit = ChronoUnit.MONTHS;
            time += "-01T00:00:00";
        } else if (time.matches("\\d{4}-\\d{2}-\\d{2}")) {
            unit = ChronoUnit.DAYS;
            time += "T00:00:00";
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime timeStart = LocalDateTime.parse(time, formatter);
            LocalDateTime timeEnd   = timeStart.plus(1, unit);
            return new ImmutablePair<>(toMjd(timeStart), toMjd(timeEnd));
        } catch (Exception e) {
            throw new ParseException("TIME string " + inputTime + " is not valid", 0);
        }
    }

    private static Double toMjd(Temporal time) {
        Double mjd = new Double(time.getLong(JulianFields.MODIFIED_JULIAN_DAY));
        mjd += 1.0 / 86400 * time.getLong(ChronoField.SECOND_OF_DAY);
        return mjd;
    }
}
