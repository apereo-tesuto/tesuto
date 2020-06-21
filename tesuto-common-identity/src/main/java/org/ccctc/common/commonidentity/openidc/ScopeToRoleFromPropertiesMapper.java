package org.ccctc.common.commonidentity.openidc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Read the mappings (by default) from filesystem:/opt/ccc/config/mappings.properties
 * If additional mappings are needed, set a new location and have those read in as well
 * 
 * @author chasegawa
 */
public class ScopeToRoleFromPropertiesMapper implements IScopeToRoleMapper {
    private Properties mappings;

    private String propertiesFileLocation = "/opt/ccc/config/mappings.properties";

    public ScopeToRoleFromPropertiesMapper() {
        mappings = new Properties();
        setPropertiesFileLocation(propertiesFileLocation);
    }

    public void setPropertiesFileLocation(String propertiesFileLocation) {
        this.propertiesFileLocation = propertiesFileLocation;
        InputStream input = null;
        try {
            input = new FileInputStream(this.propertiesFileLocation);
            mappings.load(input);
            input.close();
        }
        catch (IOException ex) {
            //ex.printStackTrace();
        }
    }

    @Override
    public Set<String> translateScopesToRoles(Set<String> scopes) {
        HashSet<String> roles = new HashSet<String>();
        for (String userScope : scopes) {
            if (mappings.containsKey(userScope)) {
                roles.add(mappings.getProperty(userScope));
            }
        }
        return roles;
    }

}
