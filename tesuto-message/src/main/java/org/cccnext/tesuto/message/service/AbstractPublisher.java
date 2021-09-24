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
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AbstractPublisher<T>  implements  MessagePublisher<T> {
    private String queueUrl;
    private String queueName;
    private String regionName = null;
    private boolean initialized = false;
    private String endpoint;
    private AmazonSQS sqs = null;
    private Class<T> sendClass;


    private ObjectMapper mapper = new ObjectMapper();

    public String getEndpoint() {
        return endpoint;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void init() {
        initialized = true;
    }

    public AmazonSQS getSqs() {
        return sqs;
    }

    public void setSqs(AmazonSQS sqs) {
        this.sqs = sqs;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Class<T> getSendClass() {
        return sendClass;
    }

    public void setSendClass(Class<T> sendClass) {
        this.sendClass = sendClass;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    public void sendMessage(T messageObject) {
        try {
            if(!initialized) {
                init();
            }
            sendMessage(mapper.writeValueAsString(messageObject));
        } catch (JsonProcessingException exception) {
            log.error("Unable to send message\n", exception);
        }
    }

    protected void sendMessage(String message) {
        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
            sqs.sendMessage(sendMessageRequest);

        } catch (Exception exception) {
            handleMessageException( message,  exception);
        }
    }
    
    abstract protected void handleMessageException(String message, Exception exception);

    abstract public void sendUnsentMessages() ;
}
