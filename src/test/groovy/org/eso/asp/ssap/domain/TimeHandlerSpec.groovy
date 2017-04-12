package org.eso.asp.ssap.domain

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
 *  You should have received a copy of the GNU Lesser General Public License
 * along with SSAPServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2017 - European Southern Observatory (ESO)
 */

import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
class TimeHandlerSpec extends Specification {

	@Unroll
	def "Convert #string to MJD #mjd"() {
		when:
		def format = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC)
		def date = LocalDateTime.parse(string, format)
		def conv = TimeHandler.toMjd(date)

		then:
		Math.abs(conv - mjd) < 1E-8

		where:
		string || mjd
		"2000-01-01T00:00:00" || 51544.0
		"2010-01-01T00:00:00" || 55197.0

		"2000-01-01T01:00:00" || 51544.0 + 1/24
		"2010-01-01T00:01:00" || 55197.0 + 1/60/24
		"2010-01-01T00:00:01" || 55197.0 + 1/86400

		"2004-02-29T00:00:00" || 53064.0
	}

	@Unroll
	def "Convert #string to MJD interval"() {
		setup:
		def format = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.UTC)
		def mjdMin = TimeHandler.toMjd(LocalDateTime.parse(start, format))
		def mjdMax = TimeHandler.toMjd(LocalDateTime.parse(stop, format))
		
		when:
		Pair conv = TimeHandler.stringToMjdObsInterval(string)

		then:
		conv.getLeft()  == mjdMin
		conv.getRight() == mjdMax

		where:
		string || start | stop
		"1999"          || "1999-01-01T00:00:00" | "2000-01-01T00:00:00"
		"1999-02"       || "1999-02-01T00:00:00" | "1999-03-01T00:00:00"
		"1999-06-03"    || "1999-06-03T00:00:00" | "1999-06-04T00:00:00"
		"1999-06-03T10:01:02"  || "1999-06-03T10:01:02" | "1999-06-03T10:01:03"
	}

}
