package org.ccctc.common.commonidentity.utils;

import org.apache.commons.lang.StringUtils;
import org.ccctc.common.commonidentity.openidc.CryptoBearerFilter;
import org.ccctc.common.commonidentity.utils.resolvers.BearerToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class exists to allow through CSRF any requests that contain a bearer token in the Authorization Header.
 *
 * The match method should return true if the request must be checked for valid CSRF tokens
 */
public class BearerCSRFRequestMatcher implements RequestMatcher {
    // All methods
    private static final HashSet<String> allowedMethods = new HashSet<String>(
            Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        if (allowedMethods.contains(httpServletRequest.getMethod())) {
            return false; // We never have to check these methods anyway
        }

        String auth = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.isEmpty(auth) || !auth.startsWith(BearerToken.STARTS_WITH);
    }
}