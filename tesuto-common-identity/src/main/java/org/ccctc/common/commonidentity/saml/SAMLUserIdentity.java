package org.ccctc.common.commonidentity.saml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.springframework.security.saml.SAMLCredential;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by andrew on 11/23/16.
 */
@Entity
public class SAMLUserIdentity extends UserIdentity {
    @Transient
    @JsonIgnore
    @Getter @Setter private SAMLCredential samlCredential;
}
