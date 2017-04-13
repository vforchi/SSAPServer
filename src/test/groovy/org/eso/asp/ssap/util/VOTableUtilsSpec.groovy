package org.eso.asp.ssap.util

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
/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
class VOTableUtilsSpec extends Specification {

	def "Get column mappings from XML"() {
		setup:
		def xml = this.class.getResource('/ssap_columns.vot').text

		when:
		def mappings = VOTableUtils.getUtypeToColumnsMappingsFromVOTable(xml)

		then:
		mappings['Char.SpatialAxis.Coverage.Support.Area']  == "s_region"
		mappings['Char.TimeAxis.Coverage.Bounds.Start'] == "t_min"
		mappings['Char.TimeAxis.Coverage.Bounds.Stop']  == "t_max"
	}

}
