package org.eso.vo.sia.util

import groovy.xml.MarkupBuilder;

class SIAUtils {

    static String withinParentheses(String query) {
        return "($query)"
    }

    static String formatError(String error) {
        def writer = new StringWriter()
        def doc    = new MarkupBuilder(writer)
        doc.setDoubleQuotes(true)

        doc.VOTABLE(version: "1.3") {
            RESOURCE(type: "results") {
                INFO(name: "QUERY_STATUS", value: "ERROR", error)
                INFO(name: "SERVICE_PROTOCOL", value: "2.0", "SIA")
                INFO(name: "REQUEST", value:"queryData")
            }
        }
        return writer.toString()
    }

}
