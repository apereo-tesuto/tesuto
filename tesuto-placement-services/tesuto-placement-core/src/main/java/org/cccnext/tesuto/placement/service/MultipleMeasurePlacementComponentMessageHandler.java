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

import java.util.Set;
import java.util.stream.Collectors;


import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.message.service.MessageHandler;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("multipleMeasurePlacementRequestHandler")
public class MultipleMeasurePlacementComponentMessageHandler implements MessageHandler<PlacementEventInputDto> {

    @Autowired
    MultipleMeasurePlacementComponentService service;

    @Autowired
    private PlacementEventLogService placementLogService;

    @Autowired
    private CompetencyMapDisciplineReader competencyMapDisciplineReader;

    @Override
    public MessageHandlingInstructions receive(PlacementEventInputDto placementRequest) {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        placementLogService.log(placementRequest.getTrackingId(),
                placementRequest.getCccid(),
                null, PlacementEventLog.EventType.PLACEMENT_MM_REQUEST_RECEIVED, "Processing Started for placement with multiple measure information");
        try {
            updateCompetencyMapDisciplines(placementRequest) ;
            service.processPlacementEvent(placementRequest);
            instructions.setDelete(true);

        } catch (RetryableMessageException exception) {
            logException(placementRequest, exception);
            instructions.setRetryDelay(30);
            instructions.setMinRetries(3);
        } catch (StoreMessageException exception) {
            logException(placementRequest, exception);
            instructions.setDelete(false);
            instructions.setStoreMessage(true);
        } catch (Exception exception) {
            logException(placementRequest, exception);
            instructions.setDelete(true);
        }
        return instructions;
    }

    private void updateCompetencyMapDisciplines(PlacementEventInputDto placementRequest) {
        if (CollectionUtils.isEmpty(placementRequest.getCompetencyMapDisciplines())) {
            Set<String> cmds = competencyMapDisciplineReader.read().stream().map(cm -> cm.getDisciplineName()).collect(Collectors.toSet());
            placementRequest.setCompetencyMapDisciplines(cmds);
        }
    }

    private void logException(PlacementEventInputDto placementRequest, Exception exception) {
        placementLogService.log(placementRequest.getTrackingId(),
                placementRequest.getCccid(),
                null, PlacementEventLog.EventType.PLACEMENT_MM_REQUEST_FAIL,
                "Unable to initiate multiple measure placement component proccessing for event: \n"  +
                ExceptionUtils.getFullException(exception));
        log.error("Unable to initiate multiple measure placement component for event: " + placementRequest.toString(), exception);
    }

}
