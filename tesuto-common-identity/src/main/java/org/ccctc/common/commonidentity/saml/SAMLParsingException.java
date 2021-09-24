package org.ccctc.common.commonidentity.saml;

/**
 * Created by Parker Neff - Unicon Inc on 12/2/15.
 */
public class SAMLParsingException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public SAMLParsingException(String message) {
        super(message);
    }
}
