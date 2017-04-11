package org.eso.asp.ssap.domain

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

import groovy.json.JsonSlurper
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair

import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.JulianFields
import java.time.temporal.Temporal

/**
 * This class contains definitions, contants and methods to define mappings between
 * the input parameters in SSA and the columns in TAP, based on the utypes defined
 * in the standard.
 * 
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public class ParameterMappings {

    public static final String QUERY_DATA = "queryData"

    public static final String MAXREC = "MAXREC"
    public static final String TOP  = "TOP"

    /* mandatory parameters */
    public static final String POS    = "POS"
    public static final String SIZE   = "SIZE"
    public static final String BAND   = "BAND"
    public static final String TIME   = "TIME"
    public static final String FORMAT = "FORMAT"

    /**
     * utypes associated to the input parameters in SSA
     */
    public static final Map<String, Object> utypes = [(POS): "Char.SpatialAxis.Coverage.Support.Area",
                                                      (TIME): ["Char.TimeAxis.Coverage.Bounds.Start", "Char.TimeAxis.Coverage.Bounds.Stop"],
                                                      ].asImmutable()

    public static Map<String, Object> parseFromJSON(String jsonContent) throws ParseException {
        try {
            def json = new JsonSlurper().parseText(jsonContent)

            int idxName  = json.metadata.findIndexOf { it.name == 'column_name'}
            int idxUtype = json.metadata.findIndexOf { it.name == 'utype'}

            def res = utypes.collectEntries { ssaPar, utype ->
                if (utype instanceof String) {
                    def column = json.data.find { it[idxUtype]?.endsWith(utype) }
                    [(ssaPar): column[idxName]]
                } else if (utype instanceof List) {
                    /* we start from the utype instead of columns.findAll to preserve the order of the utype */
                    def cols = utype.collect { type -> json.data.find { it[idxUtype]?.endsWith(type) } }
                    [(ssaPar): cols.collect { it[idxName] }]
                }
            }

            if (res.size() != utypes.size())
                throw new ParseException("Couldn't find all necessary columns", 0)

            return res
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0)
        }
    }

    public static Map<String, Object> parseFromXML(String xmlContent) throws ParseException {
        try {
            def VOTABLE = new XmlParser().parseText(xmlContent)

            /* find indices of columns containing utype and column_name in the table */
            NodeList fields = VOTABLE.RESOURCE.TABLE.FIELD
            int idxName  = fields.findIndexOf { it.@name == 'column_name'}
            int idxUtype = fields.findIndexOf { it.@name == 'utype'}

            /* data of the table, containing the SSA columns */
            NodeList columns = VOTABLE.RESOURCE.TABLE.DATA.TABLEDATA.TR

            /* return a map containing:
               key: the input parameter in SSA
               value: the name of the column with the required utype */
            def res = utypes.collectEntries { ssaPar, utype ->
                if (utype instanceof String) {
                    def col = columns.find { it.TD[idxUtype].text()?.endsWith(utype) }
                    [(ssaPar): col.TD[idxName].text()]
                } else if (utype instanceof List) {
                    /* we start from the utype instead of columns.findAll to preserve the order of the utype */
                    def cols = utype.collect { type -> columns.find { it.TD[idxUtype].text()?.endsWith(type) } }
                    [(ssaPar): cols.collect { it.TD[idxName].text() }]
                }
            }

            if (res.size() != utypes.size())
                throw new ParseException("Couldn't find all necessary columns", 0)

            return res
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0)
        }
    }

    private static Double toMjd(Temporal time) {
        Double mjd = time.getLong(JulianFields.MODIFIED_JULIAN_DAY)
        mjd += time.getLong(ChronoField.SECOND_OF_DAY) / 86400
        return mjd
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
            return new ImmutablePair<Double, Double>(null, null);

        def inputTime = time

        /* if the string is not complete to the second, add the missing part
           and define the width of the interval */
        def unit = ChronoUnit.SECONDS
        if (time ==~ /\d{4}/) {
            unit = ChronoUnit.YEARS
            time += "-01-01T00:00:00"
        } else if (time ==~ /\d{4}-\d{2}/) {
            unit = ChronoUnit.MONTHS
            time += "-01T00:00:00"
        } else if (time ==~ /\d{4}-\d{2}-\d{2}/) {
            unit = ChronoUnit.DAYS
            time += "T00:00:00"
        }

        try {
            def formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            def timeStart = LocalDateTime.parse(time, formatter)
            def timeEnd   = timeStart.plus(1, unit)
            return new ImmutablePair<Double, Double>(toMjd(timeStart), toMjd(timeEnd))
        } catch (Exception e4) {
            throw new ParseException("TIME string $inputTime is not valid", 0)
        }
    }

}
