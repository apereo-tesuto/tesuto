package org.ccctc.common.commonidentity.utils.resolvers;

import com.google.common.net.HttpHeaders;


import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class BearerTokenResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == String.class && (
                parameter.hasParameterAnnotation(BearerToken.class)// || parameter.getParameterName() == "bearerToken"
        );
    }

    @Override
    public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final String authHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BearerToken.STARTS_WITH)) {
            log.debug("Found bearer header in {}", authHeader);
            return authHeader.substring(BearerToken.STARTS_WITH.length());
        }
        return null;
    }


}
