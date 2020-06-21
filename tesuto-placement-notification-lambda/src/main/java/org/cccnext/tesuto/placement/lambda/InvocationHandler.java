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

import org.cccnext.tesuto.message.service.SQSListener;
import org.cccnext.tesuto.placement.model.CAPlacementTransaction;
import org.cccnext.tesuto.placement.service.CollegeAdaptorService;
import org.cccnext.tesuto.placement.service.PlacementNotificationDto;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class InvocationHandler {

    private ObjectMapper mapper;
    private static Map<String,String> env = System.getenv();

    static String getClientSecret() {
        String ssmClientSecretName = env.getOrDefault("CLIENT_SECRET_NAME", "mmi_client_password");
        GetParameterRequest request = new GetParameterRequest().withName(ssmClientSecretName).withWithDecryption(true);
        AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        GetParameterResult result = client.getParameter(request);
        return result.getParameter().getValue();
    }


    static CollegeAdaptorService buildCollegeAdaptorService() {
        String clientId = env.get("CLIENT_ID");
        String clientSecret = env.get("CLIENT_SECRET");
        if (clientSecret == null) {
            clientSecret = getClientSecret();
        }
        String accessTokenUri = env.get("ACCESS_TOKEN_URI");
        String grantType = "client_credentials";
        
        return new CollegeAdaptorService();
    }

    public void handleSNS(SNSEvent event, Context context)  {
        mapper = new ObjectMapper();
        event.getRecords().forEach(record -> handleRecord(record));
    }

    private void handleRecord(SNSEvent.SNSRecord record) {
        try {
            PlacementNotificationDto payload = mapper.readValue(record.getSNS().getMessage(), PlacementNotificationDto.class);
            CAPlacementAssembler assembler = new CAPlacementAssembler();
            CAPlacementTransaction transaction = assembler.assemble(payload);
            log.debug("sending {}", payload);
            buildCollegeAdaptorService().addPlacementTransaction(payload.getMisCode(), transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSQS(Context context) {
        log.debug("handler being called");
        SQSListener<PlacementNotificationDto> listener = sqsListener();
        listener.processMessages();
    }

    private SQSListener<PlacementNotificationDto> sqsListener() {
        Map<String,String> env = System.getenv();
        String queueUrl = env.get("QUEUE_URL");
        String region = env.get("REGION");
        SQSListener listener = new SQSListener<Map<String, String>>();
        SimpleModule module = new SimpleModule();
        listener.setReceiveClass(PlacementNotificationDto.class);
        listener.setQueueUrl(queueUrl);
        listener.setWaitTime(1);
        listener.setRegionName(region);
        PlacementNotificationReceiver receiver = new PlacementNotificationReceiver();
        listener.setMessageHandler(receiver);
        return listener;
    }

    static public void main(String args[]) {
        InvocationHandler handler = new InvocationHandler();
        handler.handleSQS(null);
    }
}
