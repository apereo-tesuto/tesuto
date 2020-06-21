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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.message.service.MessageHandler;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("applicationMessageHandler")
public class ApplicationMessageHandler implements MessageHandler<Map<String,String>> {

    private MessageHandlingInstructions delayInstructions;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired  MultipleMeasurePlacementRequestService requestService;

    public MessageHandlingInstructions receive(Map<String,String> payload) {
        try {
        	String cccId = null;
        	String type = payload.get("Type");
        	if (StringUtils.equals(type, "Notification")) {
                log.warn("Received application with notification payload");
                JsonNode node = objectMapper.readTree(payload.get("Message"));
                cccId = node.get("ccc_id").textValue();
        	} else {
        		cccId = payload.get("ccc_id");
        	}
            log.debug("received message for {}", cccId);
            if (cccId == null) {
                log.warn("Received application with null cccId: " + payload);
                return MessageHandlingInstructions.success();
            } else {
                requestService.requestPlacements(cccId, true);
                return MessageHandlingInstructions.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return MessageHandlingInstructions.delay();
        }
    }
}
