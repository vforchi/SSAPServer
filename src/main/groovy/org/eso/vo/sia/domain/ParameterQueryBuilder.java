package org.eso.vo.siap.domain;

import java.util.List;

public interface ParameterQueryBuilder {

    String buildQuery(List<String> parameters);

    String buildQuery(String parameter);

}
