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
package org.ccctc.common.droolsengine.engine_actions.service.messaging;

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.messaging.MessageMicroserviceFacade;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={MessageMicroserviceFacade.class, TestingConfiguration.class })

@ComponentScan
public class MessageActionServiceTest {
    
    @Autowired
    private MessageMicroserviceFacade messageActionService;

    @Autowired
    private RestTemplate restTemplate = null;
    
    private MockRestServiceServer mockServer;

    private static String MESSAGING_URL;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // capture the environment variable and reset after tests are complete
        MESSAGING_URL = System.getenv("MESSAGING_URL");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (MESSAGING_URL != null) {
            System.setProperty("MESSAGING_URL", MESSAGING_URL);
        }
    }
    
    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void nominal() {
        String messagingUrl = "http://localhost:8080/";
        // response is ignored by MessageActionService.execute()
        mockServer.expect(requestTo(messagingUrl + "ccc/api/messages/v1/sendMessagesFromRule"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        System.setProperty("MESSAGING_URL", messagingUrl);
        try {
            Map<String, Object> facts = new HashMap<String, Object>();
            facts.put("oauthToken", "abcde");
            facts.put("cccid", "abcde");
            facts.put("cccMisCode", "ZZ1");
            RulesAction rulesAction = new RulesAction("MESSAGE");
            rulesAction.addActionParameter("subject", "test subject");
            rulesAction.addActionParameter("message-body", "test-message-body");
            rulesAction.addActionParameter("message-body-html", "test-message-body-html");
            List<String> errors = messageActionService.execute(rulesAction, facts);
            for (String error: errors) {
                System.out.println("error:[" + error + "]");
            }
            assertEquals("Should not have any errors", 0, errors.size());
        } catch (Exception e) {
            fail("should not get here: " + e.getMessage());
            log.error(e.getMessage(), e);
        }
        mockServer.verify();
    }
    
    @Test
    public void nominal2() {
        String messagingUrl = "http://localhost:8080/";
        // response is ignored by MessageActionService.execute()
        mockServer.expect(requestTo(messagingUrl + "ccc/api/messages/v1/sendMessagesFromRule"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.ACCEPTED));

        System.setProperty("MESSAGING_URL", messagingUrl);
        try {
            Map<String, Object> facts = new HashMap<String, Object>();
            facts.put("oauthToken", "abcde");
            facts.put("cccid", "abcde");
            facts.put("cccMisCode", "ZZ1");
            RulesAction rulesAction = new RulesAction("MESSAGE");
            rulesAction.addActionParameter("subject", "test subject");
            rulesAction.addActionParameter("message-body", "test-message-body");
            rulesAction.addActionParameter("message-body-html", "test-message-body-html");
            List<String> errors = messageActionService.execute(rulesAction, facts);
            for (String error: errors) {
                System.out.println("error:[" + error + "]");
            }
            assertEquals("Should not have any errors", 0, errors.size());
        } catch (Exception e) {
            fail("should not get here: " + e.getMessage());
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        mockServer.verify();
    }
}
