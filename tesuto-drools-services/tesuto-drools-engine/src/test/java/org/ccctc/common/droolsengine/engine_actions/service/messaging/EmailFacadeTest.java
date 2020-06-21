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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.messaging.EmailFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    classes={EmailFacade.class, TestingConfiguration.class })

@ComponentScan
public class EmailFacadeTest {    
    @Autowired
    private EmailFacade emailFacade;
    
    private MockRestServiceServer mockServer;
    
    @Value("${ACTION_RETRY_COUNT:5}")
    private int MAX_RETRY_COUNT;

    @Autowired
    private RestTemplate restTemplate = null;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void nominal() {
        String messagingUrl = "http://localhost:8080/";
        mockServer.expect(requestTo(messagingUrl + "ccc/api/messages/v1/sendEmail"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        mockServer.expect(requestTo(messagingUrl + "ccc/api/messages/v1/sendEmail"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.CREATED));

        System.setProperty("MESSAGING_URL", messagingUrl);
        try {
            Map<String, Object> facts = new HashMap<String, Object>();
            facts.put("oauthToken", "abcde");
            facts.put("cccid", "AAA5364");
            facts.put("cccMisCode", "ZZ1");
            
            RulesAction rulesAction = new RulesAction("MESSAGE");
            rulesAction.addActionParameter("to", "mgillian@unicon.net");
            rulesAction.addActionParameter("subject", "test subject");
            rulesAction.addActionParameter("message-body-text", "test-message-body");
            rulesAction.addActionParameter("message-body-html", "test-message-body-html");
            List<String> errors = emailFacade.execute(rulesAction, facts);
            for (String error: errors) {
                System.out.println("error:[" + error + "]");
            }
            assertEquals("Test has errors but should not", 1, errors.size());
            System.out.println("errors.size():[" + errors.size() + "]");
        } catch (Exception e) {
            fail("should not have thrown an exception trying to send this message: " + e.getMessage());
            log.error("should not have thrown an exception trying to send this message", e);
        }
        mockServer.verify();
    }
    
    @Test
    public void failRetries() {
        String messagingUrl = "http://localhost:8080/";
        
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            mockServer.expect(requestTo(messagingUrl + "ccc/api/messages/v1/sendEmail"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        }
        
        System.setProperty("MESSAGING_URL", messagingUrl);
        try {
            Map<String, Object> facts = new HashMap<String, Object>();
            facts.put("oauthToken", "abcde");
            facts.put("cccid", "AAA5364");
            facts.put("cccMisCode", "ZZ1");
            
            RulesAction rulesAction = new RulesAction("MESSAGE");
            rulesAction.addActionParameter("to", "mgillian@unicon.net");
            rulesAction.addActionParameter("subject", "test subject");
            rulesAction.addActionParameter("message-body-text", "test-message-body");
            rulesAction.addActionParameter("message-body-html", "test-message-body-html");
            List<String> errors = emailFacade.execute(rulesAction, facts);
            assertEquals("Test has errors but should not", MAX_RETRY_COUNT, errors.size());
        } catch (Exception e) {
            fail("should not have thrown an exception trying to send this message: " + e.getMessage());
            log.error("should not have thrown an exception trying to send this message", e);
        }
        mockServer.verify();
    }    
}
