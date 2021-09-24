/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.placement.service;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.repository.jpa.PlacementEventLogRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
@Transactional
public class PlacementEventLogService {

    @Autowired
    PlacementEventLogRepository repository;

    @Transactional
    public void log(String trackingId, String cccId, Integer subjectAreaId, Integer subjectAreaVersionId, String misCode,
                    PlacementEventLog.EventType eventType, String message) {
        PlacementEventLog event = new PlacementEventLog();
        event.setTrackingId(trackingId);
        event .setCccId(cccId);
        event .setSubjectAreaId(subjectAreaId);
        event.setSubjectAreaVersionId(subjectAreaVersionId);
        event .setMisCode(misCode);
        event .setEvent(eventType);
        event .setMessage(message);
        event .setCreateDate(new Date());
        if(StringUtils.containsIgnoreCase("FAIL", eventType.name())) {
            log.error("Placement Event {}", event);
        } else if(StringUtils.containsIgnoreCase("NULL", eventType.name())) {
            log.info("Placement Event {}", event);
        } else {
            log.debug("Placement Event {}", event);
        }
        repository.save(event);
    }
    
    @Transactional
    public void log(String trackingId, String cccId, String misCode,
            PlacementEventLog.EventType eventType, String message) {
        log( trackingId, cccId, null, null, misCode, eventType, message);
}

    @Transactional(readOnly=true)
    public Set<PlacementEventLog> findLogsByTrackingId(String trackingId) {
        return repository.findByTrackingId(trackingId);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}
