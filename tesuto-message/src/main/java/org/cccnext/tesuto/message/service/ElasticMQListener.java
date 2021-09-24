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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

/**
 * Created by Jason Brown jbrown@unicon.net on 11/7/16.
 */
public class ElasticMQListener extends Listener {

    public void init() {
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials("x", "x"));
        sqs.setEndpoint(getEndpoint());
        sqs.createQueue(new CreateQueueRequest(getQueueName()));
        setSqs(sqs);
        super.init();
    }
}
