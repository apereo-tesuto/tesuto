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
package org.cccnext.tesuto.reports.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cccnext.tesuto.message.model.UnsentMessage;
import org.cccnext.tesuto.message.repository.jpa.UnsentMessageRepository;
import org.cccnext.tesuto.message.service.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

public class DatabasePublisher<T> implements MessagePublisher<T> {

    @Autowired
    private UnsentMessageRepository unsentMessageRepository;

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    private String queueUrl;

    private ObjectMapper mapper = new ObjectMapper();

    public void init() {
    }

    public void sendMessage(T messageObject) {
        try {

            sendMessage(mapper.writeValueAsString(messageObject));
        } catch (JsonProcessingException e) {
        }
    }

    protected void sendMessage(String message) {
        UnsentMessage unsentMessage = new UnsentMessage();
        unsentMessage.setId(UUID.randomUUID().toString());
        unsentMessage.setQueueUrl(queueUrl);
        unsentMessage.setLastSentOn(new Date());
        unsentMessage.setMessage(message);
        unsentMessageRepository.save(unsentMessage);
    }

    public void sendUnsentMessages() {

    }
}
