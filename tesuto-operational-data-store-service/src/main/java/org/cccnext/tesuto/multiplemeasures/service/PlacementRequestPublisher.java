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
package org.cccnext.tesuto.multiplemeasures.service;

import java.io.IOException;

import org.cccnext.tesuto.message.service.AbstractPublisher;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;



import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PlacementRequestPublisher extends AbstractPublisher<PlacementEventInputDto> {
    
    PlacementRequestRestClient restClient;
    
    public void setRestClient(PlacementRequestRestClient restClient) {
        this.restClient = restClient;
    }

    public void init() {
        AmazonSQS sqs = new AmazonSQSClient();
        sqs.setRegion(Region.getRegion(Regions.fromName(getRegionName())));
        setSqs(sqs);
        super.init();
    }
    
    public void setPlacementRequestRestClient(PlacementRequestRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected void handleMessageException(String placementEventInputDto, Exception exception) {
        log.error("Unable to put placement request on SQS queue " + getQueueUrl(), exception);
        try {
            restClient.requestPlacement(placementEventInputDto);
        }  catch (IOException e) {
            log.error("Unable to send placement request  to placement endpoint", exception);
        } catch (Exception e) {
            log.error("Unable to send placement request  to placement endpoint", exception);
        }
    }

    @Override
    public void sendUnsentMessages() {
        
    }

}
