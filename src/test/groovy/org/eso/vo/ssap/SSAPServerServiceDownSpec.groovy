package org.eso.vo.ssap

import groovy.json.JsonOutput
import org.eso.vo.ssap.controller.MockTAPService
import org.eso.vo.ssap.controller.SSAPController
import org.eso.vo.ssap.service.SSAPServiceTAPImpl
import org.eso.vo.vosi.domain.VOService
import org.eso.vo.vosi.service.AvailabilityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.text.SimpleDateFormat
import java.time.Instant

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
/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ssap")
class SSAPServerServiceDownSpec extends Specification {

	@Autowired
	TestRestTemplate restTemplate

	@Autowired
	SSAPServiceTAPImpl service
	
	@Autowired
	MockTAPService tapService
	
	@LocalServerPort
	int port
	
	@Autowired
	AvailabilityService availabilityService

	def setupSpec() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
		AvailabilityService.SCHEDULE_DT_FILE.delete()
	}
	
	def setup() {
		service.tapURL = "http://localhost:${port}"
	}

	def "Make server unavailable and verify that it comes back online"() {

		setup:
		def now = Instant.now()
		def start = now - 10
		def stop = now + 2
		def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		def requestJson = JsonOutput.toJson([start: formatter.format(start.toDate()), stop: formatter.format(stop.toDate()), note: "one"])
		def headers = new HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_JSON)

		def entity = new HttpEntity<String>(requestJson,headers)
		println "$start $stop"
		println availabilityService.availabilities[VOService.SSAP]*.downtimes.collect { "$it.start $it.stop" }
		restTemplate.postForObject("/actuator/availability/ssap", entity, String.class)
		println availabilityService.availabilities[VOService.SSAP]*.downtimes.collect { "$it.start $it.stop" }

		when:
		def res = restTemplate.exchange("$SSAPController.prefix?REQUEST=queryData", HttpMethod.GET, new HttpEntity<Object>(), String.class)

		then:
		res.status == HttpStatus.SERVICE_UNAVAILABLE.value()
		res.body == """<vosi:availability xmlns:vosi="http://www.ivoa.net/xml/VOSIAvailability/v1.0">
  <vosi:available>false</vosi:available>
  <vosi:backAt>${formatter.format(stop.toDate())}Z</vosi:backAt>
  <vosi:note>one</vosi:note>
</vosi:availability>"""

		when:
		Thread.sleep(2000)
		res = restTemplate.exchange("$SSAPController.prefix?REQUEST=queryData", HttpMethod.GET, new HttpEntity<Object>(), String.class)

		then:
		res.status == HttpStatus.BAD_REQUEST.value()
		res.body == """<VOTABLE version="1.3">
  <RESOURCE type="results">
    <INFO name="QUERY_STATUS" value="ERROR">Premature end of file.</INFO>
    <INFO name="SERVICE_PROTOCOL" value="1.1">SSAP</INFO>
    <INFO name="REQUEST" value="queryData" />
  </RESOURCE>
</VOTABLE>"""

		cleanup:
		availabilityService.SCHEDULE_DT_FILE.delete()
	}

}
