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

package org.eso.vo.vosi.domain

import groovy.xml.MarkupBuilder

import java.time.Instant

class Availability {

	List<Downtime> downtimes = []

	boolean isAvailable() {
		cleanDowntimes()

		def now = Instant.now()
		boolean isDown = downtimes.any {
			now.isAfter(it.start) && now.isBefore(it.stop)
		}

		return !isDown
	}

	String toXML() {
		cleanDowntimes()

		def now = Instant.now()

		def currentDownInterval = downtimes.find {
			now.isAfter(it.start) && now.isBefore(it.stop)
		}

		def available = (currentDownInterval == null)
		def backAt
		def downAt

		if (!available)
			backAt = currentDownInterval.stop
		else {
			def nextDownInterval = downtimes.find {
				now.isBefore(it.start)
			}
			if (nextDownInterval)
				downAt = nextDownInterval.start
		}

		def writer = new StringWriter()
		def doc    = new MarkupBuilder(writer)
		doc.setDoubleQuotes(true)

		doc."vosi:availability"("xmlns:vosi": "http://www.ivoa.net/xml/VOSIAvailability/v1.0") {
			"vosi:available"(available)
			if (downAt)
				"vosi:downAt"(downAt)
			if (backAt)
				"vosi:backAt"(backAt)
			downtimes.each {
				"vosi:note"(it.note)
			}
		}

		return writer.toString()
	}

	private cleanDowntimes() {
		def now = Instant.now()
		downtimes = downtimes.findAll { now.isBefore(it.stop) }
	}

}
