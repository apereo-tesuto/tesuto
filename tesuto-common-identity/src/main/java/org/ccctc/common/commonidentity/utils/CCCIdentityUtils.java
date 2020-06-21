package org.ccctc.common.commonidentity.utils;

import com.nimbusds.jwt.JWT;

import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityHolder;
import org.ccctc.common.commonidentity.exceptions.MissingBearerTokenException;
import org.ccctc.common.commonidentity.utils.resolvers.BearerToken;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by andrew on 11/4/16.
 */
public class CCCIdentityUtils {
    /**
     * Get an HttpHeaders
     * @param token The bearer token value
     * @return HTTP Headers pre-filled with the bearer token.
     */
    public static HttpHeaders getBearerHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.add(HttpHeaders.AUTHORIZATION, BearerToken.STARTS_WITH + token);
        return h;
    }

    /**
     * Returns a usable HttpEntity based on the bearer token contained in the given HTTPRequest
     * @param req The original HttpServletRequest that contains a bearer token
     * @param body The body to send
     * @param <T> The body type
     * @return An HttpEntity ready to be exchanged via RestTemplate
     */
    public static <T> HttpEntity<T> withBearer(HttpServletRequest req, T body) throws MissingBearerTokenException {
        String authToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authToken == null || !authToken.startsWith(BearerToken.STARTS_WITH)) {
            throw new MissingBearerTokenException("No bearer token found for request.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, req.getHeader(org.apache.http.HttpHeaders.AUTHORIZATION));

        return new HttpEntity<>(body, headers);
    }

    /**
     * Returns a usable HttpEntity based on using the idToken from the JWTUserIdentity as a bearer token in an upcoming
     * @param id The user identity obtained originally from a JWT
     * @param body The body of the request that will be made. May be null
     * @param <T> The body type
     * @return An HttpEntity that can be exchanged via RestTemplate
     * @throws MissingBearerTokenException if the bearer token cannot be found.
     */
    public static <T> HttpEntity<T> withBearer(JWTUserIdentity id, T body) throws MissingBearerTokenException{
        if (id == null) {
            throw new MissingBearerTokenException("Null JWTUserIdentity; no token available");
        }

        if (id.getJwt() == null) {
            throw new MissingBearerTokenException("JWTUserIdentity contained a null jwt");
        }

        return withBearer(id.getJwt(), body);
    }


    /**
     * Get an HttpEntity based on the Authentication of a request
     * @param a The original Authentication object
     * @param body The body of the HTTP request
     * @param <T> The body type
     * @return HttpEntity to send via RestTemplate
     * @throws MissingBearerTokenException if the bearer token could not be found
     */
    public static <T> HttpEntity<T> withBearer(Authentication a, T body) throws MissingBearerTokenException {
        if (a instanceof OIDCAuthenticationToken) {
            final HttpHeaders headers = getBearerHeaders(((OIDCAuthenticationToken) a).getAccessTokenValue());
            return new HttpEntity<>(body, headers);
        }

        if (a instanceof UsernamePasswordAuthenticationToken) {
            try {
                final HttpHeaders headers = getBearerHeaders((String) a.getCredentials());
                return new HttpEntity<>(body, headers);
            } catch (ClassCastException e) {
                throw new MissingBearerTokenException("Bearer token was not part of the UsernamePasswordToken");
            }
        }

        throw new MissingBearerTokenException("Could not find bearer token: Unsupported Authentication class");
    }

    /**
     * Returns a usable HttpEntity (with bearer token header) from a given SignedJWT.
     * @param jwt A signed jwt
     * @param body The body to send
     * @param <T> The type of the body
     * @return An HttpEntity with pre-filled Bearer token header
     */
    public static <T> HttpEntity<T> withBearer(JWT jwt, T body) {
        String authToken = jwt.serialize();
        return new HttpEntity<>(body, getBearerHeaders(authToken));
    }

    /**
     * A convenience method for getting an HttpEntity based on the SecurityContextHolder as long as the current thread is handling a Spring request.
     * @param body The body to send.
     * @param <T> The type of the body.
     * @return An HttpEntity to be used with a RestTemplate, with pre-filled bearer token header.
     */
    public static <T> HttpEntity<T> withBearer(T body) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return withBearer(authentication, body);
    }

    /**
     * Get the UserIdentity from a Principal.
     * @param principal The Principal
     * @return The UserIdentity object.
     */
    public static UserIdentity getUserIdentity(Principal principal) {
        if (principal instanceof UserIdentity) {
            return (UserIdentity) principal;
        }
        return null;
    }

    public static UserIdentity getUserIdentity(Authentication auth) {
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
            if (token.getPrincipal() instanceof UserIdentity) {
                return (UserIdentity) token.getPrincipal();
            }
        }
        if (auth instanceof UserIdentityHolder) {
            return ((UserIdentityHolder) auth).getUserIdentity();
        }
        return null;
    }

    /**
     * Get the UserIdentity from the current securityContext.
     * @return The UserIdentity object.
     */
    public static UserIdentity getUserIdentity() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        if (authentication.getDetails() instanceof UserIdentity) {
            return (UserIdentity)authentication.getDetails();
        }

        if (authentication instanceof OIDCAuthenticationToken) {
            return new JWTUserIdentity(((OIDCAuthenticationToken) authentication).getIdToken());
        }

        return getUserIdentity(authentication);
    }

}
