package org.ccctc.web.client.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;
import org.ccctc.common.commonidentity.exceptions.CredentialsException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * This class encapsulates the logic for making REST calls through the CCC/Unicon Zuul gateway.
 */
@Slf4j
public class CCCRestCallHandler {
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int REQUEST_TIMEOUT = 60000;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final int MAX_TOTAL_CONNECTIONS = 5;
    private static final int VALIDATE_AFTER_MS = 300000;
    
    private static List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new MappingJackson2HttpMessageConverter());
        return converters;
    }

    private HttpClientBuilder builder;
    
    private HttpComponentsClientHttpRequestFactory factory;
    
    private RestTemplate restTemplate;

    private ServiceAccountManager serviceAccountManager;

    /** basically only use for testing */
    private boolean useRestTemplateProvided = false;

    /**
     * @return the same map of header values, but with the Authorization and Bearer token values set.
     */
    private Map<String, String> addAuthorizationHeaderValue(Map<String, String> headerValues) {
        headerValues.put("Authorization", "Bearer " + serviceAccountManager.getJWT().serialize());
        return headerValues;
    }
    
    private RestTemplate getRestTemplate() {
        if (this.useRestTemplateProvided) {
            return this.restTemplate;
        }
        buildClientHttpRequestFactory();
        RestTemplate result = new RestTemplate(factory);
        result.setMessageConverters(getMessageConverters());
        result.setErrorHandler(new RestResponseErrorHandlerLoggingDecorator(result.getErrorHandler()));
        return result;
    }
    
    private void buildClientHttpRequestFactory() {
        if (factory == null) {
            buildHttpClient();
            factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(builder.build());
        }
    }
    
    private void buildHttpClient() {
        if (builder == null) {

            builder = HttpClients.custom()
                            .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT)
                                            .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build())
                            .setConnectionManager(poolingHttpClientConnectionManager())
                            .setConnectionReuseStrategy(new NoConnectionReuseStrategy());
            try {
                builder.build();
            }
            catch (Exception ex) {
                log.error("Unable to build HttpClient", ex);
                throw ex;
            }
        }
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager mgr = new PoolingHttpClientConnectionManager();
        mgr.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        mgr.setValidateAfterInactivity(VALIDATE_AFTER_MS);

        return mgr;
    }
    
    /**
     * Use this method to make a rest call that requires authentication (OAUTH) first.
     * 
     * The parameters will be sent in the body of the request
     *
     * @param httpMethod Spring enum type HttpMethod (POST, GET, DELETE, etc)
     * @param serviceUrl The URL to send the REST request
     * @param paramMap The parameter map of values to send in the request
     */
    @SuppressWarnings("rawtypes")
    public ResponseEntity makeAuthenticatedRestCall(HttpMethod httpMethod, String serviceUrl, Object parameters) {
        return makeRestCall(httpMethod, serviceUrl, parameters, addAuthorizationHeaderValue(new HashMap<String, String>()));
    }

    /**
     * Use this method to make a rest call that requires authentication (OAUTH) first.
     * This will include the supplied header values in the header. 
     * The parameter map will be sent in the body of the request
     *
     * @param httpMethod Spring enum type HttpMethod (POST, GET, DELETE, etc)
     * @param serviceUrl The URL to send the REST request
     * @param paramMap The parameter map of values to send in the request
     * @param headerValues The map of header values to include in the header
     */
    @SuppressWarnings("rawtypes")
    public ResponseEntity makeAuthenticatedRestCall(HttpMethod httpMethod, String serviceUrl, Object parameters, Map<String, String> headerValues) {
        return makeRestCall(httpMethod, serviceUrl, parameters, addAuthorizationHeaderValue(headerValues));
    }

    /**
     * RestTemplate does a slightly different operation for GET, so we handle that here.
     */
    @SuppressWarnings("rawtypes")
    private ResponseEntity makeGETRestCall(String serviceUrl, Map<String, Object> paramMap) {
        log.trace("Attempting standard REST call. Using GET call to service URL: " + serviceUrl);
        ResponseEntity response = getRestTemplate().getForEntity(serviceUrl, Object.class, paramMap);
        return response;
    }

    /**
     * Make a standard rest call. parameter map will be sent in the body of the request
     * 
     * @param httpMethod Spring enum type HttpMethod (POST, GET, DELETE, etc)
     * @param paramMap
     */
    @SuppressWarnings("rawtypes")
    public ResponseEntity makeRestCall(HttpMethod httpMethod, String serviceUrl, Map<String, Object> paramMap) {
        if (httpMethod == HttpMethod.GET) {
            return makeGETRestCall(serviceUrl, paramMap);
        }
        return makeRestCall(httpMethod, serviceUrl, paramMap, new HashMap<String,String>());
    }
    
    /**
     * Use this method to make a standard rest call that will include the supplied header values in the header. 
     * The parameter map will be sent in the body of the request
     * 
     * @param httpMethod Spring enum type HttpMethod (POST, GET, DELETE, etc)
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public ResponseEntity makeRestCall(HttpMethod httpMethod, String serviceUrl, Object params, Map<String, String> headerValues) {
        HttpHeaders restHeaders = new HttpHeaders();
        for (String key : headerValues.keySet()) {
            restHeaders.add(key, headerValues.get(key));
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(params, restHeaders);
        log.trace("Attempting standard REST call. Using " + httpMethod + " call to service URL: " + serviceUrl);
        ResponseEntity response = getRestTemplate().exchange(serviceUrl, httpMethod, entity, Object.class);
        log.trace("Response received [{}] from REST call to service URL: {}", response.getStatusCode(), serviceUrl);
        return response;
    }
    
    /**
     * Used to override the RestTemplate class. By default, this util class will use a Spring RestTemplate with JSON message converters,
     * but for unit testing, it is easier to pass a mock.
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setMessageConverters(getMessageConverters());
        final ResponseErrorHandler providedErrorHandler = this.restTemplate.getErrorHandler();
        if (!(providedErrorHandler instanceof RestResponseErrorHandlerLoggingDecorator)) {
            this.restTemplate.setErrorHandler(new RestResponseErrorHandlerLoggingDecorator(providedErrorHandler));
        }
        this.useRestTemplateProvided  = true;
    }
    
    /**
     * This is used for security. SAM is delegated to when JWT is required
     * @param serviceAccountManager
     */
    public void setServiceAccountManager(ServiceAccountManager serviceAccountManager) {
        this.serviceAccountManager = serviceAccountManager;
    }
    
    /**
     * The sample code here can be run locally by checking out the profile service code at:
     * git@bitbucket.org:cccnext/docker-service-student-profile.git
     * and standing up the default docker configuration using that project's docker-compose file.
     * @throws Exception 
     */
     @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
    //     // Simple GET
    //     RestCallHandler rch = new RestCallHandler();
    //     ResponseEntity response = rch.makeRestCall(HttpMethod.GET,
    //                     "http://localhost:8085/student-profile-service/v2/users/service/healthcheck/",
    //                     new HashMap<String, Object>());
    //     System.out.println("Response code: " + response.getStatusCode());
    //     System.out.println("Response body: " + response.getBody());
    //
    //     // GET through gateway
    //     response = rch.makeRestCall(HttpMethod.GET,
    //                     "https://gateway.ci.cccmypath.org/student-profile-service/v2/users/service/healthcheck/",
    //                     new HashMap<String, Object>(),
    //                     "https://gateway.ci.cccmypath.org/security/oauth/token", "acme", "acmesecret");
    //     System.out.println("Response code: " + response.getStatusCode());
    //     System.out.println("Response body: " + response.getBody());
    //
    //     // Simple POST
         // Set the parameters in the param map 
         HashMap<String, Object> params = new HashMap<String, Object>();
         params.put("users", Arrays.asList("AAA5364"));
         params.put("subject", "The subject is a required field");
         params.put("message-body", "This is the simple text version of the message (this is used for non-html email and SMS)");
         params.put("message-body-html", "This is the html version of the message - only used in email (when supported by the client)");
         
         //Set the header values if needed
         HashMap<String, String> headerParams = new HashMap<String, String>();
         headerParams.put("id_token", "{\"misCode\":[\"ZZ1\"]}");
         ServiceAccountManager sa;
         
         try {
             ServiceAccountManager.ServiceAccountManagerBuilder builder = new ServiceAccountManager.ServiceAccountManagerBuilder();
             sa = builder.baseEndpoint("https://gateway.ci.cccmypath.org/security/oauth").clientId("acme").clientSecret("acmesecret").build();
         }
         catch (HttpClientErrorException e) {
             if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                 throw new CredentialsException("OIDC Service Account has incorrect credentials. Please check oidc params.");
             }
             throw new Exception("Could not create ServiceAccountManager", e);
         }
         
         CCCRestCallHandler rch = new CCCRestCallHandler();
         rch.setServiceAccountManager(sa);
         
         ResponseEntity response = rch.makeAuthenticatedRestCall(HttpMethod.POST, "https://gateway.ci.cccmypath.org/ccc/api/messages/v1/sendMessages", params, headerParams);
         System.out.println("Response code: " + response.getStatusCode()); // Expecting a 202
    //     
    //     // POST through the gateway
    //     params.put("cccids", Arrays.asList("AAA5364"));
    //     response = rch.makeRestCall(HttpMethod.POST, "https://gateway.ci.cccmypath.org/student-profile-service/v2/users/studentProfiles",
    //                     params, headerParams,
    //                     "https://gateway.ci.cccmypath.org/security/oauth/token", "acme", "acmesecret");
    //     System.out.println("Response code: " + response.getStatusCode());
    //     System.out.println("Response body: " + response.getBody());
     }
}
