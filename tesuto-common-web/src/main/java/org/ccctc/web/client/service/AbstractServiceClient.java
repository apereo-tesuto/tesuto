package org.ccctc.web.client.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;
import org.ccctc.web.client.rest.CCCRestCallHandler;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Properties and default values:
 * 
 * oauth.client.id = acme
 * oauth.client.secret = acmesecret
 * oauth.provider.url = http://localhost:8080/openid-connect-server-webapp/
 * deployment.env = ci  (should be one of: [ci|test|pilot] - prod should be blank!)
 */
@Slf4j
public abstract class AbstractServiceClient implements EnvironmentAware {
    protected String env;

    protected Environment environment;
    
    protected String oauthProviderUrl;
    
    protected CCCRestCallHandler restHandler = new CCCRestCallHandler();

    protected String serviceUrl;
        
    /**
     * Sub out the {{ENV}} (if present) in the string. If we are in prod, there won't be any env, so we need to make sure that any
     * ".." are reduced to a single "." - ideally the property value is already "correct" and this function doesn't do anything.
     */
    protected String adjustForEnv(final String text) {
        String result = StringUtils.replace(text, "{{ENV}}", env);
        result = StringUtils.replace(result, "..", ".");
        return result;
    }

    /**
     * Attempt to build a service account manager. If the properties and defaults do not work, then it is assumed that another
     * process will set the service account manager. 
     */
    protected void buildServiceAccountManager(final Environment environment) {
        String clientId = environment.getProperty("oauth.client.id", "acme");
        String clientSecret = environment.getProperty("oauth.client.secret", "acmesecret");        
        
        log.info("Attempting to build ServiceAccountManager using id {} and server {}", clientId, oauthProviderUrl);
    
        try {
            ServiceAccountManager.ServiceAccountManagerBuilder builder = new ServiceAccountManager.ServiceAccountManagerBuilder();
            setServiceAccountManager(builder.baseEndpoint(oauthProviderUrl).clientId(clientId).clientSecret(clientSecret).build());
        }
        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("OIDC Service Account has incorrect credentials. Please check oidc params.");
            }
            log.error("Could not create ServiceAccountManager", e);
        }
    }
    
    protected void init(final Environment environment) {
        env = environment.getProperty("deployment.env", "ci");
        oauthProviderUrl = adjustForEnv(environment.getProperty("oauth.provider.url", "http://localhost:8080/openid-connect-server-webapp/"));
    }

    /**
     * Used to override the RestTemplate class. By default, the CCCRestCallHandler will create its own, but this method is here to
     * allow for unit testing - we can override the actual template with a mock.
     * 
     * @param restTemplate
     */
    void setRestTemplate(final RestTemplate restTemplate) {
        restHandler.setRestTemplate(restTemplate);
    }

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
        init(environment);
    }
    
    /**
     * Allows for the SAM to be defined elsewhere. By default, this class will try to create a SAM from known properties.
     * 
     * @param serviceAccountManager
     */
    public void setServiceAccountManager(final ServiceAccountManager serviceAccountManager) {
        restHandler.setServiceAccountManager(serviceAccountManager);
    }

    /**
     * Should return a map where any value which was null removed.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> stripNullValues(final Map<String, Object> map) {
        Map<String, Object> results = new HashMap<String, Object>();
        if (map != null) {
            map.keySet().forEach(key -> {
                if (map.get(key) instanceof Map) {
                    results.put(key, stripNullValues((Map<String, Object>) map.get(key)));
                } else if (map.get(key) != null && StringUtils.isNotEmpty(map.get(key).toString())) {
                    results.put(key, map.get(key));
                }
            });
        }
        return results;
    }    
}
