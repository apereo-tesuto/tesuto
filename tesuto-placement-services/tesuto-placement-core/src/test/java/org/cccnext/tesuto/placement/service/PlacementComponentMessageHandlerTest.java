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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.cccnext.tesuto.message.service.MessageHandlingInstructions;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PlacementComponentMessageHandlerTest {

    @Mock
    MultipleMeasurePlacementComponentService service;

    @Mock
    PlacementEventLogService placementLogService;

    @InjectMocks
    MultipleMeasurePlacementComponentMessageHandler mmPlacementComponentMessageHandler =
            new MultipleMeasurePlacementComponentMessageHandler();

    private PlacementEventInputDto testEvent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testEvent = new PlacementEventInputDto();
        testEvent.setCompetencyMapDisciplines( Collections.singleton("Test"));
    }

    @Test
    public void receiveWithStoreException() throws Exception {
        when(service.processPlacementEvent(any(PlacementEventInputDto.class))).thenThrow(new StoreMessageException());
        MessageHandlingInstructions instructions = mmPlacementComponentMessageHandler.receive( testEvent);
        System.out.println(instructions);
        Assert.assertFalse("Instruction should not be set to delete", instructions.isDelete());
        Assert.assertTrue("Instruction should be set to store",  instructions.isStoreMessage());
    }

    @Test
    public void receiveWithRetryableMessageException() throws Exception {
        when(service.processPlacementEvent(any(PlacementEventInputDto.class))).thenThrow(new RetryableMessageException());
        MessageHandlingInstructions instructions = mmPlacementComponentMessageHandler.receive( testEvent);
        Assert.assertFalse(instructions.isDelete());
        Assert.assertFalse(instructions.isStoreMessage());
        Assert.assertTrue( instructions.getMinRetries() > 0);
        Assert.assertTrue( instructions.getRetryDelay() > 0);
    }

    @Test
    public void receiveWithException() throws Exception {
        when(service.processPlacementEvent(any(PlacementEventInputDto.class))).thenThrow(new RuntimeException());
        MessageHandlingInstructions instructions = mmPlacementComponentMessageHandler.receive( testEvent);
        Assert.assertTrue(instructions.isDelete());
    }

    @Test
    public void receiveWithoutException() throws Exception {
        when(service.processPlacementEvent(any(PlacementEventInputDto.class))).thenReturn(new ArrayList<>());
        MessageHandlingInstructions instructions = mmPlacementComponentMessageHandler.receive( testEvent);
        Assert.assertTrue(instructions.isDelete());
    }
}
