package org.ccctc.common.commonidentity.domain.identity.enhancers;

import lombok.extern.slf4j.Slf4j;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityEnhancer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class SimpleMapEnhancer implements UserIdentityEnhancer {
    private Map<String, Collection<String>> roleMap;

    public SimpleMapEnhancer(Map<String, Collection<String>> roleMap) {
        this.roleMap = roleMap;
    }

    @Override
    public UserIdentity enhance(UserIdentity user) {
        final Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        Set<GrantedAuthority> newAuthorities = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            if (roleMap.containsKey(authority.getAuthority())) {
                for (String role : roleMap.get(authority.getAuthority())) {
                    newAuthorities.add(new SimpleGrantedAuthority(role));
                }
            }
        }
        authorities.addAll(newAuthorities);
        return user;
    }
}
