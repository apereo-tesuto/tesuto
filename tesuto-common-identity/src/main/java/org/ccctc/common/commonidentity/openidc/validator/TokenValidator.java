package org.ccctc.common.commonidentity.openidc.validator;

/**
 * Created by andrew on 3/2/17.
 */
public interface TokenValidator {
    public boolean validate(String token);
}
