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

import java.util.Collection;


import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.message.service.MessageHandler;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("assessmentPlacementComponentMessageHandler")
public class AssessmentPlacementComponentMessageHandler implements MessageHandler<AssessmentCompletePlacementInputDto> {

    @Autowired
    AssessmentPlacementComponentService service;

    @Autowired
    PlacementEventService placementEventService;

    @Autowired
    private PlacementEventLogService placementLogService;

    @Override
    public MessageHandlingInstructions receive(AssessmentCompletePlacementInputDto placementRequest) {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        try {
            placementLogService.log(placementRequest.getTrackingId(),
                    placementRequest.getCccid(),
                    null, PlacementEventLog.EventType.PLACEMENT_ASSESS_REQUEST_RECEIVED, "Processing Started for placemetn with assessment information");

            if (CollectionUtils.isEmpty(placementRequest.getCompetencyMapDisciplines())) {
                log.error("For college {} no CompetencyMapDisplines were found for event {}", placementRequest);
                placementLogService.log(placementRequest.getTrackingId(), placementRequest.getCccid(),
                        null, PlacementEventLog.EventType.PLACEMENT_ASSESS_REQUEST_FAIL,
                       "Event data did not contain competencyMapDisciplines");
                instructions.setDelete(true);
                return instructions;
            }
            Collection<PlacementComponentViewDto> components = service.processPlacementEvent(placementRequest);
            if(CollectionUtils.isNotEmpty(components)) {
                components.forEach(c -> placementEventService.requestPlacementForComponent(c));
            }
            instructions.setDelete(true);
        } catch (RetryableMessageException exception) {
            logException(placementRequest, exception);
            instructions.setRetryDelay(30);
            instructions.setMinRetries(3);
        } catch (Exception exception) {
            logException(placementRequest, exception);
            instructions.setDelete(true);
        }
        return instructions;
    }

    private void logException(AssessmentCompletePlacementInputDto placementRequest, Exception exception) {
        log.error("Unable to initiate assessment placement component for event: " + placementRequest.toString(), exception);
        placementLogService.log(placementRequest.getTrackingId(),
                placementRequest.getCccid(),
                null, PlacementEventLog.EventType.PLACEMENT_ASSESS_REQUEST_FAIL, "Processing failed for placement with assessment information. \n" +
                ExceptionUtils.getFullException(exception));
    }

}
