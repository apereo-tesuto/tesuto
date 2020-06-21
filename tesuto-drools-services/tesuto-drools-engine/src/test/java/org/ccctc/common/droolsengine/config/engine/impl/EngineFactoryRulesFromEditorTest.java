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
package org.ccctc.common.droolsengine.config.engine.impl;

import static org.ccctc.common.droolscommon.model.RuleStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;
import java.util.List;

import org.ccctc.common.droolscommon.model.DrlDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.engine.impl.EngineFactoryRulesFromEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={TestingConfiguration.class})
public class EngineFactoryRulesFromEditorTest {
    private static final String DEFAULT_APPLICATION_NAME = "sns-listener";
    
    private static final String DEFAULT_CCC_MIS_CODE = "ZZ1";
    
    @Autowired
    private DroolsEngineEnvironmentConfiguration config;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    private MockRestServiceServer mockServer;

    private EngineFactoryRulesFromEditor reader;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Test
    public void getDrlFromRuleSetId() {
        String ruleSetId = "1234";
        String drlUrl = reader.getEditorDrlURL();
        String drlString = "dummy-drl-do-not-try-to-compile";
        try {
            DrlDTO drlDTO = new DrlDTO();
            drlDTO.setDrl(drlString);
            String serverRespnseAsJson = mapper.writeValueAsString(drlDTO);
            mockServer.expect(requestTo(drlUrl + "/" + ruleSetId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(serverRespnseAsJson, MediaType.APPLICATION_JSON));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Should not throw an exception");
        }
        String drl = reader.getDrlFromRuleSetId(ruleSetId);
        assertEquals(drlString, drl);
    }
    
    @Test
    public void getDrlFromRuleSetIdNotFound() {
        String ruleSetId = "1234";
        String drlUrl = reader.getEditorDrlURL();
        mockServer.expect(requestTo(drlUrl + "/" + ruleSetId))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
        String drl = reader.getDrlFromRuleSetId(ruleSetId);
        assertEquals("", drl);
    }
    
    @Test
    public void getRuleSetDTOs() {
        String profileUrl = reader.getEditorRuleSetURL(DEFAULT_CCC_MIS_CODE);
        RuleSetDTO ruleSetDTO = new RuleSetDTO();
        ruleSetDTO.setEngine(DEFAULT_APPLICATION_NAME);
        ruleSetDTO.setFamily(DEFAULT_CCC_MIS_CODE);
        ruleSetDTO.setStatus(PUBLISHED);
        try {
            String serverResponseAsJson = mapper.writeValueAsString(Arrays.asList(ruleSetDTO));
            mockServer.expect(requestTo(profileUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(serverResponseAsJson, MediaType.APPLICATION_JSON));
        } catch (JsonProcessingException e) {
            fail("should not get here");
        }

        List<RuleSetDTO> ruleSetDTOs = reader.getRuleSetDTOs(DEFAULT_CCC_MIS_CODE);
        assertEquals(1, ruleSetDTOs.size());
        RuleSetDTO returnedRuleSetDTO = ruleSetDTOs.get(0);
        assertEquals(DEFAULT_APPLICATION_NAME, returnedRuleSetDTO.getEngine());
        
    }
    
    @Test
    public void getRuleSetDTOsNotFound() {
        String profileUrl = reader.getEditorRuleSetURL(DEFAULT_CCC_MIS_CODE);
        mockServer.expect(requestTo(profileUrl))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        List<RuleSetDTO> ruleSetDTOs = reader.getRuleSetDTOs(DEFAULT_CCC_MIS_CODE);
        assertTrue(ruleSetDTOs == null);
    }
    
    @Before
    public void setup() {
        reader = new EngineFactoryRulesFromEditor(config, restTemplate, DEFAULT_APPLICATION_NAME);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
}
