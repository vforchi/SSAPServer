package org.eso.asp.ssap.domain

import org.apache.commons.lang3.tuple.ImmutablePair
import spock.lang.Specification

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

import spock.lang.Unroll
import java.text.ParseException

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
class RangeListParameterSpec extends Specification {

	@Unroll
	void "Convert #par"() {
		when:
		def rlp = RangeListParameter.parse(par, c)

		then:
		rlp.qualifier == expectedQualifier
		rlp.numEntries == expectedSize
		rlp.rangeEntries.collect { ImmutablePair p -> [p.getLeft(), p.getRight()] } == expectedRanges
		rlp.singleEntries == expectedValues
	
		when:
		RangeListParameter.parse(par, expectedSize)

		then:
		true

		where:
		par                 | c || expectedSize | expectedRanges | expectedValues | expectedQualifier
		/* single element */
		"0.123"             | { Double.valueOf(it)} || 1            | []       | [0.123]          | null
		"1E-7"              | { Double.valueOf(it)}  || 1           | []       | [10**(-7)]       | null
		"0.123;source"      | { Double.valueOf(it)}  || 1           | []       | [0.123]          | "source"
		"VAL"               | { String.valueOf(it)}  || 1           | []       | ["VAL"]          |  null
		"VAL;source"        | { String.valueOf(it)} || 1            | []       | ["VAL"]          | "source"

		/* single range */
		"0.123/1.23"        | { Double.valueOf(it)}  || 1           | [[0.123, 1.23]] | []       | null
		"0.123/1.43;source" | { Double.valueOf(it)}  || 1           | [[0.123, 1.43]] | []       | "source"

		/* multiple ranges */
		"1E-7/1E-6,1E-4/1E-2" | { Double.valueOf(it)}  || 2         | [[10**(-7), 10**(-6)], [0.0001, 0.01]] | [] | null
		"1E-7/1E-6,1E-4/1E-2;sss" | { Double.valueOf(it)}  || 2     | [[10**(-7), 10**(-6)], [0.0001, 0.01]] | [] | "sss"

		/* mixed single and range */
		"1E-7,1E-4/1E-2" | { Double.valueOf(it)}  || 2       | [[0.0001, 0.01]] | [10**(-7)] | null
		"1E-7/1E-6,1E-2;sss" | { Double.valueOf(it)}  || 2   | [[10**(-7), 10**(-6)]] | [0.01] | "sss"
		"1E-7/1E-6,J;sss" | { try { Double.valueOf(it) } catch (Exception e) { it } } || 2      | [[10**(-7), 10**(-6)]] | ["J"]  | "sss"

		/* range with strings */
		"2010/2011-01-01" | { String.valueOf(it)} || 1    | [["2010", "2011-01-01"]] | [] | null
	}

	@Unroll
	void "Correct size #par(#size)"() {
		when:
		RangeListParameter.parse(par, size)

		then:
		true

		where:
		par | size
		"121" | 1
		"121;dsd" | 1
		"12,33" | 2
		"11/121,123/33" | 2
	}

	@Unroll
	void "Wrong size #par(#size)"() {
		when:
		RangeListParameter.parse(par, size)

		then:
		ParseException ex = thrown()
		ex.message == "Wrong length in range list: expected $size, found $res"

		where:
		par | size || res
		"121" | 2  || 1
		"121;dsd" | 2 || 1
		"12,33" | 1  || 2
		"11/121,123/33" | 3 || 2
	}
}
