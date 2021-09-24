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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.rules.stub.PlacementReaderStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by bruce on 7/12/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-application-context.xml")
public class AssignPlacementFactServiceTest {

    @Autowired AssignPlacementFactService service;
    
    @Autowired
    PlacementReaderStub reader;

    @Before
    public void setup() throws Exception {
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
    
    private Integer randomInteger() {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(5000);
    }

    private Map<String,Object> initFacts() {
        String cccId = randomString();
        String misCode = randomString();
        Integer subjectAreaId = randomInteger();
        return new HashMap<String,Object>() {{
            put(PlacementMapKeys.CCCID_KEY, cccId);
            put(PlacementMapKeys.MISCODE_KEY, misCode);
            put(PlacementMapKeys.SUBJECT_AREA_KEY, subjectAreaId.toString());
            put(PlacementMapKeys.EVENT_TRIGGER_KEY, PlacementMapKeys.EVENT_TRIGGER_ASSIGNMENT);
            put(PlacementMapKeys.RULE_SET_ID_KEY, randomInteger());
        }};
    }

    @Test
    public void testNoExecuted() {
        Map<String, Object> facts = initFacts();
        facts.remove(PlacementMapKeys.EVENT_TRIGGER_KEY);
        reader.addPlacements((String) facts.get(PlacementMapKeys.MISCODE_KEY), 
                (String)facts.get(PlacementMapKeys.CCCID_KEY),
               Integer.parseInt( (String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY)), 
               1,2);
        List<String> errors = service.processFacts(facts);
        assertFalse(facts.containsKey(PlacementMapKeys.EVENT_TRIGGER_KEY));
        assertEquals(errors.toString(),0, errors.size());
    }


    @Test
    public void errorIfNoCccId() {
        Map<String, Object> facts = initFacts();
        reader.addPlacements((String) facts.get(PlacementMapKeys.MISCODE_KEY), 
                (String)facts.get(PlacementMapKeys.CCCID_KEY),
               Integer.parseInt( (String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY)), 
               1,2);
        facts.remove(PlacementMapKeys.CCCID_KEY);
        List<String> errors = service.processFacts(facts);
        assertEquals(errors.toString(),1, errors.size());
    }

    @Test
    public void errorIfNoMisCode() {
        Map<String, Object> facts = initFacts();
        facts.remove(PlacementMapKeys.MISCODE_KEY);
        reader.addPlacements((String) facts.get(PlacementMapKeys.MISCODE_KEY), 
                (String)facts.get(PlacementMapKeys.CCCID_KEY),
               Integer.parseInt( (String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY)), 
               1,2);
        List<String> errors = service.processFacts(facts);
        assertEquals(errors.toString(),1, errors.size());
    }

    @Test
    public void errorIfNoSourceDto() {
        Map<String, Object> facts = initFacts();
        reader.clearPlacements();
        List<String> errors = service.processFacts(facts);
        assertEquals(errors.toString(),1, errors.size());
    }

    @Test
    public void noErrors() {
        Map<String, Object> facts = initFacts();
        reader.addPlacements((String) facts.get(PlacementMapKeys.MISCODE_KEY), 
                (String)facts.get(PlacementMapKeys.CCCID_KEY),
               Integer.parseInt( (String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY)), 
               1,2);
        List<String> errors = service.processFacts(facts);
        assertEquals(errors.toString(), 0, errors.size());
    }


}
