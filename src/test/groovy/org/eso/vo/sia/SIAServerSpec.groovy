package org.eso.vo.sia

import org.eso.vo.sia.controller.SIAController
import org.eso.vo.sia.service.SIAServiceObsTAPImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

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

import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sia")
class SIAServerSpec extends Specification {

	@Autowired
	TestRestTemplate restTemplate

	@Autowired
	SIAServiceObsTAPImpl service

	@LocalServerPort
	int port

	def setup() {
		service.tapURL = "http://localhost:$port"
	}

	@Unroll
	def "Query with #name"() {
		when:
		def encodedQuery = query.replaceAll(" ", "%20").replaceAll("\\+", "%2B")
		def builder = UriComponentsBuilder.fromUriString("$SIAController.prefix/query").query(encodedQuery)
		def uri = builder.build(true).toUri()

		def redirectQueryParams = headersToQuery(restTemplate.headForHeaders(uri))

		then:
		def getQuery = URLDecoder.decode(redirectQueryParams.getFirst("QUERY"), "ISO-8859-1")
		getQuery.startsWith("$service.baseQuery AND ")
		def getCondition = getQuery - "$service.baseQuery AND "
		getCondition ==~ "\\($expectedCondition\\)"

		when:
		restTemplate.postForObject(uri, encodedQuery, String.class)

		then:
		def postQuery = URLDecoder.decode(redirectQueryParams.getFirst("QUERY"), "ISO-8859-1")
		postQuery.startsWith("$service.baseQuery AND ")
		def postCondition = postQuery - "$service.baseQuery AND "
		postCondition ==~ "\\($expectedCondition\\)"

		where:
		name | query || expectedCondition
		"POS: CIRCLE"       | "POS=CIRCLE 10.0 20.0 1"                              || "INTERSECTS\\(s_region, CIRCLE\\('', 10.0, 20.0, 1\\)\\) = 1"
		"POS: RANGE"        | "POS=RANGE 12.0 12.5 34.0 36.0"                       || "INTERSECTS\\(s_region, POLYGON\\('', 12.0,34.0,12.0,36.0,12.05,36.0.*,12.5,36.0,12.5,34.0,12.45,34.0,.*,12.05,34.0\\)\\) = 1"
		"POS: POLYGON"      | "POS=POLYGON 12.0 34.0 14.0 35.0 14. 36.0 12.0 35.0"  || "INTERSECTS\\(s_region, POLYGON\\('', 12.0, 34.0, 14.0, 35.0, 14., 36.0, 12.0, 35.0\\)\\) = 1"
		"POS: pole"         | "POS=RANGE 0 360.0 89.0 +Inf"                         || "\\(s_dec BETWEEN 89.000000 AND 90.000000\\)"
		"POS: DFS-15248 1"    | "POS=RANGE 0 360.0 -2 2"                              || "\\(s_dec BETWEEN -2.000000 AND 2.000000\\)"
		"POS: DFS-15248 2"    | "POS=RANGE 10 20 -90 90"                              || "\\(s_ra BETWEEN 10.000000 AND 20.000000\\)"
		"POS: DFS-15248 3"    | "POS=RANGE 10 20 -Inf +Inf"                           || "\\(s_ra BETWEEN 10.000000 AND 20.000000\\)"
		//"POS: all sky"      | "POS=RANGE -Inf +Inf -Inf +Inf"                       || ""

		"BAND 1" | "BAND=500e-9 550e-9" || "\\(500e-9 <= em_max AND 550e-9 >= em_min\\)"
		"BAND 2" | "BAND=300 +Inf"      || "em_max >= 300"
		"BAND 3" | "BAND=-Inf 0.21"     || "em_min <= 0.21"
		"BAND 4" | "BAND=0.21"          || "\\(em_min <= 0.21 AND em_max >= 0.21\\)"

		"TIME 1"     | "TIME=55123.456 55123.466" || "\\(55123.456 <= t_max AND 55123.466 >= t_min\\)"
		"TIME 2"     | "TIME=55678.123"        || "\\(t_min <= 55678.123 AND t_max >= 55678.123\\)"

		// STRING
		"POL"        | "POL=K"           || "pol_states LIKE '%/K/%'"
		"ID"         | "ID=SOME"         || "obs_publisher_did = 'SOME'"
		"COLLECTION" | "COLLECTION=SOME" || "obs_collection = 'SOME'"
		"FACILITY"   | "FACILITY=SOME2"  || "facility_name = 'SOME2'"
		"INSTRUMENT" | "INSTRUMENT=SOME" || "instrument_name = 'SOME'"
		"DPTYPE"     | "DPTYPE=SOME"     || "dataproduct_type = 'SOME'"
		"TARGET"     | "TARGET=SOME4"    || "target_name = 'SOME4'"
		"FORMAT"     | "FORMAT=SOME"     || "access_format = 'SOME'"
		"2 COLLECTION" | "COLLECTION=SOME&COLLECTION=SOME2" || "obs_collection IN \\('SOME', 'SOME2'\\)"

		// SINGLE VALUE
		"CALIB"      | "CALIB=1"         || "calib_level = 1"
		"2 CALIB"    | "CALIB=1&CALIB=2" || "calib_level = 1 OR calib_level = 2"

		// RANGE
		"FOV"        | "FOV=1.0 2.0"     || "s_fov BETWEEN 1.0 AND 2.0"
		"FOV"        | "FOV=1.0 +Inf"    || "s_fov >= 1.0"
		"FOV"        | "FOV=-Inf 2.0"    || "s_fov <= 2.0"

	}

	def "Unsupported version"() {
		when:
		def encodedQuery = "POS=CIRCLE 10.0 20.0 1&VERSION=1.0".replaceAll(" ", "%20")
		def builder = UriComponentsBuilder.fromUriString("$SIAController.prefix/query").query(encodedQuery)
		def uri = builder.build(true).toUri()
		def res = restTemplate.getForObject(uri, String.class)
		def VOTABLE = new XmlParser().parseText(res)

		then:
		"VERSION=1.0 is not supported" == VOTABLE.RESOURCE.INFO[0].text()
	}

	def "No MAXREC"() {
		when:
		def redirectQueryParams = headersToQuery(restTemplate.headForHeaders("$SIAController.prefix/query?CALIB=0"))

		then:
		redirectQueryParams.getFirst("MAXREC") == "1000"
	}

	def "With MAXREC"() {
		when:
		def redirectQueryParams = headersToQuery(restTemplate.headForHeaders("$SIAController.prefix/query?MAXREC=5000"))

		then:
		redirectQueryParams.getFirst("MAXREC") == "5000"
	}

	def "MAXREC too big"() {
		when:
		def res = restTemplate.getForObject("$SIAController.prefix/query?CALIB=0&MAXREC=5000000", String.class)
		def VOTABLE = new XmlParser().parseText(res)

		then:
		VOTABLE.RESOURCE.INFO[0].text() == "The maximum value for MAXREC is 1000000"
	}

	@Unroll
	def "Parameter names are case insensitive"() {
		when:
		def q1 = headersToQuery(restTemplate.headForHeaders("$SIAController.prefix/query?REQUEST=queryData&$query1")).getFirst("QUERY")
		def q2 = headersToQuery(restTemplate.headForHeaders("$SIAController.prefix/query?REQUEST=queryData&$query2")).getFirst("QUERY")

		then:
		q1 == q2

		where:
		query1 | query2
		"POS=CIRCLE 10.0 20.0 1"   | "pos=CIRCLE 10.0 20.0 1"
		"TIME=10000" | "tImE=10000"
	}

	def "Retrieve capabilities"() {
		when:
		def capabilities = restTemplate.getForObject("$SIAController.prefix/capabilities", String.class)

		then:
		capabilities == """<?xml version="1.0" encoding="UTF-8"?>
<vosi:capabilities xmlns:vosi="http://www.ivoa.net/xml/VOSICapabilities/v1.0" xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <capability standardID="ivo://ivoa.net/std/VOSI#capabilities">
    <interface xsi:type="vs:ParamHTTP">
      <accessURL use="full">http://localhost:$port/sia/capabilities</accessURL>
    </interface>
  </capability>
  <capability standardID="ivo://ivoa.net/std/VOSI#availability">
    <interface xsi:type="vs:ParamHTTP">
      <accessURL use="full">http://localhost:$port/sia/availability</accessURL>
    </interface>
  </capability>
  <capability standardID="ivo://ivoa.net/std/SIA#query-2.0">
    <interface xsi:type="vs:ParamHTTP" role="std" version="2.0">
      <accessURL use="base">http://localhost:$port/sia/query</accessURL>
    </interface>
  </capability>
</vosi:capabilities>"""
	}

	static MultiValueMap<String, String> headersToQuery(HttpHeaders headers) {
		MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(headers.Location[0]).build().getQueryParams()
	}

}
