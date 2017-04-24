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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Component
@Configurable
@ConditionalOnMissingBean(name = "myCreatorDIDHandler")
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class CreatorDIDHandler extends AbstractHandler {

    @Value("${ssap.tap.utype.creatordid:DataID.CreatorDID}")
    void setParamUtype(String paramUtype) {
        this.parUtype = paramUtype;
    }

    @Value("${ssap.tap.description.creatordid:}")
    void setDescription(String description) { this.parDescription = description; }

    public CreatorDIDHandler() {
        super("CREATORDID", "char");
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(parName))
            return null;

        String value = params.get(parName);
        return parColumn + " = '" + value + "'";
    }
}
