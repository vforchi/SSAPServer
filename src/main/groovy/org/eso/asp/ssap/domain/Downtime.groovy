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

import java.time.Instant

import groovy.json.JsonSlurper
import groovy.transform.TupleConstructor

class Downtime {
	Instant start
	Instant stop
	String  note

	static Downtime getInstance(Instant start, Instant stop, String note) {
		return new Downtime(start: start, stop: stop, note: note)
	}
	static Downtime fromJson(String json) {
		def down = new JsonSlurper().parseText(json) as Downtime
		return down
	}
}
