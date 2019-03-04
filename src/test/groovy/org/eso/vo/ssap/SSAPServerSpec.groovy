package org.eso.vo.ssa

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

import org.eso.vo.ssa.controller.MockTAPService
import org.eso.vo.ssa.controller.SSAPController
import org.eso.vo.ssa.service.SSAPServiceTAPImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
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
		restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&$query", String.class)

		then:
		tapService.requestParams.QUERY == "SELECT * FROM $service.tapTable WHERE $condition"

		where:
		name | query || condition
		"POS"               | "POS=10.0,20.0"                       || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.033)) = 1"
		"POS and SIZE"      | "POS=10.0,20.0&SIZE=0.1"              || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.1)) = 1"
		"POS and VERSION"   | "POS=10.0,20.0&SIZE=0.1&VERSION=1.1"  || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.1)) = 1"
		"POS and FORMAT 1"  | "POS=10.0,20.0&FORMAT=fits"           || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.033)) = 1"
		"POS and FORMAT 2"  | "POS=10.0,20.0&FORMAT=FITS"           || "CONTAINS(s_region, CIRCLE('',10.0,20.0,0.033)) = 1"

		/* TIME */
		"TIME yyyy"         | "TIME=2010"                           || "t_min <= 55562.0 AND t_max >= 55197.0"
		"TIME yyyy-MM"      | "TIME=2010-06"                        || "t_min <= 55378.0 AND t_max >= 55348.0"
		"TIME yyyy-MM-dd"   | "TIME=2010-05-01"                     || "t_min <= 55318.0 AND t_max >= 55317.0"
		"TIME yyyy-MM-ddThh:mm:ss" | "TIME=2010-05-01T01:12:54"     || "t_min <= 55317.05063657407 AND t_max >= 55317.050625"

		"TIME yyyy/yyyy-MM-dd" | "TIME=2010/2011-04-01"             || "t_min <= 55653.0 AND t_max >= 55197.0"

		"TIME /yyyy-MM-dd" | "TIME=/2010-03-03"     || "t_min <= 55259.0"
		"TIME yyyy-MM-dd/" | "TIME=2010-03-03/"     || "t_max >= 55258.0"

		"TIME multiple ranges" | "TIME=/2010,2011"  || "t_min <= 55927.0 AND t_max >= 55562.0 OR t_min <= 55562.0"

		/* BAND */
		"BAND single WL" | "BAND=1E-7" || "em_min <= 1.0E-7 AND em_max >= 1.0E-7"
		"BAND single WL and observer" | "BAND=1E-7;observer" || "em_min <= 1.0E-7 AND em_max >= 1.0E-7"
		"BAND single range" | "BAND=1E-6/1E-7" || "em_min <= 1.0E-7 AND em_max >= 1.0E-6"
		"BAND open range left" | "BAND=/1E-7" || "em_min <= 1.0E-7"
		"BAND open range right" | "BAND=1E-6/" || "em_max >= 1.0E-6"
		"BAND multiple range" | "BAND=1E-6/2E-6,4E-6/6E-6" || "em_min <= 2.0E-6 AND em_max >= 1.0E-6 OR em_min <= 6.0E-6 AND em_max >= 4.0E-6"

		"COLLECTION" | "COLLECTION=SOME" || "collection LIKE '%SOME%'"
		"CREATORDID" | "CREATORDID=SOME" || "creatordid = 'SOME'"
		"PUBDID"     | "PUBDID=SOME" || "pubdid = 'SOME'"
		"SPATRES"    | "SPATRES=100" || "spatial_resolution > 100"
		"SPECRP"     | "SPECRP=100" || "specrp > 100"
		"SNR"        | "SNR=44" || "snr > 44"
		"TARGETNAME" | "TARGETNAME=orion" || "targetname = 'orion'"
	}

	@Unroll
	def "Error condition: #name"() {
		when:
		def res = restTemplate.getForObject("$SSAPController.prefix?$query", String.class)
		def VOTABLE = new XmlParser().parseText(res)

		then:
		message == VOTABLE.RESOURCE.INFO[0].text()

		where:
		name | query || message
		"no request"            | "POS=10.0,20.0&VERSION=1.0"                     || "Required String parameter 'REQUEST' is not present"
		"unsupported request"   | "REQUEST=getData&POS=10.0,20.0&VERSION=1.0"     || "VERSION=1.0 is not supported"
		"unsupported version"   | "REQUEST=queryData&POS=10.0,20.0&VERSION=1.0"   || "VERSION=1.0 is not supported"
		"unsupported format"    | "REQUEST=queryData&POS=10.0,20.0&FORMAT=xml"    || "FORMAT=xml is not supported"
		"empty TIME"            | "REQUEST=queryData&TIME=/"                      || "Invalid range /"
		"wrong SPATRES"         | "REQUEST=queryData&SPATRES=STR"                 || "Cannot convert STR to a float"
		"wrong SPECRP"          | "REQUEST=queryData&SPECRP=1.0r"                 || "Cannot convert 1.0r to a float"
		"wrong SNR"             | "REQUEST=queryData&SNR=1.0TT"                   || "Cannot convert 1.0TT to a float"

		/* wrong values out of range */
		"wrong RA, <0"          | "REQUEST=queryData&POS=370,10"                  || "RA in POS must be between 0 and 360"
		"wrong RA, >360"        | "REQUEST=queryData&POS=-50,10"                  || "RA in POS must be between 0 and 360"
		"wrong RA, not float"   | "REQUEST=queryData&POS=xxx,10"                  || "Can't convert xxx"
		"wrong DEC, >90"        | "REQUEST=queryData&POS=10,100"                  || "Dec in POS must be between -90 and 90"
		"wrong DEC, <-90"       | "REQUEST=queryData&POS=10,-100"                 || "Dec in POS must be between -90 and 90"
		"wrong DEC, not float"  | "REQUEST=queryData&POS=10,xxx"                  || "Can't convert xxx"
	}

	def "Reject unsupported version"() {
		when:
		def res = restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&POS=10.0,20.0&VERSION=1.0", String.class)
		def VOTABLE = new XmlParser().parseText(res)

		then:
		VOTABLE.RESOURCE.INFO[0].text() == "VERSION=1.0 is not supported"
	}

	def "No MAXREC"() {
		when:
		restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&TIME=1990/2000", String.class)

		then:
		tapService.requestParams.MAXREC == "1000"
	}

	def "With MAXREC"() {
		when:
		restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&TIME=1990/2000&MAXREC=5000", String.class)

		then:
		tapService.requestParams.MAXREC == "5000"
	}

	def "MAXREC too big"() {
		when:
		def res = restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&TIME=1990/2000&MAXREC=5000000", String.class)
		def VOTABLE = new XmlParser().parseText(res)

		then:
		VOTABLE.RESOURCE.INFO[0].text() == "The maximum value for MAXREC is 1000000"
	}

	@Unroll
	def "Parameter names are case insensitive"() {
		when:
		restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&$query1", String.class)
		def q1 = tapService.requestParams.QUERY
		restTemplate.getForObject("$SSAPController.prefix?REQUEST=queryData&$query2", String.class)
		def q2 = tapService.requestParams.QUERY

		then:
		q1 == q2

		where:
		query1 | query2
		"POS=10.0,20.0"   | "pos=10.0,20.0"
		"TIME=2010-05-01" | "tImE=2010-05-01"
	}

}
