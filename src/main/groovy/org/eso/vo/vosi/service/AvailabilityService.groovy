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

package org.eso.vo.dali.service

import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import org.eso.vo.dali.domain.Availability
import org.eso.vo.dali.domain.Downtime
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.time.Instant

@Service
class AvailabilityService {

    static final File SCHEDULE_DT_FILE = new File("scheduled_downtimes.json")

    enum VOService {
        SSA
    }

    Map<VOService, Availability> availabilities = [:]

    @PostConstruct
    void restoreAvailability() {
        VOService.values().each {
            availabilities[it] = new Availability()
        }

        if (SCHEDULE_DT_FILE.exists())
            availabilities = new JsonSlurper().parse(SCHEDULE_DT_FILE).collectEntries { Map.Entry entry ->
                def av = new Availability()
                av.downtimes = entry.value.downtimes.collect {
                    return new Downtime(start: Instant.ofEpochSecond(it.start.epochSecond, it.start.nano),
                                        stop: Instant.ofEpochSecond(it.stop.epochSecond, it.stop.nano),
                                        note: it.note)
                }
                [(VOService.valueOf(entry.key)): av]
            }
    }

    @PreDestroy
    void persistAvailability() {
        def gen = new JsonGenerator.Options()
            .excludeFieldsByName("available")
            .build()

        SCHEDULE_DT_FILE.text = gen.toJson(availabilities)
    }

    Availability getAvailability(VOService service) {
        return availabilities.get(service)
    }

}
