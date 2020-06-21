package org.ccctc.common.commonidentity.domain.identity;

import lombok.Getter;
import lombok.Setter;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.model.PendingOIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by andrew on 11/16/16.
 */
public class CCCAuthenticationProvider extends OIDCAuthenticationProvider {
    @Setter
    @Getter
    private UserIdentityEnhancer enhancer;

    @Override
    protected Authentication createAuthenticationToken(PendingOIDCAuthenticationToken token, Collection<? extends GrantedAuthority> authorities, UserInfo userInfo) {
        if (enhancer != null && userInfo instanceof UserIdentity) {
            userInfo = enhancer.enhance((UserIdentity)userInfo);
        }

        return new CCCAuthenticationToken(token.getSub(),
                token.getIssuer(),
                userInfo, authorities,
                token.getIdToken(), token.getAccessTokenValue(), token.getRefreshTokenValue());
    }
}
