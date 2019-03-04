package org.eso.vo.ssap.util;

public class QueryUtils {

    public static String withinParentheses(String query) {
        StringBuffer buf = new StringBuffer("(").append(query).append(")");
        return buf.toString();
    }

}
