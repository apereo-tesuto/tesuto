package org.ccctc.web.client.service.dto;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
public class AuditDTO {   
    private Map<String, Object> auditData;
    private String originator;
    private String target;
    private String eventType;
    private String timestamp;
    
    public boolean isValid() {
        return auditData != null && !auditData.isEmpty() 
               && StringUtils.isNotEmpty(originator) 
               && StringUtils.isNotEmpty(target)
               && StringUtils.isNotEmpty(eventType);
    }
}
