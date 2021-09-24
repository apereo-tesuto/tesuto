package org.ccctc.common.commonidentity.exceptions;

/**
 * Created by andrew on 11/4/16.
 */
public class MissingBearerTokenException extends RuntimeException {
    public MissingBearerTokenException(String message) {
        super(message);
    }
}
