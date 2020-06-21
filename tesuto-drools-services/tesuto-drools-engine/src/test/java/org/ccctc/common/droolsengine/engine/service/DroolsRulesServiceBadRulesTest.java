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
package org.ccctc.common.droolsengine.engine.service;

import static org.junit.Assert.assertEquals;

import org.ccctc.common.droolscommon.KieContainerFactory;
import org.ccctc.common.droolsengine.config.TestConfigurationBadRules;
import org.ccctc.common.droolsengine.config.engine.impl.ContainerConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
                classes = { TestConfigurationBadRules.class, DroolsRulesService.class, StudentProfileFacade.class,
                            ContainerConfiguratorFactoryImpl.class, KieContainerFactory.class, NoOpActionService.class,
                            StudentProfileFactsPreProcessor.class })
public class DroolsRulesServiceBadRulesTest {
    @Autowired
    private DroolsRulesService rulesService;

    @Test
    public void checkStatus() {
        assertEquals(DroolsEngineStatus.DISABLED_STARTUP_FAILED, rulesService.getStatus());
    }
}
