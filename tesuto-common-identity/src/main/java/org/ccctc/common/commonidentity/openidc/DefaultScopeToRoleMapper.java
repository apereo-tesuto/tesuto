package org.ccctc.common.commonidentity.openidc;

import static org.ccctc.common.commonidentity.openidc.IRolesAndScopes.GOD_TOOL_ROLE;
import static org.ccctc.common.commonidentity.openidc.IRolesAndScopes.GOD_TOOL_SCOPE;
import static org.ccctc.common.commonidentity.openidc.IRolesAndScopes.PII_ROLE;
import static org.ccctc.common.commonidentity.openidc.IRolesAndScopes.PII_SCOPE;

import java.util.HashSet;
import java.util.Set;


/**
 * Default behaviors are the ones that were presently being executed by the code at the time this strategy implementation was 
 * being developed. The expectation is that the properties file that would be injected would define this set (and others).
 * @author chasegawa
 */
public class DefaultScopeToRoleMapper implements IScopeToRoleMapper {
    @Override
    public Set<String> translateScopesToRoles(Set<String> scopes) {
        HashSet<String> roles = new HashSet<String>();
        
        // If we are missing scopes, we are done...
        if (scopes == null) {
            return roles;
        }

        if (scopes.contains(IRolesAndScopes.SUPERUSER_SCOPE)) {
            roles.add(IRolesAndScopes.SUPERUSER_ROLE);
        }
        
        if (scopes.contains(GOD_TOOL_SCOPE)) {
            roles.add(GOD_TOOL_ROLE);
        }

        if (scopes.contains(PII_SCOPE)) {
            roles.add(PII_ROLE);
        }
        
        return roles;
    }
}
