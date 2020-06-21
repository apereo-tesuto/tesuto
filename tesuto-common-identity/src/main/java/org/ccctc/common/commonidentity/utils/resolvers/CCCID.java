package org.ccctc.common.commonidentity.utils.resolvers;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew on 11/4/16.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CCCID {
    /**
     * Whether eppn will override the CCCID in the case of an administrative user
     * @return
     */
    boolean allowEPPN() default true;

    /**
     * Whether to validate the EPPN else not match the method
     * @return
     */
    boolean validate() default false;
}
