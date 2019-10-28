package org.ccctc.common.commonidentity.openidc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;

import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.mitre.openid.connect.client.UserInfoFetcher;
import org.mitre.openid.connect.model.PendingOIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class CCCUserInfoFetcher extends UserInfoFetcher {
    @Autowired
    private RestTemplate restTemplate = null;

    private String userinfoEndpoint = "";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Summary logins = new Summary.Builder()
            .name("commonidentity_userinfo_fetch_seconds")
            .help("The time taken for logins which made userinfo requests")
            .labelNames("idp")
            .register();

    public CCCUserInfoFetcher(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }


    @Override
    public UserInfo loadUserInfo(PendingOIDCAuthenticationToken token) {
        final Summary.Timer timer = logins.labels(token.getIssuer()).startTimer();
        
        final JWTUserIdentity userIdentity = new JWTUserIdentity(token.getIdToken());

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessTokenValue());
        final HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        final ResponseEntity<String> idResponse = restTemplate.exchange(userinfoEndpoint, HttpMethod.GET, entity, String.class);

        try {
            final UserIdentity userinfo = OBJECT_MAPPER.readValue(idResponse.getBody(), UserIdentity.class);
            log.debug("userIdentity JSON: {}", idResponse.getBody());
            userIdentity.setAffiliations(userinfo.getAffiliations());
            userIdentity.setEntitlements(userinfo.getEntitlements());
            userIdentity.setPrimaryAffiliation(userinfo.getPrimaryAffiliation());
        } catch (Exception e) {
            log.error("Userinfo error", e);
        } finally {
            timer.observeDuration();
        }

        return userIdentity;
    }
}
