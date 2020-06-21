package org.ccctc.common.commonidentity.utils.resolvers;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.utils.CCCIdentityUtils;


import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class CCCIDResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == String.class && parameter.hasParameterAnnotation(CCCID.class);
    }

    @Override
    public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final CCCID cccidAnnot = parameter.getParameterAnnotation(CCCID.class);

        log.debug("Resolving cccid with eppnOverride: {}", cccidAnnot.allowEPPN());

        final Principal principal = webRequest.getUserPrincipal();

        log.debug("Principal is {}", principal);
        if (principal == null) {
            return null;
        }
        UserIdentity id = CCCIdentityUtils.getUserIdentity(principal);
        if (id == null) {
            return null;
        }
        if (!StringUtils.isEmpty(id.getCccId())) {
            return id.getCccId();
        }

        return cccidAnnot.allowEPPN() ? id.getEppn() : null;
    }
}
