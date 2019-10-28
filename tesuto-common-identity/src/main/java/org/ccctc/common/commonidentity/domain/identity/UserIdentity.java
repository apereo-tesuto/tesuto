package org.ccctc.common.commonidentity.domain.identity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.mitre.openid.connect.model.Address;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Identity - Used within Web Applications and REST services to identity the Authenticated
 * and Authorized User making the request
 * Created by Parker Neff - Unicon Inc on 12/5/15.
 */
@Entity
@Slf4j
public class UserIdentity implements Serializable, UserDetails, UserInfo {
    private static final long serialVersionUID = 2L;
    public static final String AFFLIATION_AFFILATE = "affiliate";
    public static final String AFFLIATION_STUDENT = "student";
    public static final String AFFLIATION_STAFF = "staff";

    @Id
    @Getter @Setter private String uniqueID = null;

    private static String strDefault(String... args) {
        if (args != null && args.length > 0) {
            for(String next : args) {
                if (!StringUtils.isEmpty(next)) {
                    return next;
                }
            }
        }
        return "";
    }

    /**
     * Updates the unique identity property of the given {@link UserIdentity}. The precedence is:
     * <ol>
     *     <li>cccId</li>
     *     <li>eppn</li>
     *     <li>clientId</li>
     *     <li>jwt `sub` claim (needed for client_credentials grants)</li>
     * </ol>
     *
     * The reason for having this centralized is to clarify the logic and ideally keep it maintainable
     * @param id The UserIdentity to update.
     */
    public static void updateUniqueID(UserIdentity id) {
        if (id == null) {
            return;
        }

        id.setUniqueID(strDefault(id.getCccId(), id.getEppn(), id.getClientId(), id.getOriginalSub()));
    }

    @Getter @Setter private String NameID;
    @Getter(onMethod = @__({@JsonProperty("cccId")})) private String cccId;
    @Getter @Setter private String email;
    @Getter private String eppn = null;
    @Getter @Setter private String clientId;
    @Getter @Setter private String firstName;
    @Getter @Setter private String lastName;
    @Getter @Setter private String misCode;
    @Getter @Setter private String mobilePhone;
    @Getter @Setter private String phone;
    @Getter @Setter private String primaryAffiliation;
    private Set<String> affiliations = new HashSet<>();
    @Getter(onMethod = @__({@JsonProperty("entitlements")})) private Set<String> entitlements = new HashSet<>();
    @Getter(onMethod = @__({@JsonProperty("scopes")})) private Set<String> scopes = new HashSet<>();
    @Setter private String displayName;
    @Setter @Getter private String authSource;
    @Getter @Setter private Set<String> roles = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

    private String sub;

    public String getOriginalSub() {
        return this.sub;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
        updateUniqueID(this);
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
        updateUniqueID(this);
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = firstName + " " + lastName;
        }
        return displayName;
    }

    public void setAffiliations(String[] affilations) {
        if (affilations != null) {
            this.affiliations = new HashSet<>(Arrays.asList(affilations));
        }
    }

    @JsonIgnore
    public void setAffiliations(List<String> affiliations) {
        if (affiliations == null) {
            this.affiliations = new HashSet<>();
            return;
        }
        this.affiliations = new HashSet<>(affiliations);
    }

    @JsonProperty("affiliations")
    public List<String> getAffiliationList() {
        return new ArrayList<>(affiliations);
    }

    @JsonIgnore
    public String[] getAffiliations() {
        return affiliations.toArray(new String[0]);
    }

    public void setEntitlements(String[] entitlements) {
        if (entitlements != null) {
            this.entitlements = new HashSet<>(Arrays.asList(entitlements));
            return;
        }
        this.entitlements = null;
    }

    @JsonIgnore
    public void setEntitlements(Set<String> entitlements) {
        this.entitlements = entitlements;
    }

    public void setScopes(Collection<String> scopes) {
        this.scopes.clear();

        if (scopes == null) {
            return;
        }

        this.scopes.addAll(scopes);
    }

    /**
     * An enrolled Student is a student that is enrolled at a college (Has the value of 'student' in the list of affiliations)
     * @return true if enrolled student
     */
    public boolean isEnrolledStudent() {
        return affiliations.contains(AFFLIATION_STUDENT);
    }

    /**
     * An affiliated student is a student that may have used the portal, applied to college, but did not log in
     * though a college ID
     *
     * @return true if affililated student
     */
    public boolean isAffiliatedStudent() {
        boolean affiliate = affiliations.contains(AFFLIATION_AFFILATE);
        boolean student = affiliations.contains(AFFLIATION_STUDENT);
        return affiliate && !student;
    }

    /**
     * Returns true if any type of student (affliated or an enrolled student)
     * @return
     */
    public boolean isStudent() {
        return isEnrolledStudent() || isAffiliatedStudent();
    }
    /**
     * Returns true if user is staff
     * @return
     */
    public boolean isStaff() {
        return affiliations.contains(UserIdentity.AFFLIATION_STAFF) && !isStudent();
    }

    public Date getExpiration() {
        return expiration;
    }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    // Implements Spring Security's UserDetails interface

    @Setter private Set<GrantedAuthority> authorities = new HashSet<>();
    private static final SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");

    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Cache results
        if (authorities != null && authorities.size() > 1) {
            return authorities;
        }

        if (authorities == null) {
            authorities = new HashSet<>();
        }

        if (entitlements != null) {
            // Add all entitlements and affiliations as roles
            for (String entitlement : entitlements) {
                authorities.add(new SimpleGrantedAuthority(entitlement.toUpperCase()));
            }
        }
        if (affiliations != null) {
            for (String affiliation : affiliations) {
                authorities.add(new SimpleGrantedAuthority(affiliation.toUpperCase()));
            }
        }

        if(roles != null) {
            for (String role: roles) {
                authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
            }
        }

        if (!authorities.contains(userAuthority)) {
            authorities.add(userAuthority);
        }

       return authorities;
    }
    public String getPassword() {
        return null;
    }
    public String getUsername() {
        return getUniqueID();
    }
    public boolean isAccountNonExpired() {
        return true;
    }
    public boolean isAccountNonLocked() {
        return true;
    }
    public boolean isCredentialsNonExpired() {
        return true;
    }
    public boolean isEnabled() {
        return true;
    }

    // Implements Mitre's UserInfo interface

    public String getSub() {
        return uniqueID;
    }
    public void setSub(String s) {
        this.sub = s;
        updateUniqueID(this);
    }

    public String getPreferredUsername() {
        return getEppn();
    }
    public void setPreferredUsername(String s) {
        setEppn(s);
    }
    public String getName() {
        return getDisplayName();
    }
    public void setName(String s) {
        setDisplayName(s);
    }
    public String getGivenName() {
        return getFirstName();
    }
    public void setGivenName(String s) {
        setFirstName(s);
    }
    public String getFamilyName() {
        return getLastName();
    }
    public void setFamilyName(String s) {
        setLastName(s);
    }
    public String getMiddleName() {
        return "";
    }
    public void setMiddleName(String s) {}
    public String getNickname() {
        return getDisplayName();
    }
    public void setNickname(String s) {setDisplayName(s);}
    public String getProfile() {
        return null;
    }
    public void setProfile(String s) {

    }
    public String getPicture() {
        return null;
    }
    public void setPicture(String s) {
    }
    public String getWebsite() {
        return null;
    }
    public void setWebsite(String s) {}
    public Boolean getEmailVerified() {
        return true;
    }
    public void setEmailVerified(Boolean aBoolean) {

    }
    public String getGender() {
        return null;
    }
    public void setGender(String s) {

    }
    public String getZoneinfo() {
        return null;
    }
    public void setZoneinfo(String s) {

    }
    public String getLocale() {
        return null;
    }
    public void setLocale(String s) {

    }
    public String getPhoneNumber() {
        return getPhone();
    }
    public void setPhoneNumber(String s) {
        setPhone(s);
    }

    public Boolean getPhoneNumberVerified() {
        return true;
    }
    public void setPhoneNumberVerified(Boolean aBoolean) {

    }
    public Address getAddress() {
        return null;
    }
    public void setAddress(Address address) {

    }
    public String getUpdatedTime() {
        return null;
    }
    public void setUpdatedTime(String s) {

    }
    public String getBirthdate() {
        return null;
    }
    public void setBirthdate(String s) {

    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonObject toJson() {
        try {
            final String s = mapper.writeValueAsString(this);
            return (new JsonParser()).parse(s).getAsJsonObject();
        } catch (JsonProcessingException e) {
            log.error("Could not serialize user info", e);
        }
        return null;
    }
    public JsonObject getSource() {
        return null;
    }
}
