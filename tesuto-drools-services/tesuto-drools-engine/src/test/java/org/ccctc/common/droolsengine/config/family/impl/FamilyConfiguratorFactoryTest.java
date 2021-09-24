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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEditor;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEnvironment;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class })
public class FamilyConfiguratorFactoryTest {
    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private IFamilyConfiguratorFactory factory;

    @Test
    public void configFromEditor() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_KEY,
                        DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_EDITOR);
        config.setEnvironment(env);

        IFamilyConfigurator reader = factory.getConfigurator();
        assertTrue(reader instanceof FamilyConfiguratorFromEditor);
    }

    @Test
    public void configFromEnvironment() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_KEY,
                        DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_ENVIRONMENT);
        config.setEnvironment(env);

        IFamilyConfigurator reader = factory.getConfigurator();
        assertTrue(reader instanceof FamilyConfiguratorFromEnvironment);
    }

    @Test
    public void configFromFile() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_KEY,
                        DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_FILE);
        config.setEnvironment(env);

        IFamilyConfigurator reader = factory.getConfigurator();
        assertTrue(reader instanceof FamilyConfiguratorFromFile);
    }

    @Test
    public void configInvalid() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.FAMILY_SOURCE_KEY, "invalidName");
        config.setEnvironment(env);

        try {
            factory.getConfigurator();
            fail("Should not get here, should throw a RuntimeException");
        }
        catch (RuntimeException e) {
            // ignore, this is valid
        }
        catch (Exception e) {
            fail("Should not get [" + e.getMessage() + "], should throw RuntimeException");
        }
    }

    @Test
    public void defaultConfig() {
        MockEnvironment env = new MockEnvironment();
        config.setEnvironment(env);

        IFamilyConfigurator reader = factory.getConfigurator();
        assertTrue(reader instanceof FamilyConfiguratorFromEnvironment);
    }
}
