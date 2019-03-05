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

import org.apache.commons.lang3.EnumUtils;

public class ParameterQueryBuilderFactory {

    public static ParameterQueryBuilder getBuilder(String name) {
        return getBuilder(EnumUtils.getEnum(SIAParameter.class, name));
    }

    public static ParameterQueryBuilder getBuilder(SIAParameter parameter) {
        if (parameter == null)
            return null;

        switch (parameter.getType()) {
            case STRING:
                return new StringQueryBuilder(parameter.getColumns()[0]);
            case NUMBER:
                return new NumberQueryBuilder(parameter.getColumns());
            case POL:
                return new PolQueryBuilder(parameter.getColumns()[0]);
            case POS:
                return new PosQueryBuilder(parameter.getColumns()[0]);
            default:
                throw new RuntimeException("Unsupported parameter type " + parameter.getType());
        }
    }


}
