package org.eso.vo.vosi.service

import org.springframework.stereotype.Service
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
@Service
class CapabilitiesService {

    String getSIACapabilities(String baseURL) {
        return """<?xml version="1.0" encoding="UTF-8"?>
<vosi:capabilities xmlns:vosi="http://www.ivoa.net/xml/VOSICapabilities/v1.0" xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <capability standardID="ivo://ivoa.net/std/VOSI#capabilities">
    <interface xsi:type="vs:ParamHTTP">
      <accessURL use="full">$baseURL/sia/capabilities</accessURL>
    </interface>
  </capability>
  <capability standardID="ivo://ivoa.net/std/VOSI#availability">
    <interface xsi:type="vs:ParamHTTP">
      <accessURL use="full">$baseURL/sia/availability</accessURL>
    </interface>
  </capability>
  <capability standardID="ivo://ivoa.net/std/SIA#query-2.0">
    <interface xsi:type="vs:ParamHTTP" role="std" version="2.0">
      <accessURL use="base">$baseURL/sia/query</accessURL>
    </interface>
  </capability>
</vosi:capabilities>"""
    }

}
