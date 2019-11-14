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
import org.apache.commons.lang3.StringUtils;
import org.eso.vo.sia.util.SIAUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PosQueryBuilder implements  ParameterQueryBuilder {

    enum Shape {
        CIRCLE, RANGE, POLYGON;
    }

    String column;

    public PosQueryBuilder(String column) {
        this.column = column;
    }

    @Override
    public String buildQuery(List<String> parameters) {
        String query = parameters.stream()
                .map(p -> buildQuery(p))
                .collect(Collectors.joining(" OR "));

        return SIAUtils.withinParentheses(query);
    }

    @Override
    public String buildQuery(String parameter) {
        List<String> tokens = Arrays.asList(parameter.split(" "));
        Shape shape = EnumUtils.getEnum(Shape.class, tokens.get(0));
        if (shape == null)
            throw new RuntimeException("Unsupported shape in " + tokens);

        List params = tokens.subList(1, tokens.size());
        switch (shape) {
            case CIRCLE:
                return buildCircle(params);
            case RANGE:
                return buildRange(params);
            case POLYGON:
                return buildPolygon(params);
            default:
                throw new RuntimeException("Unsupported shape in " + tokens);
        }
    }

    private String buildCircle(List<String> tokens) {
        if (tokens.size() != 3)
            throw new RuntimeException("Invalid syntax for CIRCLE: expected 3 parameters (lon, lat, radius), got " + tokens.size());

        return "INTERSECTS(s_region, CIRCLE('', " + StringUtils.join(tokens, ", ") + ")) = 1";
    }

    private String buildPolygon(List<String> tokens) {
        if (tokens.size() < 6 || tokens.size() % 2 != 0)
            throw new RuntimeException("Invalid syntax for POLYGON: expected an even number of parameters (lon1, lat1, lon2, lat2, lon3, lat3...), got " + tokens.size());
        StringBuffer buf = new StringBuffer("INTERSECTS(s_region, POLYGON('', ")
                        .append(tokens.stream().collect(Collectors.joining(", ")))
                        .append(")) = 1");
        return buf.toString();
    }

    private String buildRange(List<String> tokens) {
        if (tokens.size() != 4)
            throw new RuntimeException("Invalid syntax for RANGE: expected 4 parameters (lon1, lon2, lat1, lat2), got " + tokens.size());

        Double ra1 = sanitizeRa(tokens.get(0));
        Double ra2 = sanitizeRa(tokens.get(1));
        if (ra2 <= ra1)
            throw new RuntimeException("Invalid syntax for RANGE: lon2 must be larger than lon1");
        Double dec1 = sanitizeDec(tokens.get(2));
        Double dec2 = sanitizeDec(tokens.get(3));
        if (dec2 < dec1)
            throw new RuntimeException("Invalid syntax for RANGE: lat2 cannot be smaller than lat1");

        if (ra1 == 0 && ra2 == 360)
            return String.format("(s_dec BETWEEN %f AND %f)", dec1, dec2);
        if (dec1 == -90 && dec2 == 90)
            return String.format("(s_ra BETWEEN %f AND %f)", ra1, ra2);

        StringBuffer buf = new StringBuffer("INTERSECTS(s_region, POLYGON('', ");

        List<Double> coordinates = new ArrayList();

        coordinates.add(ra1);
        coordinates.add(dec1);
        coordinates.add(ra1);
        coordinates.add(dec2);

        int pointsPerSide = Math.max(10, (int) Math.floor((ra2-ra1)*2));
        pointsPerSide = Math.min(pointsPerSide, 100);

        if (dec2 != 90) {
            for (int i = 1; i < pointsPerSide; i++) {
                coordinates.add(ra1 + (ra2 - ra1) * i / pointsPerSide);
                coordinates.add(dec2);
            }
        }

        coordinates.add(ra2);
        coordinates.add(dec2);
        coordinates.add(ra2);
        coordinates.add(dec1);

        if (dec1 != -90) {
            for (int i = 1; i < pointsPerSide; i++) {
                coordinates.add(ra2 - (ra2 - ra1) * i / pointsPerSide);
                coordinates.add(dec1);
            }
        }

        buf.append(StringUtils.join(coordinates, ","));

        buf.append(")) = 1");
        
        return buf.toString();
    }
    
    private Double sanitizeDec(String decString) {
        Double dec;
        if (decString.equals("+Inf"))
            dec = 90.0;
        else if (decString.equals("-Inf"))
            dec = -90.0;
        else
            dec = Double.parseDouble(decString);

        if (dec > 90.0 || dec < -90.0)
            throw new RuntimeException("Latitude must be within -90 and 90");

        return dec;
    }

    private Double sanitizeRa(String raString) {
        Double ra = Double.parseDouble(raString);
        if (ra > 360.0 || ra < 0.0)
            throw new RuntimeException("Longitude must be within 0 and 360");
        return ra;
    }
    
}
