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

public class PolQueryBuilder implements  ParameterQueryBuilder {
    String column;

    public PolQueryBuilder(String column) {
        this.column = column;
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
        return column + " LIKE '%/" + parameter + "/%'";
    }

}
