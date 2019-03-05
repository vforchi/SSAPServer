package org.eso.vo.sia.domain;

import java.util.List;

public interface ParameterQueryBuilder {

    String buildQuery(List<String> parameters);

    String buildQuery(String parameter);

}
