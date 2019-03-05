package org.eso.vo.sia.domain;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.eso.vo.sia.util.SIAUtils;

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
            throw new RuntimeException("Invalid syntax for RANGE: expected 4 parameters (lon1, lat1, lon2, lat2), got " + tokens.size());

        StringBuffer buf = new StringBuffer("s_ra BETWEEN ")
                .append(tokens.get(0))
                .append(" AND ")
                .append(tokens.get(1))
                .append(" AND s_dec BETWEEN ")
                .append(sanitizeDec(tokens.get(2)))
                .append(" AND ")
                .append(sanitizeDec(tokens.get(3)));
        return SIAUtils.withinParentheses(buf.toString());
    }
    
    private String sanitizeDec(String dec) {
        if (dec.equals("+Inf"))
            return "90";
        else if (dec.equals("-Inf"))
            return "-90";
        else
            return dec;
    }


}
