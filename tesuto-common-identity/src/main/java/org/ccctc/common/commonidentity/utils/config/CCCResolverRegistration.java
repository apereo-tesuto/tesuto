package org.ccctc.common.commonidentity.utils.config;

import org.ccctc.common.commonidentity.utils.resolvers.BearerTokenResolver;
import org.ccctc.common.commonidentity.utils.resolvers.CCCIDResolver;
import org.ccctc.common.commonidentity.utils.resolvers.UserIdentityResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

public class CCCResolverRegistration extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new BearerTokenResolver());
        argumentResolvers.add(new CCCIDResolver());
        argumentResolvers.add(new UserIdentityResolver());
    }
}
