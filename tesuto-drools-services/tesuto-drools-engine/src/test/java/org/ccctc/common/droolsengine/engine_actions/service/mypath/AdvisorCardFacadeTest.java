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
package org.ccctc.common.droolsengine.engine_actions.service.mypath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.docker.DockerConfig;
import org.ccctc.common.docker.RegistryConfig;
import org.ccctc.common.docker.DockerConfig.ContainerStartMode;
import org.ccctc.common.docker.spring.DockerizedTestExecutionListener;
import org.ccctc.common.docker.utils.SetupUtils;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.mypath.AdvisorCardFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={ TestingConfiguration.class, AdvisorCardFacade.class })
@TestExecutionListeners(value = {DockerizedTestExecutionListener.class},
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS )
@DockerConfig(
        image = SetupUtils.DOCKER_IMAGE,
        exposedPorts = { SetupUtils.EXPOSED_PORT },
        mappedPorts = { SetupUtils.MAPPED_PORT },
        waitForPorts = true,
        startMode = ContainerStartMode.FOR_EACH_TEST,
        host="unix:///var/run/docker.sock",
        waitForLogMessage=SetupUtils.WAIT_FOR_LOG_MESSAGE,
        registry = @RegistryConfig(email="", host="", userName="", passwd="")
)
public class AdvisorCardFacadeTest {

    @Autowired
    private AdvisorCardFacade facade;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    private SetupUtils setupUtils;
    private MockEnvironment env;

    @Before
    public void setup() {
        setupUtils = new SetupUtils();
        env = new MockEnvironment();
        config.setEnvironment(env);
    }

    private void setupRestServer(String fileName) {
        try {
            setupUtils.send(fileName);
        } catch (JsonParseException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void nominal() {
        setupRestServer("advisorCardFacadeSetup.json");

        env.setProperty(DroolsEngineEnvironmentConfiguration.ADVISORCARD_URL_KEY, 
                SetupUtils.ANSWER_ENDPOINT_URL);

        RulesAction action = new RulesAction(facade.getName());
        action.addActionParameter("state", "CA");
        action.addActionParameter("advisorCardTitle", "mytitle");

        Map<String, Object> facts = new HashMap<>();
        facts.put("cccid", "AAA1234");
        facts.put("cccMisCode", "ZZ1");

        List<String> errors = facade.execute(action, facts );
        System.out.println("errors [" + errors + "]");
        assertEquals(0, errors.size());
    }
}
