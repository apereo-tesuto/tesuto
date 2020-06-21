package org.ccctc.common.commonidentity.saml;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.Statement;
import org.opensaml.saml2.core.impl.AuthnStatementImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SamlIdentityService implements SAMLUserDetailsService {
    public static final String EPPN_ATTR = "urn:oid:1.3.6.1.4.1.5923.1.1.1.6";
    public static final String OLD_CCCID_ATTR = "urn:oid:1.3.6.1.4.1.9923.1.1.1.1";
    public static final String CCCID_ATTR = "https://www.openccc.net/saml/attributes/cccId";
    public static final String MIS_ATTR = "urn:oid:1.3.6.1.4.1.9924.1.1.1.2";
    public static final String AFFILIATION_ATTR = "urn:oid:1.3.6.1.4.1.5923.1.1.1.1";
    public static final String ENTITLEMENT_ATTR = "urn:oid:1.3.6.1.4.1.5923.1.1.1.7";
    public static final String PRIMARY_AFFILIATION_ATTR = "urn:oid:1.3.6.1.4.1.5923.1.1.1.5";
    public static final String FIRST_NAME_ATTR = "urn:oid:2.5.4.42";
    public static final String LAST_NAME_ATTR = "urn:oid:2.5.4.4";
    public static final String DISPLAY_NAME_ATTR = "urn:oid:2.16.840.1.113730.3.1.241";

    public static final String LEGACY_CCCID_ATTR = "urn:oid:0.9.2342.19200300.100.1.1";
    public static final String LEGACY_FIRST_NAME_ATTR = "urn:mace:dir:attribute-def:cccEduFirstName";
    public static final String LEGACY_LAST_NAME_ATTR = "urn:mace:dir:attribute-def:cccEduLastName";
    public static final String LEGACY_EMAIL_ATTR = "urn:oid:0.9.2342.19200300.100.1.3";
    public static final String LEGACY_PHONE_ATTR = "urn:mace:dir:attribute-def:telephoneNumber";
    public static final String LEGACY_MOBILE_PHONE_ATTR = "urn:mace:dir:attribute-def:cccEduSecondPhone";

    public static final String MIS_CODE_PARAM = "cccMisCode";
    public static final String AUTH_SOURCE_ATTR = "https://www.openccc.net/saml/attributes/cccAuthSource";

    @Value("${idp.eppnservice.url}")
    private String eppnMapUrl = "";

    RestTemplate restTemplate = new RestTemplate();

    private HashMap<String, EPPNData> eppnByMISCode = new HashMap<>();
    private HashMap<String, String> issuerToSource = new HashMap<>();

    @PostConstruct
    public void getEPPNData() {
        List<EPPNData> data;
        try {
            data = restTemplate.exchange(
                    eppnMapUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EPPNData>>() {}
            ).getBody();
        } catch (Exception e ) {
            log.error("Could not get EPPN suffix data for IDP Proxy SSO", e);
            return;
        }

        log.debug("Queried " + eppnMapUrl + " for eppn data and got "+ data.size() +" entries.");

        // Stuff data into map
        for(EPPNData d : data) {
            for (String mis : d.getMisCodes()) {
                eppnByMISCode.put(mis, d);
            }
            issuerToSource.put(d.getIdpEntityId(), d.getAuthSource());
        }
    }

    /**
     * The method is supposed to identify local account of user referenced by data in the SAML assertion
     * and return UserDetails object describing the user. In case the user has no local account, implementation
     * may decide to create one or just populate UserDetails object with data from assertion.
     * <p>
     * Returned object should correctly implement the getAuthorities method as it will be used to populate
     * entitlements inside the Authentication object.
     *
     * @param credential data populated from SAML message used to validate the user
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user details object can't be populated
     * =AAA0006
     * =Neff
     */

    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {


        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Assertion Data");
            //  List<Attribute> attributes = credential.getAttributes();
            for (Attribute attr : credential.getAttributes()) {
                String attrName = attr.getName();

                String friendlyName = attr.getFriendlyName();
                String attrValue = credential.getAttributeAsString(attrName);
                sb.append("\n");
                sb.append(attrName);
                sb.append(" (");
                sb.append(friendlyName);
                sb.append("):\t");
                sb.append(attrValue);
            }

            sb.append("\nRemote Entity ID:\t");
            sb.append(credential.getRemoteEntityID());
            sb.append("\nLocal Entity ID:\t");
            sb.append(credential.getLocalEntityID());

            log.debug(sb.toString());
        }

        SAMLUserIdentity id;
        if (isLegacyAssertion(credential)) {
            if (log.isDebugEnabled()) log.debug("IS Legacy Assertion");
            id = getLegacyAssertions(credential);
        } else {
            if (log.isDebugEnabled()) log.debug("IS V3 Assertion");
            id = getIDPV3Assertions(credential);
        }

        // Set authSource from idp proxy or original authenticating authority lookup
        final String authSourceFromProxy = credential.getAttributeAsString(AUTH_SOURCE_ATTR);
        if (!StringUtils.isEmpty(authSourceFromProxy)) {
            id.setAuthSource(authSourceFromProxy);
        } else if (credential.getAuthenticationAssertion() != null && credential.getAuthenticationAssertion().getIssuer() != null) {
            String issuer = getOriginialAuthenticatingAuthority(credential);
            String authSource = issuerToSource.get(issuer);
            log.debug("Setting UserIdentity authSource to \"{}\" for issuer \"{}\"", authSource, issuer);
            id.setAuthSource(authSource);
        }

        id.setSamlCredential(credential);

        if (credential.getNameID() != null) {
            id.setNameID(credential.getNameID().getValue());
        }
        UserIdentity.updateUniqueID(id);

        String rs = credential.getRelayState();
        log.debug("Relay state from SAML credential: {}", rs);

        if (rs != null) {
            Map<String, List<String>> m = getQueryParams(rs);

            log.debug("Param map: {}", m);

            // The RelayState-based MIS Code is currently authoritative
            if(m.containsKey(MIS_CODE_PARAM)) {
                id.setMisCode(m.get(MIS_CODE_PARAM).get(0));
            }
        }

        return id;
    }

    /**
     * urn:oid:1.3.6.1.4.1.5923.1.1.1.1=applicant
     * =Parker
     * urn:mace:dir:attribute-def:cccEduSSNType=1
     * urn:mace:dir:attribute-def:cccEduCity=Chico
     * urn:mace:dir:attribute-def:cccEduMainPhoneAuth=Y
     * urn:mace:dir:attribute-def:cccEduPostalCode=91333
     * urn:mace:dir:attribute-def:cccEduCountry=US
     * urn:mace:dir:attribute-def:cccEduState=CA
     * urn:mace:dir:attribute-def:cccEduBirthDate=1963-02-06
     * urn:mace:dir:attribute-def:cccEduHasSSN=f
     * urn:mace:dir:attribute-def:cccEduSecondPhoneAuth=N
     * urn:oid:0.9.2342.19200300.100.1.3=jsmith@unicon.net
     * urn:mace:dir:attribute-def:telephoneNumber=480-252-0936
     * urn:oid:0.9.2342.19200300.100.1.2=Joe Smith
     * urn:mace:dir:attribute-def:cccEduStreetAddress1=1234 Smith St
     * @param credential
     * @return The UserIdentity object that may be used in Spring Security and MitreID.
     */
    private SAMLUserIdentity getLegacyAssertions(SAMLCredential credential) {
        SAMLUserIdentity userDetails = new SAMLUserIdentity();

        userDetails.setCccId(credential.getAttributeAsString(LEGACY_CCCID_ATTR));
        userDetails.setFirstName(credential.getAttributeAsString(LEGACY_FIRST_NAME_ATTR));
        userDetails.setLastName(credential.getAttributeAsString(LEGACY_LAST_NAME_ATTR));
        userDetails.setEmail(credential.getAttributeAsString(LEGACY_EMAIL_ATTR));
        userDetails.setPhone(credential.getAttributeAsString(LEGACY_PHONE_ATTR));
        userDetails.setMobilePhone(credential.getAttributeAsString(LEGACY_MOBILE_PHONE_ATTR));

        // In the OpenCCC IDP, the only affiliation is 'affliate'
        userDetails.setAffiliations(new String[]{UserIdentity.AFFLIATION_AFFILATE});
        userDetails.setPrimaryAffiliation(UserIdentity.AFFLIATION_AFFILATE);
        userDetails.setEppn(extractLegacyEppn(credential));
        userDetails.setMisCode("000");

        return userDetails;
    }


    private SAMLUserIdentity getIDPV3Assertions(SAMLCredential credential) {
        /*
        urn:oid:0.9.2342.19200300.100.1.1
        urn:oid:0.9.2342.19200300.100.1.1 (uid):        rreves
urn:oid:1.3.6.1.4.1.5923.1.1.1.1 (eduPersonAffiliation):        student
urn:oid:1.3.6.1.4.1.5923.1.1.1.6 (eduPersonPrincipalName):      rreves@democollege.edu
urn:oid:1.3.6.1.4.1.5923.1.1.1.5 (eduPersonPrimaryAffiliation): student
 (cccId):       AAA1237
urn:oid:2.5.4.4 (sn):   Reves
urn:oid:2.5.4.42 (givenName):   Rose
urn:oid:2.16.840.1.113730.3.1.241 (displayName):        Rose Reves
urn:oid:1.3.6.1.4.1.9924.1.1.1.2 (cccMisCode):  941

         */
        SAMLUserIdentity userDetails = new SAMLUserIdentity();

        userDetails.setEppn(credential.getAttributeAsString(EPPN_ATTR));
        // Just in case this is an old IDP, use the old cccid attribute, but replace it with
        // new one if it exists
        if (StringUtils.isNotEmpty(credential.getAttributeAsString(CCCID_ATTR))) {
            userDetails.setCccId(credential.getAttributeAsString(CCCID_ATTR));

        } else if (StringUtils.isNotEmpty(credential.getAttributeAsString(OLD_CCCID_ATTR))){
            userDetails.setCccId(credential.getAttributeAsString(OLD_CCCID_ATTR));
        }

        userDetails.setFirstName(credential.getAttributeAsString(FIRST_NAME_ATTR));
        userDetails.setLastName(credential.getAttributeAsString(LAST_NAME_ATTR));
        userDetails.setMisCode(credential.getAttributeAsString(MIS_ATTR));
        userDetails.setPrimaryAffiliation(credential.getAttributeAsString(PRIMARY_AFFILIATION_ATTR));

        userDetails.setAffiliations(credential.getAttributeAsStringArray(AFFILIATION_ATTR));
        userDetails.setEntitlements(credential.getAttributeAsStringArray(ENTITLEMENT_ATTR));
        return userDetails;
    }

    private boolean isLegacyAssertion(SAMLCredential cred) {
        return (cred.getAttributeAsString(EPPN_ATTR) == null);
    }

    private String extractLegacyEppn(SAMLCredential cred) {
        String cccId = cred.getAttributeAsString(LEGACY_CCCID_ATTR);
        try {
            URL url = new URL(cred.getRemoteEntityID());
            return cccId + "@" + url.getHost();
        } catch (MalformedURLException e) {
            return cccId + "@unknown.net";
        }
    }

    /**
     * Get the query parameters as a map, given a query fragment (intended for
     * @param rs
     * @return
     */
    public static Map<String, List<String>> getQueryParams(String rs) {
        HashMap<String, List<String>> m = new HashMap<>();

        int from = rs.indexOf("?");
        if (from == -1) {
            return m;
        }

        int to = rs.indexOf("#");
        if (to < 0) {
            to = rs.length();
        }

        rs = rs.substring(from+1, to);

        for (String kv : rs.split("&")) {
            String[] split = kv.split("=");
            if (split.length < 2) {
                continue;
            }
            if(!m.containsKey(split[0])) {
                m.put(split[0], new ArrayList<String>());
            }
            m.get(split[0]).add(split[1]);
        }

        return m;
    }

    public String getOriginialAuthenticatingAuthority(SAMLCredential credential) {
        if (credential == null) {
            throw new IllegalArgumentException("SAMLCredential cannot be null");
        }

        List<Statement> statements = credential.getAuthenticationAssertion().getStatements();

        if (!statements.isEmpty() && statements.get(0) instanceof org.opensaml.saml2.core.impl.AuthnStatementImpl) {
            AuthnStatementImpl stmt = (AuthnStatementImpl)statements.get(0);
            if (stmt.getAuthnContext() != null && stmt.getAuthnContext().getAuthenticatingAuthorities() != null
                    && !stmt.getAuthnContext().getAuthenticatingAuthorities().isEmpty()) {
                return stmt.getAuthnContext().getAuthenticatingAuthorities().get(0).getURI();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
