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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class,
                StudentProfileFacade.class, NoOpActionService.class, StudentProfileFactsPreProcessor.class })
@TestPropertySource("classpath:application-test.properties")
public class FamilyConfiguratorFromFileTest {

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private FamilyConfiguratorFromFile familyReader;

    @Test
    public void nominal() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_FILEPATH_KEY, "./src/test/resources");
        config.setEnvironment(env);
        List<FamilyDTO> families = familyReader.getFamilies(true);
        assertEquals(1, families.size());
        FamilyDTO familyDTO = families.get(0);
        assertEquals("ZZ1", familyDTO.getFamilyCode());
        assertEquals(1, familyDTO.getEngineDTOs().size());

        Map<String, EngineDTO> engineDTOs = familyDTO.getEngineDTOs();
        for (Entry<String, EngineDTO> entry : engineDTOs.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue().getName());
        }
        assertTrue(engineDTOs.keySet().contains("applySubmit"));

        EngineDTO engineDTO = familyDTO.getEngineDTO("applySubmit");
        assertEquals("applySubmit", engineDTO.getName());
        assertEquals("org.cccmypath.rules", engineDTO.getGroupId());
        assertEquals("maven", engineDTO.getDataSource());
    }

    @Before
    public void setup() {
    }
}
