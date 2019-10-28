package org.ccctc.common.commonidentity.openidc;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.mitre.oauth2.model.AuthenticationHolderEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.openid.connect.token.ConnectTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CCCTokenEnhancer extends ConnectTokenEnhancer {
    // Not wired by default - set the config in the oidc server itself.
    private IScopeToRoleMapper scopeToRoleMapper;
    
    @Autowired
    UserInfoRepository userInfoRepository = null;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        AuthenticationHolderEntity authHolder = ((OAuth2AccessTokenEntity)accessToken).getAuthenticationHolder();
        OAuth2AccessToken oatoken = super.enhance(accessToken, authentication);
        ((OAuth2AccessTokenEntity) oatoken).setAuthenticationHolder(authHolder);
        OAuth2AccessTokenEntity token = (OAuth2AccessTokenEntity) oatoken;

        log.debug("Original token: {}", token);

        try {
            log.debug("Attempting jwt parse for id token");
            try {
                SignedJWT idToken = SignedJWT.parse(token.getIdTokenString());
                idToken = extendJWT(idToken, authentication, token.getScope());
                OAuth2AccessTokenEntity newIDToken = new OAuth2AccessTokenEntity();
                newIDToken.setJwt(idToken);
                token.setIdToken(newIDToken);
            }
            catch (NullPointerException e) {
                log.debug("ID Token not present");
            }

            token.setJwt(extendJWT((SignedJWT) token.getJwt(), authentication, token.getScope()));
            token.setAuthenticationHolder(authHolder);
            return token;
        }
        catch (ParseException e) {
            log.warn("Claim parse exception ({}) for ccc jwt token", e);
            return oatoken;
        }
        catch (Exception e) {
            log.warn("Caught exception: {}", e);
            return oatoken;
        }
    }

    private SignedJWT extendJWT(SignedJWT jwt, OAuth2Authentication authentication, Set<String> scopes) throws ParseException {
        JWTClaimsSet.Builder claimBuilder = new JWTClaimsSet.Builder(jwt.getJWTClaimsSet());
        HashSet<String> roles = new HashSet<>();
        final OAuth2Request oAuth2Request = authentication.getOAuth2Request();
        
        if (authentication.getUserAuthentication() != null) {
            Object details = authentication.getUserAuthentication().getDetails();

            log.debug("Request: {}", oAuth2Request.getRequestParameters());

            if (details == null) {
                log.debug("Could not load details directly from authentication. Loading from UserInfoRepository.");
                details = userInfoRepository.getByUsername(authentication.getName());
            }

            log.debug("Details: {}", details);

            if (details instanceof UserIdentity) {
                UserIdentity info = (UserIdentity) details;
                claimBuilder.claim(JWTUserIdentity.JWT_AUTH_SOURCE_FIELD, info.getAuthSource())
                                .claim(JWTUserIdentity.JWT_MIS_FIELD, info.getMisCode())
                                .claim(JWTUserIdentity.JWT_CCCID_FIELD, info.getCccId())
                                .claim(JWTUserIdentity.JWT_EPPN_FIELD, info.getEppn());

                for (GrantedAuthority authority : info.getAuthorities()) {
                    roles.add(authority.getAuthority());
                }
            }
        }

        String grantType = oAuth2Request == null ? "" : oAuth2Request.getGrantType();
        roles.addAll(translateScopesToRoles(grantType, scopes));

        claimBuilder.claim(JWTUserIdentity.JWT_SCOPES_FIELD, scopes).claim(JWTUserIdentity.JWT_ROLES_FIELD, roles);

        final JWTClaimsSet claims = claimBuilder.build();

        if (log.isDebugEnabled()) {
            log.debug("New JWT claims from ccc.UserIdentity: {}", claims.getClaims());
        }

        final JWSHeader header = new JWSHeader.Builder(this.getJwtService().getDefaultSigningAlgorithm())
                        .keyID(this.getJwtService().getDefaultSignerKeyId()).build();

        final SignedJWT newJWT = new SignedJWT(header, claims);
        this.getJwtService().signJwt(newJWT);
        return newJWT;
    }

    private IScopeToRoleMapper getScopeToRoleMapper() {
        if (scopeToRoleMapper == null) {
            scopeToRoleMapper = new DefaultScopeToRoleMapper();
        }
        return scopeToRoleMapper;
    }

    public void setScopeToRoleMapper(IScopeToRoleMapper scopeToRoleMapper) {
        this.scopeToRoleMapper = scopeToRoleMapper;
    }

    private Set<String> translateScopesToRoles(final String grantType, Set<String> scopes) {
        HashSet<String> roles = new HashSet<String>();
        
        // Only client_credentials grants can have superuser scope so remove it otherwise
        if (scopes.contains(IRolesAndScopes.SUPERUSER_SCOPE) && !("client_credentials").equals(grantType)) {
            scopes.remove(IRolesAndScopes.SUPERUSER_SCOPE);
        }
        
        roles.addAll(getScopeToRoleMapper().translateScopesToRoles(scopes));
        return roles;
    }

}
