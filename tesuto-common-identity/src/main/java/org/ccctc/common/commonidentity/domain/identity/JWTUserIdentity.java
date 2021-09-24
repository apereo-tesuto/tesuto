package org.ccctc.common.commonidentity.domain.identity;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;

import org.ccctc.common.commonidentity.openidc.IRolesAndScopes;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * JWTUserIdentity extracts the default CCC UserIdentity information from a mitre generated JWT idToken or AccessToken.
 * Created by andrew on 10/31/16.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class JWTUserIdentity extends UserIdentity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String JWT_AFFILIATIONS_FIELD = "affiliations";
    public static final String JWT_AUTH_SOURCE_FIELD = "authSource";
    public static final String JWT_CCCID_FIELD = "cccId";
    public static final String JWT_EMAIL_FIELD = "email";
    public static final String JWT_EPPN_FIELD = "eppn";
    public static final String JWT_MIS_FIELD = "misCode";
    public static final String JWT_ROLES_FIELD = "roles";
    public static final String JWT_SCOPES_FIELD = "scopes";
    public static final SimpleGrantedAuthority SUPERUSER_ROLE_AUTHORITY = new SimpleGrantedAuthority(IRolesAndScopes.SUPERUSER_ROLE);

    @Getter JWT jwt = null;
    @Setter @Getter private String refreshToken = null;

    public JWTUserIdentity() {}

    /**
     * Get a UserIdentity from a jwt.
     * @param jwt
     */
    public JWTUserIdentity(JWT jwt) {
        this.jwt = jwt;
        try {
            final JWTClaimsSet claims = jwt.getJWTClaimsSet();
            this.setEppn(claims.getStringClaim(JWT_EPPN_FIELD));
            this.setMisCode(claims.getStringClaim(JWT_MIS_FIELD));
            this.setCccId(claims.getStringClaim(JWT_CCCID_FIELD));
            this.setEmail(claims.getStringClaim(JWT_EMAIL_FIELD));
            this.setAffiliations(claims.getStringListClaim(JWT_AFFILIATIONS_FIELD));
            this.setAuthSource(claims.getStringClaim(JWT_AUTH_SOURCE_FIELD));
            this.setExpiration(claims.getExpirationTime());
            this.setSub(claims.getSubject());

            final List<String> roles = getRoles(claims);
            if (roles != null) {
                this.setRoles(new HashSet<>(roles));
            } else { //TODO SECURITY ISSUE!!! must remove only here because mitreid not setup properly
            	HashSet<String> hashRoles = new HashSet<>();
            	hashRoles.add("ROLE_API");
            	hashRoles.add("ROLE_ADMIN");
            	hashRoles.add("ROLE_GOD_TOOL");
            	this.setRoles(hashRoles);
            }
            final List<String> scopes = claims.getStringListClaim(JWT_SCOPES_FIELD);
            if (scopes != null) {
                this.setScopes(new HashSet<>(scopes));
            }

            UserIdentity.updateUniqueID(this);
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
                log.debug("Could not get jwt claims. See stack trace.");
            }
        }
    }
    
    /**
     * Get a JWTUserIdentity from a jwt string
     * @param jwtString The string to parse
     * @throws ParseException An exception from parsing the string passed
     */
    public JWTUserIdentity(String jwtString) throws ParseException {
        this(SignedJWT.parse(jwtString));
    }

    public JWTUserIdentity(String jwtString, String refreshToken) throws ParseException {
        this(jwtString);
        this.refreshToken = refreshToken;
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeyCloakRolesFromRealm(final JWTClaimsSet claims) throws ParseException {
        Map<String, Object> result = (Map<String, Object>) claims.getClaim("realm_access");
        if(result == null) {
        	return null;
        }
        List<String> roles = (List<String>) result.get("roles");
        return roles;
    }

    private List<String> getRoles(final JWTClaimsSet claims) throws ParseException {
        List<String> results = claims.getStringListClaim(JWT_ROLES_FIELD);
        if (results == null) {
            results = getKeyCloakRolesFromRealm(claims);
        }
       
        return results;
    }
}
