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

import org.eso.asp.ssap.service.SSAPService;
import org.eso.asp.ssap.util.VOTableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.eso.asp.ssap.domain.SSAPConstants.QUERY_DATA;

/**
 * Controller implementing SSAP
 *
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
@RestController
@Configurable
@RequestMapping(SSAPController.prefix)
public class SSAPController {

    private static final Logger log = LoggerFactory.getLogger(SSAPController.class);

    public static final String prefix = "/ssap";

    @Autowired
    SSAPService service;

    @Value("#{${ssap.versions.supported:{'1.1'}}}")
    List<String> supportedVersions;

    @Value("#{${ssap.formats.supported:{'all', 'compliant', 'native', 'fits', 'application/fits'}}}")
    List<String> supportedFormats;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_XML_VALUE })
    @ResponseBody
    ResponseEntity<?> getSpectra(
            @RequestParam(value = "VERSION", required = false) String version,
            @RequestParam(value = "REQUEST")                   String request,
            @RequestParam(value = "FORMAT", required = false)  String format,
            @RequestParam                                      Map<String, String> allParams) throws Exception {

        log.info("Incoming request: version={}, request={}, format={}, params={}", version, request, format, allParams);

        /* check VERSION */
        if (version != null && !supportedVersions.contains(version))
            return toVOTable("VERSION=" + version + " is not supported");

        /* check FORMAT */
        if (format != null) {
            if (format.toLowerCase().equals("metadata"))
                return ResponseEntity.ok(service.getMetadata());    // metadata query
            else if (!supportedFormats.contains(format.toLowerCase()))
                return toVOTable("FORMAT=" + format + " is not supported");
        }

        /* check REQUEST */
        if (request.equals(QUERY_DATA))
            return ResponseEntity.ok(service.queryData(allParams));     // standard query
        else
            return toVOTable("REQUEST=" + request + " is not implemented");
    }

    /**
     * The SSAP standard requires the server return a VOTable in case of error as much as possible.
     * This ExceptionHandler takes care of that
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> toVOTable(Exception e) {
        return toVOTable(e.getMessage());
    }

    public ResponseEntity<?> toVOTable(String message) {
        String errorVOTable = VOTableUtils.formatError(message);
        return ResponseEntity.badRequest().body(errorVOTable);
    }
    
}
