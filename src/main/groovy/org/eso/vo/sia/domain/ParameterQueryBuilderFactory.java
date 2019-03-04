package org.eso.vo.siap.domain;

import org.apache.commons.lang3.EnumUtils;

public class ParameterQueryBuilderFactory {

    public static ParameterQueryBuilder getBuilder(String name) {
        return getBuilder(EnumUtils.getEnum(SIAPParameter.class, name));
    }

    public static ParameterQueryBuilder getBuilder(SIAPParameter parameter) {
        if (parameter == null)
            return null;

        switch (parameter.getType()) {
            case STRING:
                return new StringQueryBuilder(parameter.getColumns()[0]);
            case NUMBER:
                return new NumberQueryBuilder(parameter.getColumns());
            case POL:
                return new PolQueryBuilder(parameter.getColumns()[0]);
            case POS:
                return new PosQueryBuilder(parameter.getColumns()[0]);
            default:
                throw new RuntimeException("Unsupported parameter type " + parameter.getType());
        }
    }


}
