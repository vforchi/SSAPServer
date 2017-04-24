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
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public abstract class AbstractHandler extends ParameterHandler {

    String parUtype;

    String parColumn;

    protected AbstractHandler(String parName, String parDatatype) {
        this.parName     = parName;
        this.parDatatype = parDatatype;
    }

    @Override
    public void configure(Map<String, String> utypeToColumns) throws ConfigurationException {
        parColumn = utypeToColumns.getOrDefault(parUtype, null);
        if (parColumn == null)
            throw new ConfigurationException("Couldn't find mapping for parameter " + parName);
    }

}
