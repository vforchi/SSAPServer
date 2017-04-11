package org.eso.asp.ssap

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

import org.eso.asp.ssap.controller.MockTAPService
import org.eso.asp.ssap.service.SSAPServiceTAPImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SSAPServerSpec extends Specification {

	@Autowired
	TestRestTemplate restTemplate

	@Autowired
	SSAPServiceTAPImpl service

	@Autowired
	MockTAPService tapService

	@LocalServerPort
	int port

	def setup() {
		service.tapURL = "http://localhost:$port"
	}

	@Unroll
	def "Query with #name"() {
		when:
		restTemplate.getForObject("/ssa?REQUEST=queryData&$query", String.class)

		then:
		tapService.requestParams.QUERY == "SELECT * FROM $service.tapTable WHERE $condition"

		where:
		name | query || condition
		"POS"               | "POS=10.0,20.0"                       || "CONTAINS(s_region, CIRCLE('',10.0,20.0,1)) = 1"
		"POS and SIZE"      | "POS=10.0,20.0&SIZE=0.1"              || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.1)) = 1"
		"POS and VERSION"   | "POS=10.0,20.0&SIZE=0.1&VERSION=1.1"  || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.1)) = 1"
		"POS and FORMAT 1"  | "POS=10.0,20.0&FORMAT=fits"           || "CONTAINS(s_region, CIRCLE('',10.0,20.0,1)) = 1"
		"POS and FORMAT 2"  | "POS=10.0,20.0&FORMAT=FITS"           || "CONTAINS(s_region, CIRCLE('',10.0,20.0,1)) = 1"

		"TIME yyyy"         | "TIME=2010"                           || "t_min <= 55562.0 AND t_max >= 55197.0"
		"TIME yyyy-MM"      | "TIME=2010-06"                        || "t_min <= 55378.0 AND t_max >= 55348.0"
		"TIME yyyy-MM-dd"   | "TIME=2010-05-01"                     || "t_min <= 55318.0 AND t_max >= 55317.0"
		"TIME yyyy-MM-ddThh:mm:ss" | "TIME=2010-05-01T01:12:54"     || "t_min <= 55317.0506365741 AND t_max >= 55317.050625"

		"TIME yyyy/yyyy-MM-dd" | "TIME=2010/2011-04-01"             || "t_min <= 55653.0 AND t_max >= 55197.0"

		"TIME /yyyy-MM-dd" | "TIME=/2010-03-03"     || "t_min <= 55259.0"
		"TIME yyyy-MM-dd/" | "TIME=2010-03-03/"     || "t_max >= 55258.0"

		"TIME multiple ranges" | "TIME=/2010,2011"  || "t_min <= 55927.0 AND t_max >= 55562.0 OR t_min <= 55562.0"
	}

	@Unroll
	def "Error condition: #name"() {
		when:
		def res = restTemplate.getForObject("/ssa?$query", String.class)

		then:
		res == message

		where:
		name | query || message
		"unsupported version" | "REQUEST=queryData&POS=10.0,20.0&VERSION=1.0" || "VERSION=1.0 is not supported"
		"unsupported format"  | "REQUEST=queryData&POS=10.0,20.0&FORMAT=xml"  || "FORMAT=xml is not supported"
		"empty TIME"    | "REQUEST=queryData&TIME=/" || "Invalid range /"
	}

	def "Reject unsupported version"() {
		when:
		def res = restTemplate.getForObject("/ssa?REQUEST=queryData&POS=10.0,20.0&VERSION=1.0", String.class)

		then:
		res == "VERSION=1.0 is not supported"
	}

}
