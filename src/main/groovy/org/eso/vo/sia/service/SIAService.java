package org.eso.vo.siap.service;

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

import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
public interface SIAPService {

    /**
     * This method is invoked by any query for data
     *
     * @param params the query parameters
     * @return a String representation of a VOTable containining the result of the query
     * @throws IOException
     * @throws ParseException
     */
    String query(MultiValueMap<String, String> params) throws IOException, ParseException;

}
