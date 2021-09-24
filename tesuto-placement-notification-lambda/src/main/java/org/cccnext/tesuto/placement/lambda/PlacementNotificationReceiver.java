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
package org.cccnext.tesuto.placement.lambda;

import java.util.Map;

import org.cccnext.tesuto.message.service.MessageHandler;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.model.CAPlacementTransaction;
import org.cccnext.tesuto.placement.service.CollegeAdaptorService;
import org.cccnext.tesuto.placement.service.PlacementNotificationDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PlacementNotificationReceiver implements MessageHandler<PlacementNotificationDto> {

    private CAPlacementAssembler assembler = new CAPlacementAssembler();
    private CollegeAdaptorService service;
    private Map<String,String> env = System.getenv();

    private int maxRetries;
    private int retryDelay;

    public PlacementNotificationReceiver() {
        maxRetries = Integer.parseInt(env.getOrDefault("MAX_RETRIES", "10"));
        retryDelay = Integer.parseInt(env.getOrDefault("RETRY_DELAY", "60"));
        //service = InvocationHandler.buildCollegeAdaptorService();
    }

    private MessageHandlingInstructions delay() {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions(false);
        instructions.setMaxAge(maxRetries*retryDelay);
        instructions.setRetryDelay(retryDelay);
        instructions.setMinRetries(maxRetries);
        return instructions;
    }


	private MessageHandlingInstructions handleMessage(PlacementNotificationDto payload) {
        try {
            CAPlacementTransaction transaction = assembler.assemble(payload);
            log.debug("sending {}", payload);
           // service.addPlacementTransaction(payload.getMisCode(), transaction);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return MessageHandlingInstructions.delay();
        }
        return MessageHandlingInstructions.success();
    }

    @Override
    public MessageHandlingInstructions receive(PlacementNotificationDto payload) {
	    try {
	        log.debug("received payload {}", payload);
            return handleMessage(payload);
        } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        return delay();
        }
    }
}
