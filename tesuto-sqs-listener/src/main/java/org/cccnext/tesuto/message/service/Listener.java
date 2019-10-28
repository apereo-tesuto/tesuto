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
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequest;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class Listener<T> implements MessageListener {


    static final String FIRST_RECEIVE = "ApproximateFirstReceiveTimestamp";
    static final String RECEIVE_COUNT = "ApproximateReceiveCount";
    static final String DELAY_IN_SECONDS = "DelaySeconds";

    private MessageHandler<T> messageHandler;
    private String queueUrl;
    private String queueName;
    private Class<T> receiveClass;
    private String regionName = null;
    private int waitTime = 20;
    private boolean initialized = false;
    private AmazonSQS sqs = null;
    private String endpoint;
    private ReceiveMessageRequest receiveMessageRequest = null;

    private ObjectMapper mapper;

    public MessageHandler<T> getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler<T> messageHandler) {
        this.messageHandler = messageHandler;
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Class<T> getReceiveClass() {
        return receiveClass;
    }

    public void setReceiveClass(Class<T> receiveClass) {
        this.receiveClass = receiveClass;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void init() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new Jdk8Module());
        receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setWaitTimeSeconds(waitTime);
        receiveMessageRequest = receiveMessageRequest.withAttributeNames(FIRST_RECEIVE, RECEIVE_COUNT);
        initialized = true;
    }

    public void setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private long getFirstReceiveTime(Message message) {
        String firstReceive = message.getAttributes().get(FIRST_RECEIVE);
        if (firstReceive == null) {
            log.warn("Did not receive message attribute " + FIRST_RECEIVE);
            return System.currentTimeMillis();
        } else {
            try {
                return Long.parseLong(firstReceive);
            } catch (NumberFormatException e) {
                return System.currentTimeMillis();
            }
        }
    }

    private int getPreviousAttempts(Message message) {
        String attempts = message.getAttributes().get(RECEIVE_COUNT);
        if (attempts == null) {
            log.warn("Did not receive message attribute " + RECEIVE_COUNT);
            return 0;
        } else {
            try {
                return Integer.parseInt(attempts);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private boolean delete(MessageHandlingInstructions instructions, Message message) {
        if (instructions.isDelete()) {
            return true;
        } else {
            return (instructions.getMaxAge() != null) &&
                    (getFirstReceiveTime(message) + instructions.getMaxAge() * 1000l < System.currentTimeMillis() + instructions.getRetryDelay() * 1000l) &&
                    (getPreviousAttempts(message) > instructions.getMinRetries());
        }
    }

    private boolean storeMessage(MessageHandlingInstructions instructions, Message message) {
        if (instructions.isStoreMessage()) {
            // save the message to a persistence store, or dead letter queue.
        }
        // return true so the message will be deleted.
        return true;
    }

    public void processMessages()  {
        if (!initialized) {
            init();
        }
        List<DeleteMessageBatchRequestEntry> deleteRequests = new ArrayList<>();
        List<ChangeMessageVisibilityBatchRequestEntry> changeRequests = new ArrayList<>();
        try {
            ReceiveMessageResult result = sqs.receiveMessage(receiveMessageRequest.withMessageAttributeNames(FIRST_RECEIVE, RECEIVE_COUNT));
            List<Message> list = result.getMessages();
            list.forEach(message -> {
                try {
                    String handle = message.getReceiptHandle();
                    T argument = mapper.readValue(message.getBody(), receiveClass);
                    MessageHandlingInstructions instructions = messageHandler.receive(argument);
                    log.debug("Received message attributes {}", message.getAttributes());
                    log.debug("Received message body {}", message.getBody());
                    if (storeMessage(instructions, message) || delete(instructions, message)) {
                        deleteRequests.add(
                                new DeleteMessageBatchRequestEntry(new Integer(handle.hashCode()).toString(), handle));
                    } else if (instructions.getRetryDelay() > 0) {
                        ChangeMessageVisibilityBatchRequestEntry entry = new ChangeMessageVisibilityBatchRequestEntry(
                                new Integer(handle.hashCode()).toString(), handle);
                        entry.setVisibilityTimeout(instructions.getRetryDelay());
                        changeRequests.add(entry);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }catch (Exception e){
            log.error(e.getLocalizedMessage(), e);
        }
        if (changeRequests.size() > 0) {
            try {
                sqs.changeMessageVisibilityBatch(new ChangeMessageVisibilityBatchRequest(queueUrl, changeRequests));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (deleteRequests.size() > 0) {
            try {
                sqs.deleteMessageBatch(new DeleteMessageBatchRequest(queueUrl, deleteRequests));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}


