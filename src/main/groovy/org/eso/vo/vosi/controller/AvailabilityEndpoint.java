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

package org.eso.vo.dali.controller;

import org.eso.vo.dali.domain.Availability;
import org.eso.vo.dali.domain.Downtime;
import org.eso.vo.dali.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "availability")
public class AvailabilityEndpoint {

    @Autowired
    AvailabilityService availabilityService;

    @ReadOperation
    public Map<AvailabilityService.VOService, Availability> availabilities() {
        return availabilityService.getAvailabilities();
    }
 
    @ReadOperation
    public Availability availability(@Selector String arg0) {
        try {
            return availabilityService.getAvailability(AvailabilityService.VOService.valueOf(arg0));
        } catch (Exception e) {
            return null;
        }
    }
 
    @WriteOperation
    public void configureFeature(@Selector String arg0, String arg1) {
        Availability av = availabilityService.getAvailability(AvailabilityService.VOService.valueOf(arg0));
        av.getDowntimes().add(Downtime.fromJson(arg1));
    }

 
}