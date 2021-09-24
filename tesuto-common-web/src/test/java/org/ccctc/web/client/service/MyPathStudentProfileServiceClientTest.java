package org.ccctc.web.client.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ccctc.web.client.rest.CCCRestCallHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyPathStudentProfileServiceClientTest {

    private static final String PROFILE_SERVICE_BASE_URL_FORMAT =
            "https://gateway.ci.cccmypath.org/student-profile-service/v3/users/%s/misCode/%s";
    private static final String ATTRIBUTES_MAPS_URL_FORMAT =
            PROFILE_SERVICE_BASE_URL_FORMAT + "/attributes/maps/source/%s/desc/%s";
    private static final String ATTRIBUTES_SETS_URL_FORMAT =
            PROFILE_SERVICE_BASE_URL_FORMAT + "/attributes/sets/source/%s/desc/%s";

    private MyPathStudentProfileServiceClient client = new MyPathStudentProfileServiceClient();

    private String cccId;
    private String misCode;
    private String attributesSource;
    private String attributesDescription;
    private String attributesSetsUrl;
    private String attributesMapsUrl;

    @Before
    public void setup() {
        this.cccId = "AAA123";
        this.misCode = "999";
        this.attributesSource = "APPLY";
        this.attributesDescription = "Fall2018";
        this.attributesMapsUrl = String.format(ATTRIBUTES_MAPS_URL_FORMAT, this.cccId, this.misCode, this.attributesSource, this.attributesDescription);
        this.attributesSetsUrl = String.format(ATTRIBUTES_SETS_URL_FORMAT, this.cccId, this.misCode, this.attributesSource, this.attributesDescription);
        CCCRestCallHandler restHandler = mock(CCCRestCallHandler.class);
        client.restHandler = restHandler;
        client.setEnvironment(new MockEnvironment());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGetV2BasicProfile() throws Exception {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(HttpMethod.GET,
                        "https://gateway.ci.cccmypath.org/student-profile-service/v2/users/someid", null)).thenReturn(re);
        Object result = convertToListOfMaps("src/test/resources/student1.json");
        when(re.getBody()).thenReturn(result);

        Map<String, Object> map = client.getV2BasicProfile("someid");
        Arrays.asList("age", "cccid", "email", "firstName", "lastName", "phone", "collegeStatuses")
                        .forEach(key -> Assert.assertTrue("Missing key: " + key, map.containsKey(key)));
        Assert.assertFalse("Should not have middle name as it was null", map.containsKey("middleName"));
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testGetV2BasicProfileStudent2() throws Exception {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(HttpMethod.GET,
                        "https://gateway.ci.cccmypath.org/student-profile-service/v2/users/someid", null)).thenReturn(re);
        Object result = convertToListOfMaps("src/test/resources/student2.json");
        when(re.getBody()).thenReturn(result);

        Map<String, Object> map = client.getV2BasicProfile("someid");
        Arrays.asList("age", "cccid", "email", "firstName", "lastName", "phone", "collegeStatuses", "middleName")
                        .forEach(key -> Assert.assertTrue("Missing key: " + key, map.containsKey(key)));
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void testGetV2BasicProfileStudentNotFound() throws Exception {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(HttpMethod.GET,
                        "https://gateway.ci.cccmypath.org/student-profile-service/v2/users/someid", null)).thenReturn(re);
        when(re.getBody()).thenReturn(new ArrayList<>());

        Map<String, Object> map = client.getV2BasicProfile("someid");
        Assert.assertTrue("There should be no results found", map.isEmpty());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testV3GetAttributesMap() throws Exception {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(
                HttpMethod.GET,
                this.attributesMapsUrl,
                null)).thenReturn(re);
        when(re.getBody()).thenReturn(Collections.singletonMap("foo", "bar"));
        Map<String, Object> results =
                client.v3GetAttributesMap(this.cccId, this.misCode, this.attributesSource, this.attributesDescription);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("bar", results.get("foo"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testV3AddAttributesSet() throws Exception {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        attributes.put("abc", "def");
        final CCCRestCallHandler restHandler = client.restHandler;
        final ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(
                HttpMethod.POST,
                this.attributesSetsUrl,
                this.createAttributesSetParamsMap(attributes))).thenReturn(re);
        when(re.getBody()).thenReturn(attributes);
        final Map<String, Object> results = client.v3AddAttributesSet(
                this.cccId, this.misCode, this.attributesSource, this.attributesDescription, attributes);
        Assert.assertTrue("There attributes map should not be empty", !results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("bar", results.get("foo"));
        Assert.assertEquals("def", results.get("abc"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testV3UpdateAttributesForSet() throws Exception {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        attributes.put("abc", "def");
        final CCCRestCallHandler restHandler = client.restHandler;
        final ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(
                HttpMethod.PUT,
                this.attributesSetsUrl,
                this.createAttributesSetParamsMap(attributes))).thenReturn(re);
        when(re.getBody()).thenReturn(attributes);
        final Map<String, Object> results = client.v3UpdateAttributesSet(
                this.cccId, this.misCode, this.attributesSource, this.attributesDescription, attributes);
        Assert.assertTrue("There attributes map should not be empty", !results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("bar", results.get("foo"));
        Assert.assertEquals("def", results.get("abc"));
    }

    private List<Map<String, Object>> convertToListOfMaps(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> result = mapper.readValue(FileUtils.readFileToString(new File(fileName)),
                        new TypeReference<List<Map<String, Object>>>() {
                        });
        return result;
    }

    protected Map<String, Object> createAttributesSetParamsMap(Map<String, Object> attributes) {
        return this.client.createAttributesSetParamsMap(
                this.cccId, this.misCode, this.attributesSource, this.attributesDescription, attributes);
    }

}
