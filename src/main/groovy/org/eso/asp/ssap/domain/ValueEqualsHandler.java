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

import javax.naming.ConfigurationException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public abstract class ValueEqualsHandler implements ParameterHandler {

    public final String paramName;

    private final ParameterInfo paramInfo;

    String paramUtype;

    String paramColumn;

    protected ValueEqualsHandler(String paramName, ParameterInfo paramInfo) {
        this.paramName = paramName;
        this.paramInfo = paramInfo;
    }

    @Override
    public List<ParameterInfo> getParameterInfos() {
        return Arrays.asList(paramInfo);
    }

    @Override
    public void configure(Map<String, String> utypeToColumns) throws ConfigurationException {
        paramColumn = utypeToColumns.getOrDefault(paramUtype, null);
        if (paramColumn == null)
            throw new ConfigurationException("Couldn't find mapping for parameter " + paramName);
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(paramName))
            return null;

        String value = params.get(paramName);
        return paramColumn + " = '" + value + "'";
    }
}
