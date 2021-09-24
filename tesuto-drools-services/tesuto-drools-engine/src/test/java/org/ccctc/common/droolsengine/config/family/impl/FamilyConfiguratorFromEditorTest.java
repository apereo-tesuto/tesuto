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
package org.ccctc.common.droolsengine.config.family.impl;

import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
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
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class })
public class FamilyConfiguratorFromEditorTest {
    private static final String DEFAULT_ENGINE_NAME = "sns-listener";

    private static final String DEFAULT_CCC_MIS_CODE = "ZZ1";

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    private MockRestServiceServer mockServer;

    @Autowired
    private FamilyConfiguratorFromEditor reader;

    @Autowired
    private RestTemplate restTemplate = null;

    @Test
    public void readCollegesNoDataFound() {
        String profileUrl = config.getDroolsEditorURL() + "/rules-editor/colleges?status=active";
        mockServer.expect(requestTo(profileUrl)).andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        List<FamilyDTO> collegeDTOs = reader.getFamilies(true);
        assertTrue(collegeDTOs == null);
    }

    @Test
    public void readFamilies() {
        EngineDTO engineDTO = new EngineDTO().setDataSource(DroolsEngineEnvironmentConfiguration.RULES_SOURCE_EDITOR)
                                                  .setName(DEFAULT_ENGINE_NAME);
        Map<String, EngineDTO> engines = new HashMap<String, EngineDTO>();
        engines.put(DEFAULT_ENGINE_NAME, engineDTO);
        FamilyDTO collegeDTO = new FamilyDTO().setEngines(engines).setFamilyCode(DEFAULT_CCC_MIS_CODE).setStatus(PUBLISHED);
        ObjectMapper mapper = new ObjectMapper();
        String serverResponseAsJson = "";
        try {
            serverResponseAsJson = mapper.writeValueAsString(Arrays.asList(collegeDTO));
            String profileUrl = config.getDroolsEditorURL() + "/rules-editor/colleges?status=active";
            mockServer.expect(requestTo(profileUrl)).andExpect(method(HttpMethod.GET))
                            .andRespond(withSuccess(serverResponseAsJson, MediaType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e) {
            fail("should not get here");
        }

        List<FamilyDTO> familyDTOs = reader.getFamilies(true);
        assertEquals(1, familyDTOs.size());
        FamilyDTO familyDTO = familyDTOs.get(0);
        assertEquals(DEFAULT_CCC_MIS_CODE, familyDTO.getFamilyCode());
        EngineDTO returnedEngineDTO = familyDTO.getEngineDTO(DEFAULT_ENGINE_NAME);
        assertEquals(DroolsEngineEnvironmentConfiguration.RULES_SOURCE_EDITOR, returnedEngineDTO.getDataSource());
    }

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
}
