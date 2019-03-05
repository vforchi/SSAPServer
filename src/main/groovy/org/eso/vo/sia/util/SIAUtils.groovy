package org.eso.vo.sia.util

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

import groovy.xml.MarkupBuilder

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
