package org.ccctc.web.client.service;

import org.ccctc.web.client.rest.CCCRestCallHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

public class ApplyServiceClientTest {
    private ApplyServiceClient client = new ApplyServiceClient();
    
    @Before
    public void setup() {
        CCCRestCallHandler restHandler = mock(CCCRestCallHandler.class);
        client.restHandler = restHandler;
        client.setEnvironment(new MockEnvironment());
    }
    
    @Test
    public void testFetchBadId() {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity<?> re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(HttpMethod.GET, "https://api.ci.opencccapply.net/apply/applications/badid", null)).thenReturn(re);
        when(re.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);        
        Map<String, Object> map = client.getAPPLYApplicationDetail("badid");
        Assert.assertTrue("There should be no results", map.isEmpty());
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testFetchApp() {
        CCCRestCallHandler restHandler = client.restHandler;
        ResponseEntity re = mock(ResponseEntity.class);
        when(restHandler.makeAuthenticatedRestCall(HttpMethod.GET, "https://api.ci.opencccapply.net/apply/applications/cccid", null)).thenReturn(re);
        when(re.getStatusCode()).thenReturn(HttpStatus.OK);
        
        Map<String, Object> appDetail = new HashMap<String, Object>();
        appDetail.put("null values", null);
        appDetail.put("name", "name");
        appDetail.put("something else", "not a name");
        when(re.getBody()).thenReturn(appDetail);       
        
        Map<String, Object> map = client.getAPPLYApplicationDetail("cccid");
        Assert.assertEquals("There should be two results", 2, map.size());
    }
}
