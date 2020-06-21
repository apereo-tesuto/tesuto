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
package org.ccctc.common.droolsengine.facts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes={TestingConfiguration.class, StudentProfileFacade.class,
        NoOpActionService.class, StudentProfileFactsPreProcessor.class})
@TestPropertySource("classpath:application-test.properties")
public class StudentProfileFactsPreProcessorTest {
    private final String DEFAULT_CCCID = "dummy_cccid";
    private final String DEFAULT_CCC_MISCODE = "dummy_miscode";
    private final String DEFAULT_OAUTH_TOKEN = "dummy_oauth_token";
    private MockEnvironment env;
    
    private MockRestServiceServer mockServer = null;

    @Autowired
    private StudentProfileFactsPreProcessor validator;
    
    @Autowired
    private RestTemplate restTemplate = null;
    
    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RequestContextHolder.setRequestAttributes(null);

        env = new MockEnvironment();
        config.setEnvironment(env);
    }
    
    @Test
    public void getCccidNominal() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("cccid", DEFAULT_CCCID);
        assertEquals(validator.getCccid(facts), DEFAULT_CCCID);
    }
    
    @Test
    public void getCccidNotFound() {
        Map<String, Object> facts = new HashMap<String, Object>();
        assertEquals(validator.getCccid(facts), "");
    }

    @Test
    public void getFamilyNominal() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, DEFAULT_CCC_MISCODE);
        String cccMisCode = validator.getFamily(facts);
        assertEquals(DEFAULT_CCC_MISCODE, cccMisCode);
    }
    
    @Test
    public void getFamilyMising() {
        Map<String, Object> facts = new HashMap<String, Object>();
        String cccMisCode = validator.getFamily(facts);
        assertEquals("", cccMisCode);
    }
    
    @Test
    public void validateNominal() {
        // all information is present at the start of validate()
        Map<String, Object> studentProfile = new HashMap<String, Object>();
        studentProfile.put("cccid", DEFAULT_CCCID);

        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, DEFAULT_CCCID);
        facts.put(FactsUtils.MISCODE_FIELD, DEFAULT_CCC_MISCODE);
        facts.put(FactsUtils.STUDENT_PROFILE_FIELD, studentProfile);
        List<String> errors = validator.processFacts(facts);
        assertEquals("No errors should have been returned", 0, errors.size());        
    }
    
    @Test
    public void validateNoCccIdl() {
        // Validate will fail if the CCCID is not present
        Map<String, Object> facts = new HashMap<String, Object>();
        List<String> errors = validator.processFacts(facts);
        assertEquals("Should only have 1 error about missing CCCID", 1, errors.size());
        assertTrue(errors.get(0).startsWith("Cannot retrieve studentProfile with a blank cccid"));
    }
    
    @Test
    public void validateRequestStudentProfileNoMisCodeNoRequestAttributes() {
        // This test will try to find the MIS Code based on the user's logged in session in uPortal
        // The MIS Code call will fail because there is no ServletRequestAttributes associated with
        // the RequestContext (in SecurityUtils)
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, DEFAULT_CCCID);
        List<String> errors = validator.processFacts(facts);
        assertEquals("One error should have been returned", 1, errors.size());   
        assertTrue(errors.get(0).startsWith("Cannot retrieve studentProfile with a blank cccMisCode"));
    }
    
    @Test
    public void validateRequestStudentProfile() {
        // This test will try to retrieve the Student Profile, given all other
        // information is available in the facts already.
        String serverResponseAsJson = this.buildMessagingProfile(DEFAULT_CCCID);

        String studentProfileURI = "https://localhost/student-profile-service/v2/users";
        env.setProperty(DroolsEngineEnvironmentConfiguration.PROFILE_URL_KEY, studentProfileURI);
        mockServer.expect(requestTo(studentProfileURI + "/studentProfiles"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(serverResponseAsJson, MediaType.APPLICATION_JSON));

        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, DEFAULT_CCCID);
        facts.put(FactsUtils.MISCODE_FIELD, DEFAULT_CCC_MISCODE);
        facts.put(FactsUtils.OAUTH_TOKEN_FIELD, DEFAULT_OAUTH_TOKEN);
        List<String> errors = validator.processFacts(facts);
        assertEquals("No errors should have been returned", 0, errors.size());        
        mockServer.verify();
    }

    private String buildMessagingProfile(String cccId) {
        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("cccid", cccId);
        user.put("email", "test@test.com");
        user.put("mainPhone", "480-123-4567");
        user.put("mainPhoneAuth", false);
        user.put("secondPhone", "480-765-4321");
        user.put("secondPhoneAuth", false);
        response.add(user);
        ObjectMapper mapper = new ObjectMapper();
        String serverResponseAsJson = "";
        try {
            serverResponseAsJson = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return serverResponseAsJson;
    }
        
    @Test
    public void validateRequestStudentProfileNoMisCodeWithRequestAttributes() {
        // This test will try to find the MIS Code based on the user's logged in session in uPortal
        // It will then try to retrieve an oauth token from uPortal in that session.
        // The MIS Code will succeed, the OAuth request will succeed, and the 
        // student profile request from the CCCID, MIS Code, and OAuth token will succeed.
        RequestContextHolder.setRequestAttributes(getServletRequestAttributes());
        
        mockServer.expect(requestTo("http://localhost/uPortal/api/cccUserMISCode/"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("", MediaType.TEXT_PLAIN));

        String serverResponseAsJson = this.buildMessagingProfile(DEFAULT_CCCID);
        String studentProfileURI = "https://localhost/student-profile-service/v2/users";
        env.setProperty(DroolsEngineEnvironmentConfiguration.PROFILE_URL_KEY, studentProfileURI);
        mockServer.expect(requestTo(studentProfileURI + "/studentProfiles"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(serverResponseAsJson, MediaType.APPLICATION_JSON));
        
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, DEFAULT_CCCID);
        List<String> errors = validator.processFacts(facts);
        assertEquals("No errors should have been returned", 0, errors.size());        
        mockServer.verify();
    }
    
    @Test
    public void validateRequestStudentProfileProfileWithRequestAttributes() {
        // This test will try to find the MIS Code based on the user's logged in session in uPortal
        // It will then try to retrieve an oauth token from uPortal in that session.
        // The MIS Code will succeed, the OAuth request will succeed, but the 
        // student profile request will fail.
        RequestContextHolder.setRequestAttributes(getServletRequestAttributes());
        String studentProfileURI = "https://localhost/student-profile-service/v2/users";
        env.setProperty(DroolsEngineEnvironmentConfiguration.PROFILE_URL_KEY, studentProfileURI);

        mockServer.expect(requestTo("http://localhost/uPortal/api/cccUserMISCode/"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("", MediaType.TEXT_PLAIN));

        mockServer.expect(requestTo(studentProfileURI + "/studentProfiles"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));
        
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, DEFAULT_CCCID);
        List<String> errors = validator.processFacts(facts);
        assertEquals("One error should have been returned", 1, errors.size());        
        assertTrue(errors.get(0).startsWith("Cannot execute RulesEngine without a Student Profile"));
        mockServer.verify();
    }
    
    @Test
    public void isEnabledHasMisCode() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "ZZ1");
        assertEquals(true, validator.isEnabled(facts));
    }
    
    @Test
    public void isEnabledNoMisCode() {
        Map<String, Object> facts = new HashMap<String, Object>();
        assertEquals(true, validator.isEnabled(facts));
    }
    
    @Test
    public void isEnabledMisingMisCode() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "QQQ");
        env.setProperty(validator.getName() + "_VALIDATOR_MISCODES", "ZZ1");
        assertEquals(1, config.getValidatorMisCodes(validator.getName()).size());
        // only enabled for ZZ1
        assertEquals(false, validator.isEnabled(facts));
    }

    @Test
    public void isEnabledIgnoresMisCodes() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "QQQ");
        assertEquals(0, config.getValidatorMisCodes(validator.getName()).size());
        // enabled because there are no validator_miscodes defined
        assertEquals(true, validator.isEnabled(facts));
    }

    /**
     * getServletRequestAttributes is used whenever the SecurityUtils has to mock the rest template.
     * SecurityUtils wants to retrieve information from the context.
     * @return
     */
    private ServletRequestAttributes getServletRequestAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return requestAttributes;
    }
}
