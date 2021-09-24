package org.ccctc.common.commonidentity.domain.identity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.ccctc.common.commonidentity.domain.identity.response.ClientCredentials;
import org.ccctc.common.commonidentity.utils.CCCIdentityUtils;
import org.ccctc.common.commonidentity.utils.resolvers.BearerToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jwt.JWT;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ServiceAccountManager implements JWTGetter {
    public static void main(String[] args) {
        ServiceAccountManager.ServiceAccountManagerBuilder builder = new ServiceAccountManager.ServiceAccountManagerBuilder();
        ServiceAccountManager sam = builder.baseEndpoint("https://gateway.ci.cccmypath.org/security/oauth").clientId("acme").clientSecret("acmesecret").build();
        System.out.println(sam.getJWT().getParsedString());
    }
        
    @Setter
    private String baseEndpoint = null;

    @Setter
    private String clientId;
    
    @Setter
    private String clientSecret;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private RestTemplate restTemplate;
    
    @Setter
    private String[] scopes = null;

    private JWTUserIdentity token = null;

    /**
     * 
     * @param baseEndpoint
     * @param clientId
     * @param clientSecret
     * @param restTemplate
     * @param scopes
     * @throws AccessDeniedException
     */
    private ServiceAccountManager(String baseEndpoint, 
            String clientId, 
            String clientSecret,
            RestTemplate restTemplate,
            String... scopes) throws AccessDeniedException {
        if ('/' == baseEndpoint.charAt(baseEndpoint.length()-1)) {
            baseEndpoint = baseEndpoint.substring(0, baseEndpoint.length()-1);
        }
        log.debug("Base endpoint for AccountManager is {}, client id is {}", baseEndpoint, clientId);
        this.baseEndpoint = baseEndpoint;

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = restTemplate;
        this.scopes = scopes;

        getToken();
    }

    @Override
    public JWT getJWT() {
        return getToken().getJwt();
    }

    public JWTUserIdentity getToken() throws AccessDeniedException {
        if (token != null && new Date().before(token.getExpiration())) {
            log.debug("Token does not expire until {}; returning cached token.", dateFormat.format(token.getExpiration()));
            return token;
        }
        log.debug("No token available; querying {}", this.baseEndpoint);

        UriComponentsBuilder builder = getTokenURLBuilder(false);

        log.debug("Request URI: {}", builder.build().encode().toUriString());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        ResponseEntity<ClientCredentials> creds = restTemplate.postForEntity(
                builder.build().encode().toUri(),
                request,
                ClientCredentials.class
        );
        
        // If this didn't work (basic form), try adding the params to the URL
        if (creds.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
            builder = getTokenURLBuilder(true);
            creds = restTemplate.postForEntity(builder.build().encode().toUri(), request, ClientCredentials.class);
        }
        
        //If we still don't work, too bad.
        if (creds.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
            log.error("Configured OIDC server [{}] responded with a non-success code [{}] for client [{}] secret", builder.build().toUriString(), creds.getStatusCode(), clientId);
            throw new AccessDeniedException("Access denied by remote server with reason " + creds.getStatusCode().getReasonPhrase());
        }
        try {
            token = new JWTUserIdentity(creds.getBody().getAccessTokenValue());
        } catch (ParseException e) {
            throw new AccessDeniedException("Could not get access token value", e);
        }

        log.debug("Got token {}", token);

        return token;
    }

    protected UriComponentsBuilder getTokenURLBuilder(boolean includeParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseEndpoint + "/token");
        if (includeParams) {
            builder.queryParam("grant_type", "client_credentials").queryParam("response_type", "token");
        }

        if (includeParams && scopes != null && scopes.length > 0) {
            builder.queryParam("scope", StringUtils.join(scopes, "+"));
        }
        return builder;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @deprecated {@link CCCRestTemplate}
     * @param body The request body
     * @param <T> The body type
     * @return An HTTPEntity with the appropriate Authorization header set.
     */
    @Deprecated
    public <T> HttpEntity<T> withBearer(T body) {
        return CCCIdentityUtils.withBearer(getToken(), body);
    }
    
    /**
     * @deprecated {@link CCCRestTemplate}
     * @param body The request body
     * @param headers The headers (aside from Authorization) that should be sent
     * @param <T> The body type
     * @return An HTTPEntity with the appropriate Authorization header set.
     */
    @Deprecated
    public <T> HttpEntity<T> withBearer(T body, HttpHeaders headers) {
        String bearer = BearerToken.STARTS_WITH + getJWT().serialize();
        headers.add(HttpHeaders.AUTHORIZATION, bearer);
        return new HttpEntity<>(body, headers);
    }
    
    public static class ServiceAccountManagerBuilder {
        private String baseEndpoint;
        private String clientId;
        private String clientSecret;
        private RestTemplate restTemplate;
        private List<String> scopes = new ArrayList<>();
        
        public ServiceAccountManagerBuilder baseEndpoint(String baseEndpoint) {
            this.baseEndpoint = baseEndpoint;
            return this;
        }
        
        public ServiceAccountManager build() {
            if (StringUtils.isBlank(baseEndpoint)) {
                this.baseEndpoint = "http://localhost:8080";
            }
            if (StringUtils.isBlank(clientId)) {
                this.clientId = "blankClientId";
            }
            if (StringUtils.isBlank(clientSecret)) {
                this.clientSecret = "blankClientSecret";
            }
            if (this.restTemplate == null) {
                // normally, the restTemplate is created by the builder.  This 
                // section is the default behavior.  For Unit Testing, however, we
                // need to be able to pass in a mock rest template
                log.debug("creating a new restTemplate for ServiceAccountManager");
                ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
                this.restTemplate = new RestTemplate(requestFactory);
                this.restTemplate.setErrorHandler(new AuthenticationResponseErrorHandler());
            }
            ServiceAccountManager manager = new ServiceAccountManager(baseEndpoint, 
                    clientId, clientSecret, restTemplate, scopes.toArray(new String[scopes.size()]));
            return manager;
        }
        
        public ServiceAccountManagerBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public ServiceAccountManagerBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        
        public ServiceAccountManagerBuilder restTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
            return this;
        }
        
        public ServiceAccountManagerBuilder scope(String scope) {
            this.scopes.add(scope);
            return this;
        }
    }
}
