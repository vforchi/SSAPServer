package org.eso.vo.siap.domain;

import org.eso.vo.ssap.util.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PolQueryBuilder implements  ParameterQueryBuilder {
    String column;

    public PolQueryBuilder(String column) {
        this.column = column;
    }

    @Override
    public String buildQuery(List<String> parameters) {
        String query = parameters.stream()
                .map(p -> buildQuery(p))
                .collect(Collectors.joining(" OR "));

        return QueryUtils.withinParentheses(query);
    }

    @Override
    public String buildQuery(String parameter) {
        return column + " LIKE '%/" + parameter + "/%'";
    }

}
