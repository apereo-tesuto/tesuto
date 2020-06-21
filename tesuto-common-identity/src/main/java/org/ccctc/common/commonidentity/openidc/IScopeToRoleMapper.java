package org.ccctc.common.commonidentity.openidc;

import java.util.Set;

public interface IScopeToRoleMapper {
    public Set<String> translateScopesToRoles(Set<String> scopes);
}
