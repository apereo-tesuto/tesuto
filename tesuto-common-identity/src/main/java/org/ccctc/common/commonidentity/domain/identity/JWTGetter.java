package org.ccctc.common.commonidentity.domain.identity;

import com.nimbusds.jwt.JWT;

/**
 * Created by andrew on 2/10/17.
 */
public interface JWTGetter {
    public JWT getJWT();
}
