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

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public class ParameterInfo {

    public ParameterInfo(String name, String value, String dataType, String arraySize, String description) {
        this.name = name;
        this.value = value;
        this.dataType = dataType;
        this.arraySize = arraySize;
        this.description = description;
    }

    public ParameterInfo(String name, String dataType, String description) {
        this.name = name;
        this.value = "";
        this.dataType = dataType;
        this.arraySize = "*";
        this.description = description;
    }

    public final String name;
    public final String value;
    public final String dataType;
    public final String arraySize;
    public final String description;
}
