package org.ccctc.web.client.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.datamodel.Application;
import org.ccctc.common.datamodel.StudentContact;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * The MyPathStudentProfileServiceClient encapsulates the logic for making service calls to the MyPath SPS rest services.
 * This will check the standard spring - application.properties file for the needed properties and alternately (and overridding)
 * then look for spsService.properties
 * 
 * This class needs Spring Core and Commons-Lang.
 * Logging through SLF4j
 * 
 * Configuration Properties and default values:
 * 
 * sps.url=https://gateway.{{ENV}}.cccmypath.org/student-profile-service/
 * oauth.client.id = acme
 * oauth.client.secret = acmesecret
 * oauth.provider.url = http://localhost:8080/openid-connect-server-webapp/
 * deployment.env = ci (should be one of: [ci|test|pilot] - prod should be blank!)
 * 
 */
@Service
@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:spsService.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/opt/ccc/config/spsService.properties", ignoreResourceNotFound = true)
@Slf4j
public class MyPathStudentProfileServiceClient extends AbstractServiceClient {   
    public MyPathStudentProfileServiceClient() {}

    private Map<String, Object> applicationToMap(Application application) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("appId", application.getAppId());
        result.put("appLang", application.getAppLang());
        result.put("cccId", application.getCccId());
        result.put("collegeId", application.getCollegeId());
        result.put("confirmation", application.getConfirmation());
        result.put("eduGoal", application.getEduGoal());
        result.put("highestEduLevel", application.getHighestEduLevel());
        result.put("intendedMajor", application.getIntendedMajor());
        result.put("majorCategory", application.getMajorCategory());
        result.put("majorCode", application.getMajorCode());
        result.put("termCode", application.getTermCode());
        return result;
    }

    private Map<String, Object> contactToMap(StudentContact contact) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("cccId", contact.getCccId());
        result.put("city", contact.getCity());
        result.put("country", contact.getCountry());
        result.put("email", contact.getEmail());
        result.put("mainphone", contact.getMainphone());
        result.put("mainphoneExt", contact.getMainphoneExt());
        result.put("mainphoneintl", contact.getMainphoneintl());
        result.put("nonusaprovince", contact.getNonusaprovince());
        result.put("permCity", contact.getPermCity());
        result.put("permCountry", contact.getPermCountry());
        result.put("permNonusaprovince", contact.getPermNonusaprovince());
        result.put("permPostalCode", contact.getPermPostalcode());
        result.put("permState", contact.getPermState());
        result.put("permStreetaddress1", contact.getPermStreetaddress1());
        result.put("permStreetaddress2", contact.getPermStreetaddress2());
        result.put("postalCode", contact.getPostalcode());
        result.put("secondphone", contact.getSecondphone());
        result.put("secondphoneExt", contact.getSecondphoneExt());
        result.put("secondphoneintl", contact.getSecondphoneintl());
        result.put("state", contact.getState());
        result.put("streetaddress1", contact.getStreetaddress1());
        result.put("streetaddress2", contact.getStreetaddress2());
        result.put("zip4", contact.getZip4());
        return result;
    }

    protected Map<String, Object> createAttributesSetParamsMap(String cccId, String misCode, String source, String description,
                    Map<String, Object> attributes) {
        Map<String, Object> result = new HashMap<>();
        result.put("cccId", cccId);
        result.put("misCode", misCode);
        result.put("source", source);
        result.put("description", description);
        result.put("attributes", attributes);
        return result;
    }

    private String getAttributesMapsUrl(String cccId, String misCode) {
        return serviceUrl + String.format("v3/users/%s/misCode/%s/attributes/maps", cccId, misCode);
    }

    private String getAttributesMapsUrl(String cccId, String misCode, String source) {
        return this.getAttributesMapsUrl(cccId, misCode) + "/source/" + source;
    }

    private String getAttributesMapsUrl(String cccId, String misCode, String source, String description) {
        return this.getAttributesMapsUrl(cccId, misCode, source) + "/desc/" + description;
    }

    private String getAttributesSetsUrl(String cccId, String misCode, String source, String description) {
        return serviceUrl + String.format("v3/users/%s/misCode/%s/attributes/sets/source/%s/desc/%s", cccId, misCode, source, description);
    }

    public String getStatus() {
        String clientId = environment.getProperty("oauth.client.id", "acme");
        return "Using [serviceURL]: " + serviceUrl + " , [oauthProviderURL]: " + oauthProviderUrl + " , [clientId]: " + clientId
                        + ", [serviceURL]: " + serviceUrl;
    }

    /**
     * SPS always returns a result, so we don't have to check the status code for not found.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getV2BasicProfile(String cccId) {
        String url = serviceUrl + "v2/users/" + cccId;
        log.debug("Attempting to fetch simple profile from: " + url);
        List<Map<String, Object>> results = new ArrayList<>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, url, null);
        results = (List<Map<String, Object>>) re.getBody();
        return stripNullValues(results.isEmpty() ? new HashMap<String, Object>() : results.get(0));
    }

    /**
     * Messaging profiles include the fields needed by the messaging service to send messages.
     */
    public Map<String, Object> getV2MessagingProfileData(String cccId) {
        return getV2MessagingProfileData(Arrays.asList(cccId));
    }
    
    /**
     * Messaging profiles include the fields needed by the messaging service to send messages.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getV2MessagingProfileData(List<String> cccIds) {
        String url = serviceUrl + "v2/users/messagingStudentProfiles";
        log.debug("Attempting to fetch messaging profile from: " + url);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cccids", cccIds);

        List<Map<String, Object>> results = new ArrayList<>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, url, params);
        results = (List<Map<String, Object>>) re.getBody();
        return stripNullValues(results.isEmpty() ? new HashMap<String, Object>() : results.get(0));
    }

    @Override
    protected void init(final Environment environment) {
        super.init(environment);
        serviceUrl = adjustForEnv(environment.getProperty("sps.url", "https://gateway.{{ENV}}.cccmypath.org/student-profile-service/"));
        buildServiceAccountManager(environment);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> v3AddApplicationToProfile(Application application) {
        String url = serviceUrl + "v3/users/" + application.getCccId() + "/application";
        log.debug("Attempting to to send application to: " + url);

        Map<String, Object> results = new HashMap<>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, url, applicationToMap(application));
        results = (Map<String, Object>) re.getBody();
        return stripNullValues(results);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> v3AddAttributesSet(String cccId, String misCode, String source, String description,
                    Map<String, Object> attributes) {
        final String url = this.getAttributesSetsUrl(cccId, misCode, source, description);
        log.debug("Attempting to add attributes set at: {}", url);
        final ResponseEntity<Map<String, Object>> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, url,
                        this.createAttributesSetParamsMap(cccId, misCode, source, description, attributes));
        final Map<String, Object> results = re.getBody();
        log.debug("Returning: {}", results);
        return results;
    }

    /**
     * @return the list of associated school codes.
     *         **NOTE: ignores 000
     */
    @SuppressWarnings("unchecked")
    public List<String> v3AddCollegeAssociation(String cccId, String misCode) {
        String url = serviceUrl + "v3/users/addAssoc/" + cccId + "/" + misCode;
        log.debug("Attempting to to add association for college using: " + url);

        List<String> results = new ArrayList<String>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.PUT, url, new HashMap<>());
        results = (List<String>) re.getBody();
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> v3CreatOrUpdateProfile(StudentContact contact) {
        String url = serviceUrl + "v3/users/" + contact.getCccId() + "/application";
        log.debug("Attempting to to send application to: " + url);

        Map<String, Object> results = new HashMap<>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, url, contactToMap(contact));
        results = (Map<String, Object>) re.getBody();
        return stripNullValues(results);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> v3GetAttributesMap(String cccId, String misCode, String source, String description) {
        String url = this.getAttributesMapsUrl(cccId, misCode, source, description);
        log.debug("Attempting to retrieve attributes at: " + url);
        final ResponseEntity<Map<String, Object>> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, url, null);
        final Map<String, Object> results = re.getBody();
        log.debug("Returning: {}", results);
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, Object>>> v3GetAttributesMaps(String cccId, String misCode) {
        String url = this.getAttributesMapsUrl(cccId, misCode);
        log.debug("Attempting to retrieve attributes at: " + url);
        final ResponseEntity<Map<String, Map<String, Map<String, Object>>>> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, url, null);
        final Map<String, Map<String, Map<String, Object>>> results = re.getBody();
        log.debug("Returning: {}", results);
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> v3GetAttributesMaps(String cccId, String misCode, String source) {
        String url = this.getAttributesMapsUrl(cccId, misCode, source);
        log.debug("Attempting to retrieve attributes at: " + url);
        final ResponseEntity<Map<String, Map<String, Object>>> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, url, null);
        final Map<String, Map<String, Object>> results = re.getBody();
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> v3UpdateAttributesSet(String cccId, String misCode, String source, String description, Map<String, Object> attributes) {
        final String url = this.getAttributesSetsUrl(cccId, misCode, source, description);
        log.debug("Attempting to update attributes set at: {}", url);
        final ResponseEntity<Map<String, Object>> re = restHandler.makeAuthenticatedRestCall(HttpMethod.PUT, url,
                        this.createAttributesSetParamsMap(cccId, misCode, source, description, attributes));
        final Map<String, Object> results = re.getBody();
        log.debug("Returning: {}", results);
        return results;
    }

    /**
     * This main is not the expected use, but rather a quick demo of the results of using the class to fetch application details.
     * Out of the box, this will work for the CI env and return the (fake) student data (real data from CI, not a real person).
     */
    public static void main(String args[]) {
        MyPathStudentProfileServiceClient client = new MyPathStudentProfileServiceClient();
        client.setEnvironment(new MockEnvironment());
        System.out.println(client.getV2MessagingProfileData("AAA5364"));
    }
}
