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
import org.springframework.boot.json.JsonParserFactory;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Object representing a range-list parameter, as defined in chapter 8.7.2
 * of the SSA specifications:
 *
 * http://www.ivoa.net/documents/SSA/20120210/REC-SSA-1.1-20120210.htm
 *
 * NOTE: steps are currently not implemented
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 *
 */
public class RangeListParameter {

    private List<Pair<Double, Double>> rangeEntries = new ArrayList<>();
    private List<Double> doubleEntries = new ArrayList<>();
    private List<String> stringEntries = new ArrayList<>();
    private final String qualifier;

    public RangeListParameter(List<Pair<Double, Double>> rangeEntries, List<Double> doubleEntries, List<String> stringEntries, String qualifier) {
        this.rangeEntries = rangeEntries;
        this.doubleEntries = doubleEntries;
        this.stringEntries = stringEntries;
        this.qualifier = qualifier;
    }

    /**** GETTERS ****/
    public int getNumEntries() {
        return rangeEntries.size() + doubleEntries.size() + stringEntries.size();
    }

    public List<Pair<Double, Double>> getRangeEntries() {
        return rangeEntries;
    }

    public List<String> getStringEntries() {
        return stringEntries;
    }

    public List<Double> getDoubleEntries() {
        return doubleEntries;
    }

    public String getQualifier() {
        return qualifier;
    }

    /*** Static methods to build the object from a String ****/
    public static RangeListParameter parse(String par) throws ParseException {
        return parse(par, null);
    }

    public static RangeListParameter parse(String par, Integer length) throws ParseException {

        String qualifier = null;
        if (par.contains(";")) {
            String[] tokens = par.split(";");
            if (tokens.length > 2)
                throw new RuntimeException(""); // TODO
            qualifier = tokens[1];
            par = tokens[0];
        }

        List<Pair<Double, Double>> rangeEntries = new ArrayList<>();
        List<Double> doubleEntries = new ArrayList<>();
        List<String> stringEntries = new ArrayList<>();
        String[] entries = par.split(",");
        if (length != null && entries.length != length)
            throw new ParseException("Wrong length in range list: expected " + length + ", found " + entries.length, 0);
        else {
            for (String entry: entries) {
                if (entry.contains("/")) {
                    String[] tokens = entry.split("/");
                    if (tokens.length == 2) {
                        try {
                            List<Double> items = Arrays.stream(tokens).map(Double::valueOf).collect(Collectors.toList());
                            rangeEntries.add(new ImmutablePair<>(items.get(0), items.get(1)));
                        } catch (NumberFormatException e) {
                            throw new ParseException("", 0); // TODO
                        }
                    } else if (tokens.length == 3) {
                        // TODO
                    } else {
                        // TODO
                    }
                } else {
                    try {
                        doubleEntries.add(Double.parseDouble(entry));
                    } catch (NumberFormatException e) {
                        stringEntries.add(entry);
                    }
                }
            }
        }
        RangeListParameter param = new RangeListParameter(Collections.unmodifiableList(rangeEntries),
                                                        Collections.unmodifiableList(doubleEntries),
                                                        Collections.unmodifiableList(stringEntries),
                                                        qualifier);
        return param;
    }

    /**
     * Created by vforchi on 05/04/17.
     */
    public static class ParametersMappings {

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
            List<Map>  metadata = (List) jsonObj.get("metadata");
            List<List> columns  = (List) jsonObj.get("data");
            int idxName = -1, idxUtype = -1;
            for (int i = 0; i < metadata.size(); i++) {
                Map entry = (Map) metadata.get(i);
                if (entry.get("name").equals("column_name"))
                    idxName = i;
                else if (entry.get("name").equals("utype"))
                    idxUtype = i;
            }

            Map<String, Object> res = new HashMap<>();
            for (List column: columns) {
                if (utypes.get(POS).equals(column.get(idxUtype)))
                    res.put(POS, column.get(idxName));
            }
            return res;
        }

    }
}
