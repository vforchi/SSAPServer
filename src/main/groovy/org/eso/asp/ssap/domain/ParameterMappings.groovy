package org.eso.asp.ssap.domain

import org.springframework.boot.json.JsonParserFactory
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
import java.text.ParseException
/**
 * This class contains definitions, contants and methods to define mappings between
 * the input parameters in SSA and the columns in TAP, based on the utypes defined
 * in the standard.
 * 
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public class ParameterMappings {

    public static final String QUERY_DATA = "queryData";

    public static final String MAXREC = "MAXREC";
    public static final String TOP  = "TOP";

    public static final String POS  = "POS";
    public static final String SIZE = "SIZE";

    /**
     * utypes associated to the input parameters in SSA
     */
    public static final Map<String, String> utypes;
    static {
        Map<String, String> tempUtypes = new HashMap<>();
        tempUtypes.put(POS, "ssa:Char.SpatialAxis.Coverage.Support.Area");
        utypes = Collections.unmodifiableMap(tempUtypes);
    }

    public static Map<String, Object> parseFromJSON(String jsonContent) throws ParseException {
        Map jsonObj = JsonParserFactory.getJsonParser().parseMap(jsonContent);
        List<Map> metadata = (List<Map>) jsonObj.get("metadata");
        List<List> columns  = (List<List>) jsonObj.get("data");
        int idxName = -1, idxUtype = -1;
        for (int i = 0; i < metadata.size(); i++) {
            Map entry = metadata.get(i);
            if (entry.get("name").equals("column_name"))
                idxName = i;
            else if (entry.get("name").equals("utype"))
                idxUtype = i;
        }

        Map<String, Object> res = new HashMap<>();
        for (List column: columns) {
            Object utype = column.get(idxUtype);
            if (utype != null) {
                if (utypes.get(POS).equalsIgnoreCase(utype.toString()))
                    res.put(POS, column.get(idxName));
            }
        }
        if (res.size() != 1)
            throw new ParseException("Couldn't find all necessary columns", 0);
        return res;
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
            return utypes.collectEntries { ssaPar, utype ->
                def column = columns.find { it.TD[idxUtype].text() == utype}
                [(ssaPar): column.TD[idxName].text()]
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0)
        }
    }

}
