package org.ccctc.common.commonidentity.domain.identity.enhancers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentityEnhancer;

@NoArgsConstructor
@Slf4j
public class ChainingUserIdentityEnhancer implements UserIdentityEnhancer {
    @Getter
    @Setter
    List<UserIdentityEnhancer> enhancers;

    @Override
    public UserIdentity enhance(UserIdentity user) {
        if (enhancers != null) {
            for(UserIdentityEnhancer enhancer : enhancers) {
                log.debug("Enhancing use identity with {}", enhancer.getClass().getCanonicalName());

                // A null user or enhancer will stop the chain.
                if (user == null || enhancer == null) {
                    break;
                }
                user = enhancer.enhance(user);
            }
        }

        return user;
    }
}
