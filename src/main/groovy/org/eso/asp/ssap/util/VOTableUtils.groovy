package org.eso.asp.ssap.util

/*
 * This file is part of SSAPServer.
 *
 * SSAPServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SSAPServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SSAPServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2017 - European Southern Observatory (ESO)
 */

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.eso.asp.ssap.domain.ParameterHandler
import java.text.ParseException

/**
 * This class contains helper methods to deal with VOTable entity. It's in Groovy because
 * it has powerful XML libraries
 * 
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@Slf4j
public class VOTableUtils {

	/**
	 * This method returns the mapping between utype and column name in the TAP service
	 * @param xmlContent: a VOTable containing the column metadata of the table containing the SSA schema
	 * @return a map, where the key is the utype and the value the column name
	 * @throws ParseException
	 */
    public static Map<String, String> getUtypeToColumnsMappingsFromVOTable(String xmlContent) throws ParseException {
        try {
            def VOTABLE = new XmlSlurper().parseText(xmlContent)

            def fieldsWithUtype = VOTABLE.RESOURCE.TABLE.FIELD.findAll { it.@utype }
            return fieldsWithUtype.collectEntries {
                [it.@utype.text().replaceFirst(".*:", ""), it.@name.text()]
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0)
        }
    }

	/**
	 * This method generates the output of the SSA call FORMAT=METADATA
	 * @param handlers the registerer input parameters handlers
	 * @param xmlContent a VOTable containing the column metadata of the table containing the SSA schema
	 * @return a VOTable 
	 */
    public static String getSSAMetadata(Collection<ParameterHandler> handlers, String xmlContent) {

        def OUTPUT = new XmlParser().parseText(xmlContent)

        def writer = new StringWriter()
        def doc    = new MarkupBuilder(writer)
        doc.setDoubleQuotes(true)

        doc.VOTABLE(version: "1.3", "xmlns:ssa": "http://www.ivoa.net/xml/DalSsap/v1.1",
                "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
                "xmlns": "http://www.ivoa.net/xml/VOTable/v1.3",
                "xsi:schemaLocation": "http://www.ivoa.net/xml/VOTable/v1.3 http://www.ivoa.net/xml/VOTable/VOTable-1.3.xsd") {
            DESCRIPTION("Lorem ipsum")
            RESOURCE(type: "results") {
                DESCRIPTION("Lorem ipsum")
                INFO(name: "QUERY_STATUS", "OK")
                INFO(name: "SERVICE_PROTOCOL", value: "1.1", "SSAP")
                handlers.each { handler ->
                    handler.getParameterInfos().each { param ->
                        PARAM(name: "INPUT:$param.name", value: param.value,
                                datatype: param.dataType, arraysize: param.arraySize) {
                            DESCRIPTION(param.description)
                        }
                    }
                }
                OUTPUT.RESOURCE.TABLE.FIELD.each { field ->
                    def attribs = field.attributes().collectEntries { k, v ->
                        if (k == "name")
                            v = "OUTPUT:$v"
                        return [k, v]
                    }
                    attribs.value = ""
                    PARAM(attribs) {
                        DESCRIPTION(field.DESCRIPTION.text())
                    }
                }
            }
        }
        return writer.toString()
    }

    public static String convertTAPtoSSAP(String tapResult) {
        def TAP = new XmlParser().parseText(tapResult)

        def queryStatus = TAP.RESOURCE.INFO.find { it.@name == "QUERY_STATUS" }.@value
        if (queryStatus != "OK") {
            // TODO do something
            log.error("Error executing TAP query. Status = $queryStatus")
        }

        /* add SERVICE_PROTOCOL */
        TAP.RESOURCE[0].appendNode(
                "INFO",
                [name: "SERVICE_PROTOCOL", value: "1.1"],
                "SSAP"
        )
        return XmlUtil.serialize(TAP)
    }

    public static String formatError(String error) {
        def writer = new StringWriter()
        def doc    = new MarkupBuilder(writer)
        doc.setDoubleQuotes(true)

        doc.VOTABLE(version: "1.3") {
            RESOURCE(type: "results") {
                INFO(name: "QUERY_STATUS", value: "ERROR", error)
                INFO(name: "SERVICE_PROTOCOL", value: "1.1", "SSAP")
                INFO(name: "REQUEST", value:"queryData")
            }
        }
        return writer.toString()
    }
}
