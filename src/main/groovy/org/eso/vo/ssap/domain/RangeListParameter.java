package org.eso.vo.ssa.domain;

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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
public class RangeListParameter<T> {

    private List<Pair<T, T>> rangeEntries = new ArrayList<>();
    private List<T> singleEntries = new ArrayList<>();
    private final String qualifier;

    public RangeListParameter(List<Pair<T, T>> rangeEntries, List<T> singleEntries, String qualifier) {
        this.rangeEntries = rangeEntries;
        this.singleEntries = singleEntries;
        this.qualifier = qualifier;
    }

    /**** GETTERS ****/
    public int getNumEntries() {
        return rangeEntries.size() + singleEntries.size();
    }

    public List<Pair<T, T>> getRangeEntries() {
        return rangeEntries;
    }

    public List<T> getSingleEntries() {
        return singleEntries;
    }

    public String getQualifier() {
        return qualifier;
    }

    /*** Static methods to build the object from a String ****/
    private static class DefaultConversion implements Function<String, Object> {
        @Override
        public Object apply(String s) {
            if (s == null || s.length() == 0)
                return null;
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                return s;
            }
        }
    }

    private static class DoubleConversion implements Function<String, Double> {
        @Override
        public Double apply(String s) {
            if (s == null || s.length() == 0)
                return null;
            return Double.valueOf(s);
        }
    }

    private static class StringConversion implements Function<String, String> {
        @Override
        public String apply(String s) {
            if (s.length() == 0)
                return null;
            return s;
        }
    }

    public static Function<String, Object> DEFAULT_CONVERTER = new DefaultConversion();
    public static Function<String, Double> DOUBLE_CONVERTER = new DoubleConversion();
    public static Function<String, String> STRING_CONVERTER = new StringConversion();

    public static RangeListParameter<Object> parse(String par) throws ParseException {
        return parse(par, null, DEFAULT_CONVERTER);
    }

    public static RangeListParameter<Object> parse(String par, Integer length) throws ParseException {
        return parse(par, length, DEFAULT_CONVERTER);
    }

    public static <S> RangeListParameter<S> parse(String par, Function<String, S> f) throws ParseException {
        return parse(par, null, f);
    }

    /**
     * This method converts a String into a range list parameter
     * @param par the input value
     * @param length the requested length of the list, if null any length is accepted
     * @param function a function to convert the elements into output values
     * @param <S> the class of the elements in the parameter
     * @return a range list parameter
     * @throws ParseException
     */
    public static <S> RangeListParameter<S> parse(String par, Integer length, Function<String, S> function) throws ParseException {

        String qualifier = null;
        if (par.contains(";")) {
            String[] tokens = par.split(";");
            if (tokens.length > 2)
                throw new RuntimeException(""); // TODO
            qualifier = tokens[1];
            par = tokens[0];
        }

        List<Pair<S, S>> rangeEntries = new ArrayList<>();
        List<S> singleEntries = new ArrayList<>();
        String[] entries = par.split(",");
        if (length != null && entries.length != length)
            throw new ParseException("Wrong length in range list: expected " + length + ", found " + entries.length, 0);
        else {
            for (String entry : entries) {
                if ("/".equals(entry))
                    throw new ParseException("Invalid range /", 0);
                else if (entry.contains("/")) {
                    String[] tokens = entry.split("/", -1);
                    if (tokens.length == 2) {
                        try {
                            List<S> items = Arrays.stream(tokens).map(function).collect(Collectors.toList());
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
                        singleEntries.add(function.apply(entry));
                    } catch (NumberFormatException e) {
                        throw new ParseException("Can't convert " + entry, 0);
                    }
                }
            }
        }
        return new RangeListParameter<>(Collections.unmodifiableList(rangeEntries),
                Collections.unmodifiableList(singleEntries),
                qualifier);
    }
}


