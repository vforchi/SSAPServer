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

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Component
@Configurable
@ConditionalOnMissingBean(name = "myPosHandler")
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class PosHandler extends AbstractHandler {

    public static final String SIZE = "SIZE";

    @Value("${ssap.tap.utype.pos:Char.SpatialAxis.Coverage.Support.Area}")
    void setParamUtype(String paramUtype) {
        this.parUtype = paramUtype;
    }

    @Value("${ssap.tap.description.pos:}")
    void setDescription(String description) { this.parDescription = description; }

    @Value("${ssap.size.default:0.033}")
    private String defaultSize;

    @Value("${ssap.tap.description.size:}")
    private String sizeDescription;

    @Override
    public List<ParameterInfo> getParameterInfos() {
        ParameterInfo sizeParam = new ParameterInfo("SIZE", "char", sizeDescription);
        return Arrays.asList(super.getParameterInfos().get(0), sizeParam);
    }

    public PosHandler() {
        super("POS", "char");
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(parName))
            return null;
        
        String posValue = params.get(parName);
        String size     = params.getOrDefault(SIZE, defaultSize);

        RangeListParameter<Double> rlp = RangeListParameter.parse(posValue, 2, Double::valueOf);
        if (rlp.getSingleEntries().size() != 2)
            throw new ParseException("", 0); //TOOO
        Double ra  = rlp.getSingleEntries().get(0);
        if (ra < 0 || ra > 360)
            throw new ParseException("RA in POS must be between 0 and 360", 0);
        Double dec = rlp.getSingleEntries().get(1);
        if (dec < -90 || dec > 90)
            throw new ParseException("Dec in POS must be between -90 and 90", 0);

        StringBuffer buf = new StringBuffer();
        buf.append("CONTAINS(");
        buf.append(parColumn);
        buf.append(", CIRCLE('',");
        buf.append(ra);
        buf.append(",");
        buf.append(dec);
        buf.append(",");
        buf.append(size);
        buf.append(")) = 1");
        return buf.toString();
    }

}
