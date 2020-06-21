package org.ccctc.common.commonidentity.utils;

import org.ccctc.common.commonidentity.utils.config.CCCResolverRegistration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew on 12/1/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({CCCResolverRegistration.class})
public @interface EnableCCCMethodResolvers {
}
