package org.ccctc.common.commonidentity.utils.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by andrew on 12/9/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(CCCResolverRegistration.class)
public @interface EnableCCCResolvers {
}
