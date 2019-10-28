package org.ccctc.common.commonidentity.domain.identity;

import org.ccctc.common.commonidentity.utils.CCCIdentityUtils;

import com.nimbusds.jwt.JWT;
import lombok.Setter;

/**
 * Created by andrew on 2/10/17.
 */
public class SecurityContextPreferrer implements JWTGetter {
    @Setter private JWTGetter sa;

    public SecurityContextPreferrer(JWTGetter sa) {
        this.sa = sa;
    }
    public SecurityContextPreferrer(){};

    public JWT getContextJWT() {
        UserIdentity id = CCCIdentityUtils.getUserIdentity();
        if (id != null && id instanceof JWTUserIdentity) {
            return ((JWTUserIdentity)id).getJwt();
        }
        return null;
    }

    @Override
    public JWT getJWT() {
        JWT contextJWT = getContextJWT();
        if (contextJWT != null) {
            return contextJWT;
        }
        if (sa == null) {
            return null;
        }

        return sa.getJWT();
    }
}
