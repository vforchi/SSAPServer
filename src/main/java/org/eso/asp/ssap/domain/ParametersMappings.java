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

import org.springframework.boot.json.JsonParserFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public class ParametersMappings {

    public static final String QUERY_DATA = "queryData";

    public static final String MAXREC = "MAXREC";
    public static final String TOP  = "TOP";

    public static final String POS  = "POS";
    public static final String SIZE = "SIZE";

    public static final Map<String, String> utypes;
    static {
        Map<String, String> tempUtypes = new HashMap<>();
        tempUtypes.put(POS, "obscore:Char.SpatialAxis.Coverage.Support.Area");
        utypes = Collections.unmodifiableMap(tempUtypes);
    }

    public static Map<String, Object> getParameterMappings(String jsonContent) {
        Map jsonObj = JsonParserFactory.getJsonParser().parseMap(jsonContent);
        List<Map> metadata = (List) jsonObj.get("metadata");
        List<List> columns  = (List) jsonObj.get("data");
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
        return res;
    }

}
