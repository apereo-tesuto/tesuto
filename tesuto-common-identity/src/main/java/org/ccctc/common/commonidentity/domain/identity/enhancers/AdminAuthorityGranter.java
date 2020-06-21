package org.ccctc.common.commonidentity.domain.identity.enhancers;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityEnhancer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class AdminAuthorityGranter implements UserIdentityEnhancer {
    private List<GrantedAuthority> authoritiesToGrant = new ArrayList<>();

    private final Set<String> adminAffiliations = new ImmutableSet.Builder<String>()
            .add("faculty")
            .add("staff")
            .build();

    public AdminAuthorityGranter(List<String> rolesToGrant) {
        for (String role : rolesToGrant) {
            authoritiesToGrant.add(new SimpleGrantedAuthority(role));
        }
    }

    @Override
    public UserIdentity enhance(UserIdentity userIdentity) {
        if (adminAffiliations.contains(userIdentity.getPrimaryAffiliation())) {
            try {
                final Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) userIdentity.getAuthorities();
                authorities.addAll(authoritiesToGrant);
                userIdentity.setAuthorities(authorities);
            } catch (ClassCastException e) {
                log.warn("UserIdentity authorities was not a Set<GrantedAuthority>. Roles will remain the same.");
            }
        }
        return userIdentity;
    }
}
