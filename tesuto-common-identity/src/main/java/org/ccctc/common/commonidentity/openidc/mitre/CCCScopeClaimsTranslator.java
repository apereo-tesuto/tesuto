package org.ccctc.common.commonidentity.openidc.mitre;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.mitre.openid.connect.service.impl.DefaultScopeClaimTranslationService;

import java.util.Set;

public class CCCScopeClaimsTranslator extends DefaultScopeClaimTranslationService {
    private SetMultimap<String, String> cccClaims = HashMultimap.create();

    public CCCScopeClaimsTranslator() {
        setupDefaults();
    }

    public CCCScopeClaimsTranslator(SetMultimap<String, String> cccClaims) {
        this.cccClaims = cccClaims;
    }

    private void setupDefaults() {
        this.cccClaims.put("profile", "affiliations");
        this.cccClaims.put("profile", "entitlements");
        this.cccClaims.put("profile", "primaryAffiliation");
        this.cccClaims.put("profile", "enrolledStudent");
        this.cccClaims.put("profile", "affiliatedStudent");
        this.cccClaims.put("profile", "authSource");
    }

    @Override
    public Set<String> getClaimsForScope(String scope) {
        final Set<String> claims = super.getClaimsForScope(scope);
        if (cccClaims.containsKey(scope)) {
            claims.addAll(cccClaims.get(scope));
        }
        return claims;
    }
}
