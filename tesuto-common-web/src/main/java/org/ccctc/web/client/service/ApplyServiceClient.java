package org.ccctc.web.client.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * The ApplyServiceClient encapsulates the logic for making service calls to the CCC Apply rest services.
 * This will check the standard spring - application.properties file for the needed properties and alternately (and overridding)
 * then look for applyService.properties
 * 
 * This class needs Spring Core and Commons-Lang.
 * Logging through SLF4j
 * 
 * Configuration Properties and default values:
 * 
 * apply.url=https://api.{{ENV}}.opencccapply.net/apply/applications/
 * oauth.client.id = acme
 * oauth.client.secret = acmesecret
 * oauth.provider.url = http://localhost:8080/openid-connect-server-webapp/
 * deployment.env = ci (should be one of: [ci|test|pilot] - prod should be blank!)
 * 
 */
@Service
@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:applyService.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/opt/ccc/config/applyService.properties", ignoreResourceNotFound = true)
@Slf4j
public class ApplyServiceClient extends AbstractServiceClient {
    private final static String APP_TYPE = "%app%";
    
    public ApplyServiceClient() {
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getApplicationDetail(String appUrl) {
        Map<String, Object> results = new HashMap<String, Object>();
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, appUrl, null);
        if (HttpStatus.NOT_FOUND.equals(re.getStatusCode())) {
            return results;
        }
        results = (Map<String, Object>) re.getBody();
        return stripNullValues(results);
    }

    public Map<String, Object> getAPPLYApplicationDetail(final String applicationId) {        
        String appUrl = StringUtils.replace(serviceUrl, APP_TYPE, "apply") + applicationId;
        log.debug("Attempting to fetch APPLY application from: " + appUrl);  
        return getApplicationDetail(appUrl);
    }
    
    public Map<String, Object> getBOGApplicationDetail(final String applicationId) {        
        String appUrl = StringUtils.replace(serviceUrl, APP_TYPE, "ccpg") + applicationId;
        log.debug("Attempting to fetch APPLY application from: " + appUrl);  
        return getApplicationDetail(appUrl);
    }
    
    public Map<String, Object> getINTLApplicationDetail(final String applicationId) {        
        String appUrl = StringUtils.replace(serviceUrl, APP_TYPE, "intl") + applicationId;
        log.debug("Attempting to fetch APPLY application from: " + appUrl);  
        return getApplicationDetail(appUrl);
    }

    @Override
    protected void init(final Environment environment) {
        super.init(environment);
        serviceUrl = adjustForEnv(environment.getProperty("apply.url", "https://api.{{ENV}}.opencccapply.net/apply/applications/"));
        serviceUrl = StringUtils.replace(serviceUrl, "apply", APP_TYPE);
        log.debug("ApplyServiceClient init() using env properties: url= " + serviceUrl);
        buildServiceAccountManager(environment);
    }
    
    /**
     * This main is not the expected use, but rather a quick demo of the results of using the class to fetch application details.
     * Out of the box, this will work for the CI env and return the (fake) student data (real data from CI, not a real person).
     */
    public static void main(String args[]) {
        ApplyServiceClient client = new ApplyServiceClient();
        client.setEnvironment(new MockEnvironment());
        System.out.println(client.getAPPLYApplicationDetail("22645"));
    }
}
