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

import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
/**
 * Service will kick off the Rules Engin fater the placement event has been
 * triggered.
 */
public class PlacementRulesNotification {

	@Value("${placement.rules.queue}")
	private String queue;

	private AmazonSQS sqs = new AmazonSQSClient();
	private ObjectMapper objectMapper = new ObjectMapper();

	public void createPlacement(PlacementViewDto placementViewDto) {
		try {
			String json = objectMapper.writeValueAsString(placementViewDto);
			try {
				SendMessageRequest request = new SendMessageRequest(queue, json);
				sqs.sendMessage(request);
			} catch (Exception e) {
				log.error("Failed to send message to rules engin {}", json);
			}
		} catch (JsonProcessingException pe) {
			log.error("Failed to generate JSON from {}", placementViewDto);
		}
	}

}
