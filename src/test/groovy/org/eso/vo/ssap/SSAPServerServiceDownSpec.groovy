package org.eso.vo.ssap

import org.eso.vo.ssap.controller.MockTAPService
import org.eso.vo.ssap.controller.SSAPController
import org.eso.vo.ssap.service.SSAPServiceTAPImpl
import org.eso.vo.vosi.domain.Availability
import org.eso.vo.vosi.domain.Downtime
import org.eso.vo.vosi.domain.VOService
import org.eso.vo.vosi.service.AvailabilityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

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
	AvailabilityService availabilityService;
	
	def setup() {
		service.tapURL = "http://localhost:${port}"
	}

	def "service down test"() {
			setup:
			def ssaAv = new Availability()
			def now = Instant.now()
			ssaAv.downtimes << new Downtime(start: now - 1000, stop: now + 1000, note: "one") //server is NOT available
			availabilityService.availabilities[VOService.SSAP] = ssaAv
			availabilityService.persistAvailability()

			when:
			def res = restTemplate.exchange("$SSAPController.prefix?REQUEST=POS=10+20+0.1",HttpMethod.GET, new HttpEntity<Object>(),String.class)
	
			then:
			res.status == HttpStatus.SERVICE_UNAVAILABLE.value() 
			res.body == """<vosi:availability xmlns:vosi="http://www.ivoa.net/xml/VOSIAvailability/v1.0">
  <vosi:available>false</vosi:available>
  <vosi:backAt>${now+1000}</vosi:backAt>
  <vosi:note>one</vosi:note>
</vosi:availability>"""
			
			cleanup:
			availabilityService.SCHEDULE_DT_FILE.delete()
	}
	
}
