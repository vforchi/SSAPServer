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

package org.eso.vo.vosi.controller;

import org.eso.vo.vosi.domain.VOService;
import org.eso.vo.vosi.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configurable
public class AvailabilityController {

    @Autowired
    AvailabilityService availabilityService;

    @GetMapping(path = "/{service}/availability", produces = "application/xml")
    public @ResponseBody String getAvailability(@PathVariable("service") String service) {
        VOService voService = VOService.valueOf(service.toUpperCase());
        return availabilityService.getAvailability(voService).toXML();
    }

}
