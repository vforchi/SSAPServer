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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Component
@Configurable
@ConditionalOnMissingBean(name = "mySpatResHandler")
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class SpatResHandler extends MinValueHandler {

    @Value("${ssap.tap.utype.spatres:Char.SpatialAxis.Resolution}")
    void setParamUtype(String paramUtype) {
        this.parUtype = paramUtype;
    }

    @Value("${ssap.tap.description.spatres:}")
    void setDescription(String description) { this.parDescription = description; }

    public SpatResHandler() {
        super("SPATRES", "float");
    }

}
