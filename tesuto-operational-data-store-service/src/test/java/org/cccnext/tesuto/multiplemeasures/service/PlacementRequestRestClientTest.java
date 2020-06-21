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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cccnext.tesuto.multiplemeasures.CalpassService;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlacementRequestRestClientTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Ignore
    public void placementRequestRestClientHappyPathTest() throws Exception {
        CalpassService handler = new CalpassService("classpath:/calpass-test.json");
        PlacementRequestRestClient restClient = handler.placementRequestRestClient();

        PlacementEventInputDto placementEventInputDto = new PlacementEventInputDto();

        placementEventInputDto.setCccid("ABC12345");
        Set<String> colleges = new HashSet<String>();
        colleges.add("ZZ6");
        placementEventInputDto.setCollegeMisCodes(colleges);
        placementEventInputDto.setTrackingId(UUID.randomUUID().toString());

        restClient.requestPlacement(objectMapper.writeValueAsString(placementEventInputDto));

    }

}
