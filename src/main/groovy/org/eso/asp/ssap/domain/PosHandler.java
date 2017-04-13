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

import javax.naming.ConfigurationException;
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
public class PosHandler implements ParameterHandler {

    public static final String POS  = "POS";
    public static final String SIZE = "SIZE";

    @Value("${ssap.tap.utype.pos:Char.SpatialAxis.Coverage.Support.Area}")
    String posUtype;

    String posColumn;

    @Value("${ssap.size.default:0.033}")
    private String defaultSize;

    private final String posDoc = "Search Position in the form ra,dec where ra and dec are given in decimal degrees" +
            " in the (FK5 2000/ICRS) coordinate system. Currently the reference frame format modifier is not " +
            "supported, nor are multiple sets of ra,dec values.";
    private final ParameterInfo posParam  = new ParameterInfo("POS", "char", posDoc);
    private final ParameterInfo sizeParam = new ParameterInfo("SIZE", "char", "Search diameter in decimal degrees. Default = 0.033 degrees.");

    @Override
    public List<ParameterInfo> getParameterInfos() {
        return Arrays.asList(posParam, sizeParam);
    }

    @Override
    public void configure(Map<String, String> utypeToColumns) throws ConfigurationException {
        posColumn = utypeToColumns.getOrDefault(posUtype, null);
        if (posColumn == null)
            throw new ConfigurationException("Couldn't find mapping for parameter POS");
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(POS))
            return null;
        
        String posValue = params.get(POS);
        String size     = params.getOrDefault(SIZE, defaultSize);

        RangeListParameter<Double> rlp = RangeListParameter.parse(posValue, 2, Double::valueOf);
        if (rlp.getSingleEntries().size() != 2)
            throw new ParseException("", 0); //TOOO
        Double ra = rlp.getSingleEntries().get(0);
        Double dec = rlp.getSingleEntries().get(1);

        StringBuffer buf = new StringBuffer();
        buf.append("CONTAINS(");
        buf.append(posColumn);
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
