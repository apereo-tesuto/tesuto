package org.ccctc.common.commonidentity.saml;

import org.junit.Test;
import org.opensaml.saml2.core.NameID;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.saml.SamlIdentityService;

/**
 * Created by Parker Neff - Unicon Inc on 11/4/15.
 */
public class SamlIdentityServiceTest {

   @Test
    public void testLegacyAssertion() {
        SAMLCredential credential = mock(SAMLCredential.class);
        when(credential.getAttributeAsString(SamlIdentityService.LEGACY_CCCID_ATTR)).thenReturn("AAA0001");
        when(credential.getAttributeAsString(SamlIdentityService.LEGACY_FIRST_NAME_ATTR)).thenReturn("Homer");
        when(credential.getAttributeAsString(SamlIdentityService.LEGACY_LAST_NAME_ATTR)).thenReturn("Simpson");


        when(credential.getRemoteEntityID()).thenReturn("https://ci.openccc.net/idp/shibboleth");
        when(credential.getLocalEntityID()).thenReturn("localid");

       NameID nid = mock(NameID.class);
       when(nid.getValue()).thenReturn("mynameid");
       when(credential.getNameID()).thenReturn(nid);


       SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(credential);
        assertNotNull(userDetails.getEppn());
        assertEquals("000",  userDetails.getMisCode());
        assertEquals("AAA0001", userDetails.getCccId());
        assertEquals("Homer", userDetails.getFirstName());
        assertEquals("Simpson", userDetails.getLastName());
        assertEquals("Homer Simpson", userDetails.getDisplayName());
        assertEquals(UserIdentity.AFFLIATION_AFFILATE, userDetails.getPrimaryAffiliation());
        assertEquals("AAA0001@ci.openccc.net", userDetails.getEppn());
        assertTrue(userDetails.isAffiliatedStudent());
        assertFalse(userDetails.isEnrolledStudent());
        assertTrue(userDetails.isStudent());
       assertFalse(userDetails.isStaff());

        assertNotNull(userDetails.getAffiliations());




    }

    @Test
    public void testCollegeAssertionStudent() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsString(SamlIdentityService.FIRST_NAME_ATTR)).thenReturn("Homer");
        when(cred.getAttributeAsString(SamlIdentityService.LAST_NAME_ATTR)).thenReturn("Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.DISPLAY_NAME_ATTR)).thenReturn("Homer Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.PRIMARY_AFFILIATION_ATTR)).thenReturn(UserIdentity.AFFLIATION_STUDENT);
        String[] affliations = {UserIdentity.AFFLIATION_STUDENT};
        when(cred.getAttributeAsStringArray(SamlIdentityService.AFFILIATION_ATTR)).thenReturn(affliations);
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});

        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");

        NameID nameId = mock(NameID.class);
        when(nameId.getValue()).thenReturn("mynameid");
        when(nameId.toString()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nameId);
        when(cred.getRemoteEntityID()).thenReturn("remoteid");
        when(cred.getLocalEntityID()).thenReturn("localid");



        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());

        assertEquals("Homer", userDetails.getFirstName());
        assertEquals("Simpson", userDetails.getLastName());
        assertEquals("Homer Simpson", userDetails.getDisplayName());
        assertEquals(UserIdentity.AFFLIATION_STUDENT, userDetails.getPrimaryAffiliation());
        assertTrue(userDetails.isEnrolledStudent());
        assertFalse(userDetails.isAffiliatedStudent());
        assertTrue(userDetails.isStudent());
        assertFalse(userDetails.isStaff());
        //assertEquals(affliations, userDetails.getAffiliations());



    }

    @Test
    public void testCollegeAssertionStudentStaff() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsString(SamlIdentityService.FIRST_NAME_ATTR)).thenReturn("Homer");
        when(cred.getAttributeAsString(SamlIdentityService.LAST_NAME_ATTR)).thenReturn("Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.DISPLAY_NAME_ATTR)).thenReturn("Homer Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.PRIMARY_AFFILIATION_ATTR)).thenReturn(UserIdentity.AFFLIATION_STUDENT);
        String[] affliations = {UserIdentity.AFFLIATION_STUDENT, UserIdentity.AFFLIATION_STAFF};
        when(cred.getAttributeAsStringArray(SamlIdentityService.AFFILIATION_ATTR)).thenReturn(affliations);
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});

        NameID nameId = mock(NameID.class);
        when(nameId.toString()).thenReturn("mynameid");
        when(nameId.getValue()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nameId);
        when(cred.getRemoteEntityID()).thenReturn("remoteid");
        when(cred.getLocalEntityID()).thenReturn("localid");



        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());

        assertEquals("Homer", userDetails.getFirstName());
        assertEquals("Simpson", userDetails.getLastName());
        assertEquals("Homer Simpson", userDetails.getDisplayName());
        assertEquals(UserIdentity.AFFLIATION_STUDENT, userDetails.getPrimaryAffiliation());
        assertTrue(userDetails.isEnrolledStudent());
        assertFalse(userDetails.isAffiliatedStudent());
        assertTrue(userDetails.isStudent());
        assertFalse(userDetails.isStaff());
        //assertEquals(affliations, userDetails.getAffiliations());



    }
    @Test
    public void testCollegeAssertionStaff() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsString(SamlIdentityService.FIRST_NAME_ATTR)).thenReturn("Homer");
        when(cred.getAttributeAsString(SamlIdentityService.LAST_NAME_ATTR)).thenReturn("Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.DISPLAY_NAME_ATTR)).thenReturn("Homer Simpson");
        when(cred.getAttributeAsString(SamlIdentityService.PRIMARY_AFFILIATION_ATTR)).thenReturn(UserIdentity.AFFLIATION_STUDENT);
        String[] affliations = {UserIdentity.AFFLIATION_STAFF};
        when(cred.getAttributeAsStringArray(SamlIdentityService.AFFILIATION_ATTR)).thenReturn(affliations);
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");

        NameID nameId = mock(NameID.class);
        when(nameId.toString()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nameId);
        when(cred.getRemoteEntityID()).thenReturn("remoteid");
        when(cred.getLocalEntityID()).thenReturn("localid");



        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());

        assertEquals("Homer", userDetails.getFirstName());
        assertEquals("Simpson", userDetails.getLastName());
        assertEquals("Homer Simpson", userDetails.getDisplayName());
        assertEquals(UserIdentity.AFFLIATION_STUDENT, userDetails.getPrimaryAffiliation());
        assertFalse(userDetails.isEnrolledStudent());
        assertFalse(userDetails.isAffiliatedStudent());
        assertFalse(userDetails.isStudent());
        assertTrue(userDetails.isStaff());




    }
    @Test
    public void testCccId() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});

        NameID nid = mock(NameID.class);
        when(nid.getValue()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nid);


        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());


    }
    @Test
    public void testCccIdOld() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");
        when(cred.getAttributeAsString(SamlIdentityService.OLD_CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn(null);
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});

        NameID nid = mock(NameID.class);
        when(nid.getValue()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nid);

        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());


    }
    @Test
    public void testCccIdBoth() {
        SAMLCredential cred = mock(SAMLCredential.class);
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");
        when(cred.getAttributeAsString(SamlIdentityService.OLD_CCCID_ATTR)).thenReturn("AAA0002");
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});


        NameID nid = mock(NameID.class);
        when(nid.getValue()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nid);

        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals("AAA0001", userDetails.getCccId());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FOO")));
    }

    @Test
    public void testAuthSource() {
        SAMLCredential cred = mock(SAMLCredential.class);
        final String testSource = "FOOBAR";
        when(cred.getAttributeAsString(SamlIdentityService.AUTH_SOURCE_ATTR)).thenReturn(testSource);
        when(cred.getAttributeAsString(SamlIdentityService.EPPN_ATTR)).thenReturn("hsimson@santarosa.edu");
        when(cred.getAttributeAsString(SamlIdentityService.OLD_CCCID_ATTR)).thenReturn("AAA0002");
        when(cred.getAttributeAsString(SamlIdentityService.CCCID_ATTR)).thenReturn("AAA0001");
        when(cred.getAttributeAsStringArray(SamlIdentityService.ENTITLEMENT_ATTR)).thenReturn(new String[]{"ROLE_FOO"});


        NameID nid = mock(NameID.class);
        when(nid.getValue()).thenReturn("mynameid");
        when(cred.getNameID()).thenReturn(nid);

        SamlIdentityService service = new SamlIdentityService();
        UserIdentity userDetails = (UserIdentity)service.loadUserBySAML(cred);

        assertEquals(testSource, userDetails.getAuthSource());
    }
}
