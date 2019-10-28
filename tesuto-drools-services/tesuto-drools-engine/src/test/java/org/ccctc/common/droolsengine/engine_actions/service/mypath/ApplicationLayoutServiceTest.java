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

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.mypath.ApplicationLayoutService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={ApplicationLayoutService.class, TestingConfiguration.class})

@ComponentScan
@TestPropertySource("classpath:application-test.properties")
public class ApplicationLayoutServiceTest {
    private static final String DEFAULT_CCCID = "abcde";
    private static final String DEFAULT_MIS_CODE = "ZZ1";
    private static final String DEFAULT_ATTRIBUTE_NAME = "is_veteran";
    private MockRestServiceServer mockServer;

    @Autowired
    private ApplicationLayoutService applicationLayoutService = null;

    @Autowired
    RestTemplate rt = null;
    
    @Autowired
    private Environment env = null;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(rt);
    }
    
    @Test
    public void nominal() {
        String applicationLayoutUrl = env.getProperty("APPLICATION_LAYOUT_URL");
        String attribute = "is_veteran";
        String value = "true";
        String targetUrl = applicationLayoutUrl + "/ccc/api/users/" + DEFAULT_CCCID +"/cccMisCode/" + DEFAULT_MIS_CODE + "/attributes/" + attribute + "/value/" + value;

        // response is ignored by MessageActionService.execute()
        mockServer.expect(requestTo(targetUrl))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK));
                
        try {
            Map<String, Object> facts = new HashMap<String, Object>();
            facts.put("oauthToken", "abcde");
            facts.put("cccid", DEFAULT_CCCID);
            facts.put("cccMisCode", DEFAULT_MIS_CODE);
            RulesAction rulesAction = new RulesAction("PINBOARD");
            rulesAction.addActionParameter("action", "add");
            rulesAction.addActionParameter("attribute_name", DEFAULT_ATTRIBUTE_NAME);
            rulesAction.addActionParameter("attribute_value", "true");
            List<String> errors = applicationLayoutService.execute(rulesAction, facts);
            for (String error: errors) {
                System.out.println("error:[" + error + "]");
            }
            assertEquals("Should not have any errors", 0, errors.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail("should not get here: " + e.getMessage());
        }
        mockServer.verify();
    }    
}
