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

import org.ccctc.common.droolsengine.engine.service.IContainerService;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine.service.IContainerServiceFactory;
import org.ccctc.common.droolsengine.engine.service.DefaultContainerService;
import org.ccctc.common.droolsengine.engine.service.PollingContainerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={TestingConfiguration.class})
public class DefaultContainerServiceFactoryTest {

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private IContainerServiceFactory containerServiceFactory;

    @Test
    public void getContainerServiceNoEnvironmentProperty() {
        MockEnvironment env = new MockEnvironment();
        config.setEnvironment(env);
        IContainerService containerService = containerServiceFactory.getContainerService("testName");
        assertEquals(DefaultContainerService.class, containerService.getClass());
    }

    @Test
    public void getContainerServiceEnvironmentPropertyDefault() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.CONTAINER_SERVICE_TYPE_KEY, DroolsEngineEnvironmentConfiguration.CONTAINER_SERVICE_TYPE_DEFAULT);
        config.setEnvironment(env);
        IContainerService containerService = containerServiceFactory.getContainerService("testName");
        assertEquals(DefaultContainerService.class, containerService.getClass());
    }

    @Test
    public void getContainerServiceEnvironmentPropertyPolling() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.CONTAINER_SERVICE_TYPE_KEY, DroolsEngineEnvironmentConfiguration.CONTAINER_SERVICE_TYPE_POLLING);
        config.setEnvironment(env);
        IContainerService containerService = containerServiceFactory.getContainerService("testName");
        assertEquals(PollingContainerService.class, containerService.getClass());
    }
}
