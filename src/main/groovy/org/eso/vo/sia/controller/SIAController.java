package org.eso.vo.sia.controller;

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

import org.eso.vo.dali.domain.DALIConstants;
import org.eso.vo.sia.service.SIAService;
import org.eso.vo.sia.util.SIAUtils;
import org.eso.vo.vosi.domain.Availability;
import org.eso.vo.vosi.domain.VOService;
import org.eso.vo.vosi.service.AvailabilityService;
import org.eso.vo.vosi.service.CapabilitiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller implementing SIA
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@RestController
@Configurable
@ConditionalOnProperty(value="sia.enabled", havingValue = "true")
@RequestMapping(SIAController.prefix)
public class SIAController {

    private static final Logger log = LoggerFactory.getLogger(SIAController.class);

    public static final String prefix = "/sia";

    @Autowired
    SIAService SIAService;

    @Autowired
    AvailabilityService availabilityService;

    @Autowired
    CapabilitiesService capabilitiesService;

    @Value("#{${sia.versions.supported:{'2', '2.0'}}}")
    List<String> supportedVersions;

    @Value("${base.url:}")
    String baseUrl;

    @ResponseBody
    @RequestMapping(path = "query", method = { RequestMethod.GET, RequestMethod.POST }, produces = { MediaType.TEXT_XML_VALUE })
    ResponseEntity<?> query(
            @RequestParam(value = DALIConstants.VERSION, required = false) String version,
            @RequestParam(value = DALIConstants.RESPONSEFORMAT, required = false)  String responseformat,
            @RequestParam MultiValueMap allParams) throws Exception {

        log.info("Incoming request: version={}, format={}, params={}", version, responseformat, allParams);

        /* check AVAILABLITY */
        Availability ssaAvailability = availabilityService.getAvailability(VOService.SIA);
        if(!ssaAvailability.isAvailable())
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ssaAvailability.toXML());

        /* check VERSION */
        if (version != null && !supportedVersions.contains(version))
            return toVOTable("VERSION=" + version + " is not supported");

        return ResponseEntity.ok(SIAService.query(allParams));
    }

    @ResponseBody
    @RequestMapping(path = "capabilities", method = RequestMethod.GET, produces = { MediaType.TEXT_XML_VALUE })
    ResponseEntity<?> capabilities(HttpServletRequest request) {
        String url = baseUrl;
        if (url.isEmpty()) {
            url = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80)
                url += ":" + request.getServerPort();
        }
        return ResponseEntity.ok(capabilitiesService.getSIACapabilities(url));
    }
    
    /**
     * The DALI standard requires the server return a VOTable in case of error as much as possible.
     * This ExceptionHandler takes care of that
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> toVOTable(Exception e) {
        return toVOTable(e.getMessage());
    }

    public ResponseEntity<?> toVOTable(String message) {
        String errorVOTable = SIAUtils.formatError(message);
        return ResponseEntity.badRequest().body(errorVOTable);
    }
    
}
