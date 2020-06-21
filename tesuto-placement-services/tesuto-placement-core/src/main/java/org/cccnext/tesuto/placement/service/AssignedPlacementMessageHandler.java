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

import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.message.service.MessageHandler;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.service.PlacementEventService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("assignedPlacementMessageHandler")
public class AssignedPlacementMessageHandler  implements MessageHandler<PlacementEventInputDto> {

    @Autowired
    PlacementEventService placementService;

    @Override
    public MessageHandlingInstructions receive(PlacementEventInputDto placementEvent) {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        try {
            for (String misCode: placementEvent.getCollegeMisCodes()) {
                PlacementActionResult actionResult = new PlacementActionResult();
                actionResult.setCccid( placementEvent.getCccid());
                actionResult.setCollegeId( misCode);
                actionResult.setTrackingId( placementEvent.getTrackingId());
                actionResult.setPlacementId( placementEvent.getPlacementId());
                placementService.requestAssignedPlacement(actionResult);
            }
            instructions.setDelete(true);
        } catch (RetryableMessageException e) {
            log.error("Unable to initiate multiple measure placement component for event: " + placementEvent.toString(), e);
            instructions.setRetryDelay(30);
            instructions.setMinRetries(3);
        } catch (StoreMessageException e) {
            log.error("Unable to initiate multiple measure placement component for event: " + placementEvent.toString(), e);
            instructions = MessageHandlingInstructions.store();
        } catch (Exception e) {
            log.error("Unable to initiate multiple measure placement component for event: " + placementEvent.toString(), e);
            instructions.setRetryDelay(10);
            instructions.setMinRetries(3);
        }
        return instructions;
    }

}
