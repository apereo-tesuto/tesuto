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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.util.Collections;

import org.apache.commons.lang3.RandomStringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CreatePlacementMessageHandlerTest {

	@Mock
	PlacementEventService service;

	@Mock
	PlacementEventLogService placementLogService;

	@InjectMocks
	CreatePlacementMessageHandler placementHandler = new CreatePlacementMessageHandler();

	private PlacementEventInputDto testEvent;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		testEvent = new PlacementEventInputDto();
		testEvent.setCollegeMisCodes( Collections.singleton("ZZ1"));
		testEvent.setCompetencyMapDisciplines( Collections.singleton("Test"));
		testEvent.setCccid( RandomStringUtils.random(100));
		testEvent.setTrackingId( RandomStringUtils.random(100));
		testEvent.setPlacementId( RandomStringUtils.random(100));
	}

	@Test
	public void receiveWithRetryableMessageException() throws Exception {
		doThrow(new RetryableMessageException()).when(service).requestPlacementForActionResult(any(PlacementActionResult.class));
		MessageHandlingInstructions instructions = placementHandler.receive( testEvent);
		Assert.assertFalse(instructions.isDelete());
		Assert.assertFalse(instructions.isStoreMessage());
		Assert.assertTrue( instructions.getMinRetries() > 0);
		Assert.assertTrue( instructions.getRetryDelay() > 0);
	}

	@Test
	public void receiveWithException() throws Exception {
		doThrow(new RuntimeException()).when(service).requestPlacementForActionResult(any(PlacementActionResult.class));
		MessageHandlingInstructions instructions = placementHandler.receive( testEvent);
		Assert.assertFalse(instructions.isDelete());
		Assert.assertFalse(instructions.isStoreMessage());
		Assert.assertTrue( instructions.getMinRetries() > 0);
		Assert.assertTrue( instructions.getRetryDelay() > 0);
	}

	@Test
	public void receiveWithoutException() throws Exception {
		doNothing().when(service).requestPlacementForActionResult(any(PlacementActionResult.class));
		MessageHandlingInstructions instructions = placementHandler.receive( testEvent);
		Assert.assertTrue(instructions.isDelete());
	}
}
