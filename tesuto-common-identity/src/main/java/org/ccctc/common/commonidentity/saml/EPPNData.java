package org.ccctc.common.commonidentity.saml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * POJO data type representing the data stored in the authSources.json file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class EPPNData {
    @Getter @Setter private String eppnSuffix;
    @Getter @Setter private String authSource;
    @Getter @Setter private String description;
    @Getter @Setter private String idpEntityId;
    @Getter @Setter private Collection<String> misCodes;
}
