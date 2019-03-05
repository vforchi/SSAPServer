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
import org.eso.asp.ssap.controller.SSAPController
import org.eso.asp.ssap.service.SSAPServiceTAPImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

import spock.lang.Specification

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SSAPServerServiceDownSpec extends Specification {

	@Autowired
	TestRestTemplate restTemplate

	@Autowired
	SSAPServiceTAPImpl service

	@LocalServerPort
	int port

	def setup() {
		service.tapURL = "http://localhost:${port+1}" //non-existing port
	}


	def "TAP service down check"() {
		when:
		ResponseEntity<String> res = restTemplate.exchange("$SSAPController.prefix?REQUEST=queryData&POS=10.0,20.0",HttpMethod.GET, new HttpEntity<Object>(),String.class);

		then:
		res.statusCode == org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
		res.body == '{"SSA":{"downtimes":[],"available":true}}'
	}

	

}
