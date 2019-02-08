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

package org.eso.asp.ssap.domain

import spock.lang.Specification
import java.time.Instant

class AvailabilitySpec extends Specification {

	void "Convert Availability"() {
		when:
		def av = new Availability()
		av.notes = ["service is accepting queries", "everything ok"]

		then:
		av.toXML() == """<vosi:availability xmlns:vosi="http://www.ivoa.net/xml/VOSIAvailability/v1.0">
  <vosi:available>true</vosi:available>
  <vosi:note>service is accepting queries</vosi:note>
  <vosi:note>everything ok</vosi:note>
</vosi:availability>"""

		when:
		av = new Availability()
		def now = Instant.now()
		av.downtimes << [now - 1000, now + 1000]

		then:
		av.toXML() == """<vosi:availability xmlns:vosi="http://www.ivoa.net/xml/VOSIAvailability/v1.0">
  <vosi:available>false</vosi:available>
  <vosi:backAt>${now+1000}</vosi:backAt>
</vosi:availability>"""

		when:
		av = new Availability()
		av.downtimes << [now + 1001, now + 2000]

		then:
		av.toXML() == """<vosi:availability xmlns:vosi="http://www.ivoa.net/xml/VOSIAvailability/v1.0">
  <vosi:available>true</vosi:available>
  <vosi:downAt>${now+1001}</vosi:downAt>
</vosi:availability>"""

	}

}
