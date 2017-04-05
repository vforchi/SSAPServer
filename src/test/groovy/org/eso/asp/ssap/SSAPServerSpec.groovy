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

	def "Query with POS"() {
		setup:
		service.tapURL = "http://localhost:$port"

		when:
		restTemplate.getForObject("/ssa?REQUEST=queryData&POS=10.0,20.0", String.class)

		then:
		tapService.requestParams.QUERY == "SELECT * FROM ivoa.ssa WHERE CONTAINS(s_region, CIRCLE('',10.0,20.0,1)) = 1"
	}

}
