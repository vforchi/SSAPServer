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

import java.text.ParseException;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public abstract class MinValueHandler extends AbstractHandler {

    protected MinValueHandler(String paramName, String datatype) {
        super(paramName, datatype);
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(parName))
            return null;

        String value = params.get(parName);
        try {
            Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ParseException("Cannot convert " + value + " to a float", 0);
        }

        return parColumn + " > " + value;
    }
}
