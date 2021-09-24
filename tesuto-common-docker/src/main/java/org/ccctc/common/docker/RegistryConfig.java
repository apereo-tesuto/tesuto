package org.ccctc.common.docker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
@Documented
public @interface RegistryConfig {

	String host() default "https://index.docker.io/v1/";
	String email();
	String userName();
	String passwd();
}