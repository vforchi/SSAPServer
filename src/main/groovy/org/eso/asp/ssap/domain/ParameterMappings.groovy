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
import java.text.ParseException

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
    public static final Map<String, Object> utypes = [(POS): "ssa:Char.SpatialAxis.Coverage.Support.Area",
                                                      (TIME): ["ssa:Char.TimeAxis.Coverage.Bounds.Start", "ssa:Char.TimeAxis.Coverage.Bounds.Stop"]].asImmutable()

    public static Map<String, Object> parseFromJSON(String jsonContent) throws ParseException {
        try {
            def json = new JsonSlurper().parseText(jsonContent)

            int idxName  = json.metadata.findIndexOf { it.name == 'column_name'}
            int idxUtype = json.metadata.findIndexOf { it.name == 'utype'}

            def res = utypes.collectEntries { ssaPar, utype ->
                if (utype instanceof String) {
                    def column = json.data.find { it[idxUtype] == utype}
                    [(ssaPar): column[idxName]]
                } else if (utype instanceof List) {
                    /* we start from the utype instead of columns.findAll to preserve the order of the utype */
                    def cols = utype.collect { type -> json.data.find { it[idxUtype] == type } }
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
                    def col = columns.find { it.TD[idxUtype].text() == utype }
                    [(ssaPar): col.TD[idxName].text()]
                } else if (utype instanceof List) {
                    /* we start from the utype instead of columns.findAll to preserve the order of the utype */
                    def cols = utype.collect { type -> columns.find { it.TD[idxUtype].text() == type } }
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

}
