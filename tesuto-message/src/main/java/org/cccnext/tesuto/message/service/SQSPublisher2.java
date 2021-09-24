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


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;




import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class SQSPublisher2<T> implements  MessagePublisher<T> {

    private String queueUrl;
    private AmazonSQS sqs = new AmazonSQSClient();

    private ObjectMapper mapper = new ObjectMapper();
    
    public SQSPublisher2() {
        mapper.registerModule(new Jdk8Module());
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public void sendMessage(T messageObject) {
        try {
            sendMessage(mapper.writeValueAsString(messageObject));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendMessage(String message) {
        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
            sqs.sendMessage(sendMessageRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
