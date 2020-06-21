package org.ccctc.common.commonidentity.openidc;

import com.nimbusds.jwt.SignedJWT;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.ccctc.common.commonidentity.domain.identity.CCCAuthenticationToken;
import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityEnhancer;
import org.ccctc.common.commonidentity.openidc.validator.HTTPIntrospectValidator;
import org.ccctc.common.commonidentity.openidc.validator.TokenValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleBearerFilter extends GenericFilterBean {
    protected static final Histogram tokensProcessed = Histogram.build()
            .name("tokens_processed_time")
            .help("The number of tokens processed and time taken by the CryptoBearerFilter")
            .register();

    protected static final Counter rejectedTokens = Counter.build()
            .name("tokens_rejected")
            .help("The number of tokens rejected by CryptoBearerFilter")
            .register();


    @Getter @Setter private String clientId = "client";
    @Getter @Setter private String clientSecret = "secret";
    @Getter @Setter private String introspectUrl;
    @Setter(AccessLevel.PACKAGE) private BearerTokenExtractor extractor = new BearerTokenExtractor();
    @Setter(AccessLevel.PACKAGE) private RestTemplate restTemplate = new RestTemplate();
    @Setter UserIdentityEnhancer enhancer = null;
    @Setter TokenValidator validator;
    @Setter private Map<String, List<String>> roleMap = null;

    public SimpleBearerFilter() {}

    /**
     * Create a SimpleBearerFilter to validate bearer tokens and provide role mappings.
     * @param roles The roles to map. Keys are expected incoming roles. For each incoming role present as a key in the map, all role values will be assigned.
     *
     *              For example, the combination of: Roles: [ROLE_USER], RoleMap: {ROLE_USER: [ROLE_ADMIN]} would result in Roles: [ROLE_USER, ROLE_ADMIN].
     */
    public SimpleBearerFilter(Map<String, List<String>> roles) {
        this.roleMap = roles;
    }

    public SimpleBearerFilter(String clientId, String clientSecret, String introspectUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.introspectUrl = introspectUrl;
    }

    public SimpleBearerFilter(Map<String, List<String>> roleMap, String clientId, String clientSecret, String introspectUrl) {
        this(clientId, clientSecret, introspectUrl);
        this.roleMap = roleMap;
    }

    HashSet<GrantedAuthority> buildAuthoritiesList(Collection<? extends GrantedAuthority> roles) throws ParseException {
        HashSet<GrantedAuthority> authorities = new HashSet<>(roles.size()+1);
        for(GrantedAuthority role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        if (roleMap == null || roleMap.size() == 0) {
            return authorities;
        }

        // Maintain a separate list here so we don't alter the original (since we're using it)
        List<GrantedAuthority> mappedAuthorities = new ArrayList<>();

        // For each key in the roleMap that exists in the incoming roles claim, add configured roles
        for (GrantedAuthority authority: authorities) {
            String role = authority.getAuthority();

            if (roleMap.containsKey(role)) {
                for(String additionalRole : roleMap.get(role)) {
                    mappedAuthorities.add(new SimpleGrantedAuthority(additionalRole.toUpperCase()));
                }
            }
        }

        authorities.addAll(mappedAuthorities);
        return authorities;
    }


    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        restTemplate.setErrorHandler(new OpenIdResponseErrorHandler());
        validator = new HTTPIntrospectValidator(introspectUrl, clientId, clientSecret);
    }


    Authentication extractToken(HttpServletRequest httpRequest) {
        return extractor.extract(httpRequest);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            log.debug("Http Authorization header: {}", httpRequest.getHeader("Authorization"));
            Authentication preAuthToken = extractToken(httpRequest);

            if (preAuthToken != null && validateAuthentication(preAuthToken)) {
                log.debug("Http Authorization token: {}", preAuthToken.getName());
                Authentication auth = buildAuthenticationToken(preAuthToken);
                if (auth != null) {
                    auth.setAuthenticated(true);
                }
                log.debug("Final authorization object set in securityContext: {}", auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
       } catch (ParseException e) {
           log.error("Error extracting or validating preAuthToken.", e);
       }
        chain.doFilter(request, response);
    }
    
    CCCAuthenticationToken buildAuthenticationToken(Authentication auth) throws ParseException {
        if (auth == null) {
            throw new ParseException("Cannot parse null authentication", 0);
        }

        final SignedJWT jwt = SignedJWT.parse(auth.getName());
        log.debug("JWT claim set: {}", jwt.getJWTClaimsSet());

        UserIdentity user = getJWTUserIdentity(jwt);

        user.setAuthorities(buildAuthoritiesList(user.getAuthorities()));

        log.debug("Pre-enhancer user: {}", user);

        if (enhancer != null) {
            user = enhancer.enhance(user);
        }

        log.debug("Post-enhancer user: {}", user);

        return new CCCAuthenticationToken(jwt, user);
    }

    protected JWTUserIdentity getJWTUserIdentity(final SignedJWT jwt) {
        return new JWTUserIdentity(jwt);
    }

    public boolean validateAuthentication(Authentication auth) {
        final Histogram.Timer timer = tokensProcessed.startTimer();

        if (auth == null) {
            timer.observeDuration();
            rejectedTokens.inc();
            return false;
        }

        String token = auth.getName();
        log.debug("Token for authentication is {}", token);

        boolean valid = validator.validate(token);
        timer.observeDuration();

        if(!valid) {
            rejectedTokens.inc();
        }

        return valid;
    }
}
