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
package org.cccnext.tesuto.rules.service;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.placement.view.AssignedPlacementRulesSourceDto;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.APPLY_SOURCE;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.VERIFIED;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.SELF_REPORTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bruce on 7/12/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-application-context.xml")
public class AssignPlacementActionServiceTest {

    @Autowired AssignPlacementActionService service;
    @Autowired OperationalDataStoreService ods;

    @Before
    public void setup() throws Exception {
        ods.createStudentTable();
        ods.createVariableSetTable();
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Map<String,Object> initFacts() {
        String cccId = randomString();
        String misCode = randomString();
        return new HashMap<String,Object>() {{
            put(PlacementMapKeys.CCCID_KEY, cccId);
            put(PlacementMapKeys.MISCODE_KEY, misCode);
            put(PlacementMapKeys.EVENT_TRIGGER_KEY, PlacementMapKeys.EVENT_TRIGGER_ASSIGNMENT);
            put(PlacementMapKeys.PLACEMENT_ID_KEY, new AssignedPlacementRulesSourceDto());
        }};
    }

    @Test
    public void testNoExecuted() {
    }

}
