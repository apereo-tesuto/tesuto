package org.ccctc.common.commonidentity.openidc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityEnhancer;
import org.ccctc.common.commonidentity.openidc.validator.JWKSetLoadException;
import org.ccctc.common.commonidentity.openidc.validator.SignatureValidator;
import org.springframework.security.core.Authentication;

import com.nimbusds.jwt.SignedJWT;

import io.prometheus.client.Histogram;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class must be instantiated as a bean. Otherwise, afterPropertiesSet() will not be called and the JWK set will not be loaded.
 * This class can handle tokens from BOTH Keycloak and Mitre.
 */
@NoArgsConstructor
@Slf4j
public class CryptoBearerFilter extends SimpleBearerFilter {   
    private String[] jwkURLs;
    private final List<SignatureValidator> validators = new ArrayList<SignatureValidator>();
    
    /**
     * Create a new CryptoBearerFilter to validate bearer tokens via JWK url.
     *
     * @param jwkURL The URL for the json web key store of the IdP.
     */
    public CryptoBearerFilter(String jwkURL) {
        setJwkUrls(new String[]{jwkURL});
    }

    public CryptoBearerFilter(String... jwkURLs) {
        setJwkUrls(jwkURLs);
    }

    /**
     * Create a CryptoBearerFilter to validate bearer tokens and provide role mappings.
     *
     * @param jwkURL The URL for the json web key store of the IdP.
     * @param roles  The roles to map. Keys are expected incoming roles. For each incoming role present as a key in the map, all role values will be assigned.
     *               <p>
     *               For example, the combination of: Roles: [ROLE_USER], RoleMap: {ROLE_USER: [ROLE_ADMIN]} would result in Roles: [ROLE_USER, ROLE_ADMIN].
     */
    public CryptoBearerFilter(String jwkURL, Map<String, List<String>> roles) {
        super(roles);
        setJwkUrls(new String[]{jwkURL});        
    }
    
    public CryptoBearerFilter(UserIdentityEnhancer enhancer, String... jwkURLs) {
        setJwkUrls(jwkURLs);
        setEnhancer(enhancer);
    }
    
    @Override
    public void afterPropertiesSet() throws ServletException {
        // Try to build a SV for each URL - only throw an exc if NO validators could be built
        Arrays.asList(jwkURLs).forEach(jwkURL -> {
            log.debug("Attempting to use jwk url {} for filter keyset.", jwkURL);
            try {
                validators.add(new SignatureValidator(jwkURL));
            } catch (JWKSetLoadException e) {
                log.error("Could not load keys for crypto bearer filter", e);
            }   
        });

        if (validators.isEmpty()) {
            throw new ServletException("Could not load any keys for crypto bearer filter");
        }
    }

    protected JWTUserIdentity getJWTUserIdentity(final SignedJWT jwt) {
        return new JWTUserIdentity(jwt);
    }
    
    public boolean isHealthy() {
        return jwkURLs.length == validators.size();
    }
    
    private void setJwkUrls(String[] jwkURLs) {
        this.jwkURLs = jwkURLs;
    }
    
    @Override
    public boolean validateAuthentication(Authentication auth) {
        final Histogram.Timer timer = tokensProcessed.startTimer();

        if (auth == null) {
            timer.observeDuration();
            rejectedTokens.inc();
            return false;
        }

        String token = auth.getName();
        log.debug("Token for authentication is {}", token);

        boolean valid = false;

        for (Iterator<SignatureValidator> i = validators.iterator(); i.hasNext();) {
            valid = valid || i.next().validate(token);
            if (valid) {
                break;
            }
        }

        timer.observeDuration();
        if (!valid) {
            rejectedTokens.inc();
        }

        return valid;
    }
}
