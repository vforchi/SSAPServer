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

import org.eso.vo.vosi.domain.Availability;
import org.eso.vo.vosi.domain.Downtime;
import org.eso.vo.vosi.domain.VOService;
import org.eso.vo.vosi.service.AvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@Endpoint(id = "availability")
public class AvailabilityEndpoint {
	private static final Logger log = LoggerFactory.getLogger(AvailabilityEndpoint.class);
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final SimpleDateFormat in = new SimpleDateFormat(DATE_FORMAT);

	@Autowired
    AvailabilityService availabilityService;

    @ReadOperation
    public Map<VOService, Availability> availabilities() {
        return availabilityService.getAvailabilities();
    }
 
    @ReadOperation
    public Availability availability(@Selector String arg0) {
        try {
        	log.debug("availability by {}", arg0);
            return availabilityService.getAvailability(VOService.valueOf(arg0.toUpperCase()));
        } catch (Exception e) {
        	log.error("error while returning availability: {}", e.getMessage(), e);
            return null;
        }
    }
 
    @WriteOperation
    public void configureAvailability(@Selector String arg0, String start, String stop, String note) {
    	log.debug("configureAvailability by arg0={}, start={}, stop={}, note={}", arg0, start, stop, note);

        Availability av = availabilityService.getAvailability(VOService.valueOf(arg0.toUpperCase()));
        av.getDowntimes().add(Downtime.getInstance(stringToInstant(start), stringToInstant(stop), note));
    }
    
    private Instant stringToInstant(String dateStr) {
		try {
			Date date = in.parse(dateStr);
			return Instant.ofEpochMilli(date.getTime());
		} catch(Exception p) {
			throw new IllegalArgumentException("Fail to parse Instant from '"+dateStr+"'. Expected date format "+DATE_FORMAT);
		}
	}
 
}