package org.ccctc.common.commonidentity.openidc;

import java.util.HashSet;

import org.ccctc.common.commonidentity.openidc.ScopeToRoleFromPropertiesMapper;
import org.junit.Assert;
import org.junit.Test;

public class ScopeToRoleFromPropertiesMapperTest {

    @Test
    public void testTranslateScopesToRoles() {
        ScopeToRoleFromPropertiesMapper mapper = new ScopeToRoleFromPropertiesMapper();
        mapper.setPropertiesFileLocation("src/test/resources/mappings.properties");
        HashSet<String> scopes = new HashSet<String>();
        scopes.add("foo");
        Assert.assertTrue("Mapper not reading test mapping file", mapper.translateScopesToRoles(scopes).contains("bar"));
    }
}