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
 * Copyright 2019 - European Southern Observatory (ESO)
 */
/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */

package org.eso.asp.ssap.service

import org.eso.asp.ssap.domain.Availability
import org.eso.asp.ssap.domain.Downtime
import spock.lang.Specification
import java.time.Instant

class AvailabilityServiceSpec extends Specification {

	static final Instant now   = Instant.now()
	static final Instant start = now + 100
	static final Instant stop  = now + 200
	static final String json = """{"SSA":{"downtimes":[{"start":{"epochSecond":$start.epochSecond,"nano":$start.nano},"note":"one","stop":{"epochSecond":$stop.epochSecond,"nano":$stop.nano}}]}}"""

	def "Save Availability"() {
		setup:
		def availabilityService = new AvailabilityService()
		availabilityService.SCHEDULE_DT_FILE.delete()

		def ssaAv = new Availability()
		ssaAv.downtimes << new Downtime(start: start,
									    stop: stop,
										note: "one")
		
		availabilityService.availabilities[AvailabilityService.VOService.SSA] = ssaAv

		when:
		availabilityService.persistAvailability()

		then:
		availabilityService.SCHEDULE_DT_FILE.text == json
	}

	def "Retrieve Availability"() {
		setup:
		def availabilityService = new AvailabilityService()
		availabilityService.SCHEDULE_DT_FILE.text = json

		when:
		availabilityService.restoreAvailability()

		then:
		def ssaAv = availabilityService.availabilities[AvailabilityService.VOService.SSA]
		ssaAv.downtimes[0].start == start
		ssaAv.downtimes[0].stop  == stop
		ssaAv.downtimes[0].note  == "one"

		cleanup:
		availabilityService.SCHEDULE_DT_FILE.delete()
	}

}
