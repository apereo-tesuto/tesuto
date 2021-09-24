package org.ccctc.common.commonidentity.domain.identity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientCredentials {
    @JsonProperty("access_token")
    @Getter @Setter private String accessTokenValue;
    
    @JsonProperty("id_token")
    @Getter @Setter private String idToken;

    @JsonProperty("refresh_token")
    @Getter @Setter private String refreshToken;

    @JsonProperty("token_type")
    @Getter @Setter private String tokenType;

    @JsonProperty("scope")
    @Getter @Setter private String scope;
}
