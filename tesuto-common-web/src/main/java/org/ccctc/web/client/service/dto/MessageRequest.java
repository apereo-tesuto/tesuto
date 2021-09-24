package org.ccctc.web.client.service.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MessageRequest {
    private String email;
    
    private List<String> groups;
    
    private String mainPhone;
    
    private Boolean mainPhoneAuth;
    
    @JsonProperty(value = "message-body")
    private String messageBody;
    
    @JsonProperty(value = "message-body-html")
    private String messageBodyHtml;
    
    @JsonProperty(value = "message-id")
    private String messageId;
    
    private String misCode;
    
    private String secondPhone;
    
    private Boolean secondPhoneAuth;
    
    private String subject;
    
    @JsonProperty(value = "user-detail-map")
    private Map<String, String> userDetailMap;
    
    /** This is a list of ccc ids */
    private List<String> users;
}
