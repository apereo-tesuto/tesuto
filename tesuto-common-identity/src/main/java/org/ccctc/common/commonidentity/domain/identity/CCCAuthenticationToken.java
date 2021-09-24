package org.ccctc.common.commonidentity.domain.identity;

import com.nimbusds.jwt.JWT;
import lombok.Getter;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.util.Collection;

/**
 * Created by andrew on 11/16/16.
 */
public class CCCAuthenticationToken extends OIDCAuthenticationToken {
    private Object principal;

    @Getter
    private UserIdentity userIdentity = null;

    public CCCAuthenticationToken(JWTUserIdentity id) throws ParseException {
        this(id.getJwt(), id, id.getRefreshToken());
    }

    public CCCAuthenticationToken(JWT jwt, UserIdentity userIdentity) throws ParseException {
        this(jwt, userIdentity, "");
    }

    public CCCAuthenticationToken(JWT jwt, UserIdentity userIdentity, String refreshToken) throws ParseException {
        this(
                jwt.getJWTClaimsSet().getSubject(),
                jwt.getJWTClaimsSet().getIssuer(),
                userIdentity,
                userIdentity.getAuthorities(),
                jwt,
                jwt.serialize(),
                refreshToken
        );
        this.userIdentity = userIdentity;
    }
    

    public CCCAuthenticationToken(String subject, String issuer, UserInfo userInfo, Collection<? extends GrantedAuthority> authorities, JWT idToken, String accessTokenValue, String refreshTokenValue) {
        super(subject, issuer, userInfo, authorities, idToken, accessTokenValue, refreshTokenValue);

        if (userInfo instanceof UserDetails) {
            principal = userInfo;
            setDetails(userInfo);

            if (userInfo instanceof JWTUserIdentity) {
                ((JWTUserIdentity)userInfo).setRefreshToken(refreshTokenValue);
            }
        } else {
            principal = getPrincipal();
        }
        if (userInfo instanceof UserIdentity) {
            this.userIdentity = (UserIdentity)userInfo;
        }
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
