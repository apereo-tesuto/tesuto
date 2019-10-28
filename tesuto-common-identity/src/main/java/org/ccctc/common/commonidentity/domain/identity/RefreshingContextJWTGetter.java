package org.ccctc.common.commonidentity.domain.identity;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import org.ccctc.common.commonidentity.domain.identity.response.ClientCredentials;
import org.ccctc.common.commonidentity.utils.CCCIdentityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jwt.JWT;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshingContextJWTGetter implements JWTGetter {
    @Setter(AccessLevel.PACKAGE)
    private RestTemplate rt = new RestTemplate();

    @Setter
    private String tokenEndpoint = null;
    private String clientId = null;
    private String clientSecret = null;

    public RefreshingContextJWTGetter(String tokenEndpoint, String clientId, String clientSecret) {
        this.tokenEndpoint = StringUtils.trimTrailingCharacter(tokenEndpoint, '/');
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public JWTUserIdentity refreshToken(JWTUserIdentity id) throws ParseException {
        final URI uri = UriComponentsBuilder.fromHttpUrl(tokenEndpoint)
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", id.getRefreshToken())
                .build().toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        final ResponseEntity<ClientCredentials> res = rt.postForEntity(uri, request, ClientCredentials.class);
        final ClientCredentials creds = res.getBody();
        final JWTUserIdentity jwt = new JWTUserIdentity(creds.getAccessTokenValue(), creds.getRefreshToken());
        return jwt;
    }

    public Authentication getAuthenticationToken() {
        final UserIdentity id = CCCIdentityUtils.getUserIdentity();
        if (id instanceof JWTUserIdentity) {
            JWTUserIdentity jwtUserIdentity = (JWTUserIdentity) id;
            try {
                final JWT jwt = jwtUserIdentity.getJwt();
                if (jwt != null && new Date().after(jwt.getJWTClaimsSet().getExpirationTime())) {
                    // If the token has expired, attempt to refresh the context and return the token.
                    JWTUserIdentity freshID = refreshToken(jwtUserIdentity);
                    return new CCCAuthenticationToken(freshID);
                }
            } catch(ParseException e){
                log.error("Got a parse exception while trying to refresh a JWT", e);
            }
        }
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JWT getJWT() {
        final Authentication authentication = getAuthenticationToken();
        if (authentication instanceof CCCAuthenticationToken) {
            CCCAuthenticationToken cccAuthenticationToken = (CCCAuthenticationToken) authentication;
            final UserIdentity userIdentity = cccAuthenticationToken.getUserIdentity();
            if (userIdentity instanceof JWTUserIdentity) {
                JWTUserIdentity identity = (JWTUserIdentity) userIdentity;
                return identity.getJwt();
            }
        }
        return null;
    }
}
