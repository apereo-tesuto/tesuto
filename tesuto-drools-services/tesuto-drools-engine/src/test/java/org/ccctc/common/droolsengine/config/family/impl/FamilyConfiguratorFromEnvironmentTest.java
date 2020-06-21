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

import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class })
public class FamilyConfiguratorFromEnvironmentTest {

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private FamilyConfiguratorFromEnvironment reader;

    @Test
    public void nominal() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DROOLS_RULES_MISCODE_COUNT", "1");
        env.setProperty("DROOLS_RULES_MISCODE_1", "ZZ1");
        config.setEnvironment(env);
        List<FamilyDTO> families = reader.getFamilies(true);
        assertEquals(1, families.size());
    }
}
