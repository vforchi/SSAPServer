package org.eso.asp.ssap.domain

import java.text.ParseException

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
    public static final String TOP    = "TOP"

    public static Map<String, String> getUtypeToColumnsMappingsFromVOTable(String xmlContent) throws ParseException {
        try {
            def VOTABLE = new XmlParser().parseText(xmlContent)

            /* find indices of columns containing utype and column_name in the table */
            NodeList fields = VOTABLE.RESOURCE.TABLE.FIELD
            int idxName  = fields.findIndexOf { it.@name == 'column_name'}
            int idxUtype = fields.findIndexOf { it.@name == 'utype'}

            /* data of the table, containing the SSA columns */
            NodeList columns = VOTABLE.RESOURCE.TABLE.DATA.TABLEDATA.TR

            return columns.collectEntries {
                [it.TD[idxUtype].text().replaceFirst(".*:", ""), it.TD[idxName].text()]
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0)
        }
    }


}
