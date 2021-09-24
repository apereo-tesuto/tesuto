package org.ccctc.common.commonidentity.openidc.validator;

/**
 * Created by andrew on 3/2/17.
 */
public class JWKSetLoadException extends Exception {
    JWKSetLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
