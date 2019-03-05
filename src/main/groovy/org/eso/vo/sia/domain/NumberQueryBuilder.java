package org.eso.vo.sia.domain;

/*
 * This file is part of SIAPServer.
 *
 * SIAPServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SIAPServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SIAPServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2019 - European Southern Observatory (ESO)
 */

import org.eso.vo.sia.util.SIAUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NumberQueryBuilder implements  ParameterQueryBuilder {

    String[] columns;

    public NumberQueryBuilder(String[] columns) {
        this.columns = columns;
    }

    @Override
    public String buildQuery(List<String> parameters) {
        String query = parameters.stream()
                .map(p -> buildQuery(p))
                .collect(Collectors.joining(" OR "));

        return SIAUtils.withinParentheses(query);
    }

    @Override
    public String buildQuery(String parameter) {
        String[] borders = parameter.split(" ");
        if (borders.length == 1) {
            if (columns.length == 1)
                return columns[0] + " = " + borders[0];
            else
                return buildNumberQueryFragment(columns[0], columns[1], borders[0]);
        } else if (borders.length == 2) {
            if (columns.length == 1)
                return buildRangeQueryFragment(columns[0], borders[0], borders[1]);
            else
                return buildRangeQueryFragment(columns[0], columns[1], borders[0], borders[1]);
        } else {
            throw new RuntimeException("Invalid parameter: [" + parameter + "]");
        }
    }

    private String buildNumberQueryFragment(String column_min, String column_max, String border) {
        StringBuffer buf = new StringBuffer();
        buf.append(column_min);
        buf.append(" <= ");
        buf.append(border);
        buf.append(" AND ");
        buf.append(column_max);
        buf.append(" >= ");
        buf.append(border);
        return SIAUtils.withinParentheses(buf.toString());
    }

    private String buildRangeQueryFragment(String column, String min, String max) {
        String query;
        if (min.equals("-Inf") && max.equals("+Inf"))
            query = column + " IS NOT NULL ";
        else if (max.equals("+Inf"))
            query = column + " >= " + min;
        else if (min.equals("-Inf"))
            query = column + " <= " + max;
        else {
            try {
                Double.valueOf(min);
                Double.valueOf(max);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }

            query = column + " BETWEEN " + min + " AND " + max;
        }

        return query;
    }

    private String buildRangeQueryFragment(String column_min, String column_max, String min, String max) {
        String query;
        if (min.equals("-Inf") && max.equals("+Inf"))
            query = column_min + " IS NOT NULL ";
        else if (max.equals("+Inf"))
            query = column_max + " >= " + min;
        else if (min.equals("-Inf"))
            query = column_min + " <= " + max;
        else {
            try {
                Double.valueOf(min);
                Double.valueOf(max);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }

            // [a, b] and [c, d] overlap if c < b && d > a
            StringBuffer buf = new StringBuffer();
            buf.append(min);
            buf.append(" <= ");
            buf.append(column_max);
            buf.append(" AND ");
            buf.append(max);
            buf.append(" >= ");
            buf.append(column_min);
            query = SIAUtils.withinParentheses(buf.toString());
        }

        return query;
    }
}
