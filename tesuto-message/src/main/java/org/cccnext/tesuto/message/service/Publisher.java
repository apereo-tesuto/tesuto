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
package org.cccnext.tesuto.message.service;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.cccnext.tesuto.message.model.UnsentMessage;
import org.cccnext.tesuto.message.repository.jpa.UnsentMessageRepository;


import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class Publisher<T> extends  AbstractPublisher<T> {
    
    @Autowired
    private UnsentMessageRepository unsentMessageRepository;
    public void sendMessage(T messageObject) {
        try {
            if(!isInitialized()) {
                init();
            }
            sendMessage(getMapper().writeValueAsString(messageObject));
        } catch (JsonProcessingException exception) {
            log.error("Unable to send message\n", exception);
        }
    }

    protected void sendMessage(String message) {
        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest(getQueueUrl(), message);
            getSqs().sendMessage(sendMessageRequest);

        } catch (Exception exception) {
            handleMessageException( message,  exception);
        }
    }
    
    @Override
    protected void handleMessageException(String message, Exception exception) {
        log.error("Unable to send message\n", exception);
        UnsentMessage unsentMessage = new UnsentMessage();
        unsentMessage.setQueueUrl(getQueueUrl());
        unsentMessage.setId(UUID.randomUUID().toString());
        unsentMessage.setLastSentOn(new Date());
        unsentMessage.setMessage(message);
        unsentMessageRepository.save(unsentMessage);
    }

    @Override
    public void sendUnsentMessages() {
        if(!isInitialized()) {
            init();
        }
        Set<UnsentMessage> unsentMessages = unsentMessageRepository.findByQueueUrl(getQueueUrl());
        for(UnsentMessage message:unsentMessages) {
            if(message.getMessage() != null) {
                sendMessage(message.getMessage());
            }
            unsentMessageRepository.deleteById(message.getId());
        }
    }
}
