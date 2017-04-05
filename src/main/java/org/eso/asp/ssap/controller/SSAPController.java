package org.eso.asp.ssap.controller;

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

import org.eso.asp.ssap.domain.RangeListParameter;
import org.eso.asp.ssap.service.SSAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller implementing the /ssa API of SSAP
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@RestController
@RequestMapping("/ssa")
public class SSAPController {

    @Autowired
    SSAPService service;

//    @Bean
//    Filter caseInsensitiveFilter() {
//        return new CaseInsensitiveRequestFilter();
//    }

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_XML_VALUE })
    @ResponseBody
    ResponseEntity<?> getSpectra(
            @RequestParam(value = "VERSION", required = false) String version,
            @RequestParam(value = "REQUEST", required = true) String request,
            @RequestParam Map<String, String> allParams) {

        try {
            if (request.equals(RangeListParameter.ParametersMappings.QUERY_DATA)) {
                Object body = service.queryData(allParams);
                return ResponseEntity.ok(body);
            } else
                return ResponseEntity.badRequest().body("REQUEST=" + request + "is not implemented");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
