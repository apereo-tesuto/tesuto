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
package org.cccnext.tesuto.web.security;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.cccnext.tesuto.web.service.UserAccountDtoUserIdentityAssembler;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-test.xml" })
//@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
//@IntegrationTest("server.port:0")
@Transactional
public class SAMLAuthenticationSuccessHandlerTest {
    /* TODO: The Spring bean values are null right now:
     * @Autowired UserAccountDtoUserIdentityAssembler userAccountDtoUserIdentityAssembler;
     * @Autowired UserAccountService UserAccountService;
     */
    @Resource(name = "samlAuthenticationSuccessHandler")
    SAMLAuthenticationSuccessHandler samlAuthenticationSuccessHandler;
    Authentication authentication;
    @Resource(name = "userAccountDtoUserIdentityAssembler")
    UserAccountDtoUserIdentityAssembler userAccountDtoUserIdentityAssembler;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testClearFailedLogins() throws Exception {
        // Nothing to test.  Emtpy method for this implementation.
    }

    /**
     * Utility method to mock out an IDP authentication
     * @param userAccountId example "1" or "2"
     * @param affiliations example {"student"} or {"staff"}
     */
    public void mockOutIdpAuthentication(String userAccountId, String[] affiliations) {
        // Mock out a student IDP authentication
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        authentication = new TestingAuthenticationToken("someUser", "somePassword");
        authentication.setAuthenticated(true);
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEppn(userAccountId);
        userIdentity.setCccId(userAccountId);
        userIdentity.setAffiliations(affiliations);
        ((TestingAuthenticationToken) authentication).setDetails(userIdentity);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testOnAuthenticationSuccess() throws Exception {
        // Only calls other methods which are tested.
    }

    @Test
    public void testClearFailedLogins1() throws Exception {
        // Nothing to test.  Emtpy method for this implementation.
    }

    @Test
    public void testHandle() throws Exception {
        // TODO: Probably worth a test at some point.
    }

    @Test
    public void testGetGrantedAuthorities1() throws Exception {

    }

    @Test
    public void testClearAuthenticationAttributes() throws Exception {
        // Not implemented for this
    }

    @Test
    public void testSamlAffiliationResult() throws Exception {
        // Test if they are of the faculty essence
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"staff"});
        SAMLAuthenticationSuccessHandler.SamlAuthResults samlAuthResults =
                samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertFalse(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test if they are of the student essance
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"student"});
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test the case of being both a student and faculty
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"student", "faculty"});
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test the case are a student without a CCCID
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"affiliate"});
        // Manually clear the CCCID
        UserIdentity userIdentity = (UserIdentity) authentication.getDetails();
        userIdentity.setCccId(null);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertFalse(samlAuthResults.isCccIdFlag());

        // Test the case are both a student and faculty without a CCCID
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"alum", "employee"});
        // Manually clear the CCCID
        userIdentity = (UserIdentity) authentication.getDetails();
        userIdentity.setCccId(null);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertFalse(samlAuthResults.isCccIdFlag());
    }
    @Test
    public void testInvalidSamlAffiliationResult() throws Exception {
        // Test if they are non valid affiliations
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"nothingvalid"});
        SAMLAuthenticationSuccessHandler.SamlAuthResults samlAuthResults =
                samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertFalse(samlAuthResults.isStudentFlag());

        // Test if affiliations are empty
        mockOutIdpAuthentication("scotty@democollege.edu", new String[0]);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertFalse(samlAuthResults.isStudentFlag());

        // Test if the affiliation is null
        mockOutIdpAuthentication("scotty@democollege.edu", null);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertFalse(samlAuthResults.isStudentFlag());

    }
    
    @Test
    public void testCaseInsensitiveSamlAffiliationResult() throws Exception {
        // Test if they are of the faculty essence
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"StAff"});
        SAMLAuthenticationSuccessHandler.SamlAuthResults samlAuthResults =
                samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertFalse(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test if they are of the student essance
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"Student"});
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test the case of being both a student and faculty
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"stuDent", "Faculty"});
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertTrue(samlAuthResults.isCccIdFlag());

        // Test the case are a student without a CCCID
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"AFFILIATE"});
        // Manually clear the CCCID
        UserIdentity userIdentity = (UserIdentity) authentication.getDetails();
        userIdentity.setCccId(null);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertFalse(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertFalse(samlAuthResults.isCccIdFlag());

        // Test the case are both a student and faculty without a CCCID
        mockOutIdpAuthentication("scotty@democollege.edu", new String[]{"alum", "employee"});
        // Manually clear the CCCID
        userIdentity = (UserIdentity) authentication.getDetails();
        userIdentity.setCccId(null);
        samlAuthResults = samlAuthenticationSuccessHandler.samlAffiliationResult((UserIdentity) authentication.getDetails());
        Assert.assertTrue(samlAuthResults.isFacultyFlag());
        Assert.assertTrue(samlAuthResults.isStudentFlag());
        Assert.assertFalse(samlAuthResults.isCccIdFlag());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateParsesSingleParameterProperly() throws Exception {
        String relayState = "/student?cccMisCode=ZZ3";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student", result.getLeft());
        Assert.assertEquals("ZZ3" , result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateParsesMultipleMisCodesProperly() throws Exception {
        String relayState = "/student?cccMisCode=ZZ3&cccMisCode=123";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student", result.getLeft());
        Assert.assertEquals("123" , result.getRight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesMultipleQuestionMarksProperly() throws Exception {
        String relayState = "/student?cccMisCode=?ZZ3";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesMultipleEqualsProperly() throws Exception {
        String relayState = "/student?cccMisCode=Z=Z3";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student", result.getLeft());
        Assert.assertEquals("Z=Z3" , result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesMultipleAmpersandsProperly() throws Exception {
        String relayState = "/student?some=thing&cccMisCode=ZZ3&&&foo=bar";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student?some=thing&foo=bar", result.getLeft());
        Assert.assertEquals("ZZ3" , result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesNoParametersProperly() throws Exception {
        String relayState = "/student";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student", result.getLeft());
        Assert.assertEquals(null, result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesNoMisCodeProperly() throws Exception {
        String relayState = "/student?some=thing&foo=bar";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student?some=thing&foo=bar", result.getLeft());
        Assert.assertEquals(null , result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesEmptyParameterNameProperly() throws Exception {
        String relayState = "/student?some=thing&=bar";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student?some=thing", result.getLeft());
        Assert.assertEquals(null , result.getRight());
    }

    @Test
    public void testParseTargetUrlAndMisCodeFromRelayStateHandlesEmptyParameterValueProperly() throws Exception {
        String relayState = "/student?some=thing&foo=";
        ImmutablePair result = samlAuthenticationSuccessHandler.parseTargetUrlAndMisCodeFromRelayState(relayState);
        Assert.assertEquals("/student?some=thing&foo=", result.getLeft());
        Assert.assertEquals(null , result.getRight());
    }
}
