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

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Component
@Configurable
@ConditionalOnMissingBean(name = "myBandHandler")
@ConditionalOnProperty(value="ssap.use.tap", havingValue = "true")
public class BandHandler implements ParameterHandler {

    public static final String BAND = "BAND";

    @Value("${ssap.tap.utype.band.start:Char.SpectralAxis.Coverage.Bounds.Start}")
    String bandStartUtype;

    @Value("${ssap.tap.utype.band.stop:Char.SpectralAxis.Coverage.Bounds.Stop}")
    String bandStopUtype;

    String bandStartColumn, bandStopColumn;
    
    private final ParameterInfo bandParam = new ParameterInfo("BAND", "char", "");

    @Override
    public List<ParameterInfo> getParameterInfos() {
        return Arrays.asList(bandParam);
    }

    @Override
    public void configure(Map<String, String> utypeToColumns) throws ConfigurationException {
        bandStartColumn = utypeToColumns.getOrDefault(bandStartUtype, null);
        bandStopColumn = utypeToColumns.getOrDefault(bandStopUtype, null);
        if (bandStartColumn == null || bandStopColumn == null)
            throw new ConfigurationException("Couldn't find mapping for parameter BAND");
    }

    @Override
    public String validateAndGenerateQueryCondition(Map<String, String> params) throws ParseException {
        if (!params.containsKey(BAND))
            return null;

        String bandValue = params.get(BAND);

        List<String> conditions = new ArrayList<>();

        RangeListParameter<Object> rlp = RangeListParameter.parse(bandValue);
        if (rlp.getQualifier() != null && !rlp.getQualifier().equals("observer"))
            throw new ParseException("BAND qualifier " + rlp.getQualifier() + " is not supported", 0);

        for (Object val: rlp.getSingleEntries()) {
            if (val instanceof Double)
                conditions.add(bandStartColumn + " <= " + val + " AND " + bandStopColumn + " >= " + val);
            else if (val instanceof String) {
                // TODO
            } else
                throw new ParseException("Unsupported value " + val + " in BAND", 0);
        }

        for (Pair<Object,Object> range: rlp.getRangeEntries()) {
            if (range.getLeft() instanceof String || range.getRight() instanceof String)
                throw new ParseException("Unsupported range " + range + " in BAND", 0);
            else {
                if (range.getLeft() == null)
                    conditions.add(bandStartColumn + " <= " + range.getRight());
                else if (range.getRight() == null)
                    conditions.add(bandStopColumn + " >= " + range.getLeft());
                else
                    conditions.add(bandStartColumn + " <= " + range.getRight() + " AND " + bandStopColumn + " >= " + range.getLeft());
            }
        }

        return String.join(" OR ", conditions);
    }
}
