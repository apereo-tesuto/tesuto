package org.ccctc.common.commonidentity.legacy.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SO HACKY
 *
 * This object exists and implements JWT so that serialize() can return the actual bearer token header, but the user
 * identity information in the claimset will be pulled from the impersonated user details (in the form of id_token header)
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IDTokenHeaderObject implements JWT {
    public static final String ID_TOKEN_HEADER_ISSUER = "id_token_header";
    // "{\"eppn\":\"pcollegeadmin@democollege.edu\",\"cccId\":null,\"misCode\":\"999\",\"affil
    // iations\":[\"portalCollegeAdmin\"],\"primaryAffiliation\":\"portalCollegeAdmin\",\"firstName\":\"Peter\",\"
    // lastName\":\"Collegeadmin\",\"email\":null,\"phone\":null,\"mobilePhone\":null,\"displayName\":\"Peter Coll
    // egeadmin\"}"

    public String eppn;
    public String cccId;
    public String misCode;
    public List<String> affiliations;
    public String primaryAffiliation;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String mobilePhone;
    public String displayName;
    private Set<String> roles = new HashSet<>();

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles.addAll(roles);
    }

    @Setter
    @JsonIgnore
    private UserIdentity originalIdentity = null;

    private String bearerToken;

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    private JWTClaimsSet claims;

    @Override
    public Header getHeader() {
        return null;
    }

    @Override
    public JWTClaimsSet getJWTClaimsSet() throws ParseException {
        if (claims == null) {
            String sub = cccId != null ? cccId : eppn;
            if (StringUtils.isEmpty(sub)) {
                sub = originalIdentity.getSub();
            }

            claims = new JWTClaimsSet.Builder()
                    .subject(sub)
                    .issuer(ID_TOKEN_HEADER_ISSUER)
                    .claim(JWTUserIdentity.JWT_EMAIL_FIELD, email)
                    .claim(JWTUserIdentity.JWT_CCCID_FIELD, cccId)
                    .claim(JWTUserIdentity.JWT_MIS_FIELD, misCode)
                    .claim(JWTUserIdentity.JWT_AFFILIATIONS_FIELD, affiliations)
                    .claim("primaryAffiliations", primaryAffiliation)
                    .claim("firstName", firstName)
                    .claim("lastName", lastName)
                    .claim("phone", phone)
                    .claim("mobilePhone", mobilePhone)
                    .claim("displayName", displayName)
                    .claim(JWTUserIdentity.JWT_ROLES_FIELD, roles)
                    .build();
        }
        return claims;
    }



    public void setPermissions(Collection<String> permissions) {
        roles.addAll(permissions);
    }

    @Override
    public Base64URL[] getParsedParts() {
        return new Base64URL[0];
    }

    @Override
    public String getParsedString() {
        return null;
    }

    @Override
    public String serialize() {
        return bearerToken;
    }
}
