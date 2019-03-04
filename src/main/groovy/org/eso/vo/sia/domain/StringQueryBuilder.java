package org.eso.vo.siap.domain;

import org.eso.vo.ssap.util.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

public class StringQueryBuilder implements  ParameterQueryBuilder {

    String column;

    public StringQueryBuilder(String column) {
        this.column = column;
    }

    @Override
    public String buildQuery(List<String> parameters) {
        String query;

        if (parameters.size() == 1)
            query = buildQuery(parameters.get(0));
        else {
            query = column + " IN (" +
                    parameters.stream()
                            .map(p -> "'" + p + "'")
                            .collect(Collectors.joining(", "))
                    + ")";
            query = QueryUtils.withinParentheses(query);
        }

        return query;
    }

    @Override
    public String buildQuery(String parameter) {
        return QueryUtils.withinParentheses(column + " = '" + parameter + "'");
    }

}
