package org.ccctc.web.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccctc.web.client.service.dto.AuditDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Configuration
@PropertySource(value = "classpath:auditService.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/opt/ccc/config/auditService.properties", ignoreResourceNotFound = true)
@Slf4j
public class AuditServiceClient extends AbstractServiceClient {
    public static final String EVENT_TYPE_FILE_DELETED_PRIVATE = "file_delete_private";
    public static final String EVENT_TYPE_FILE_UPLOAD_PRIVATE = "file_upload_private";
    public static final String EVENT_TYPE_MESSAGING = "messaging";
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void init(final Environment environment) {
        super.init(environment);
        serviceUrl = adjustForEnv(environment.getProperty("audit.url", "https://gateway.{{ENV}}.cccmypath.org/audit-service/audit"));
        buildServiceAccountManager(environment);
        log.debug("********** Using service URL {}", serviceUrl);
    }
    
    @SuppressWarnings("unchecked")
    public void createAudit(AuditDTO dto) throws JsonParseException, JsonMappingException, IOException {
        log.debug("Handling request to create audit record [ {} ]", dto);
        new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = mapper.convertValue(dto, Map.class);
                restHandler.makeAuthenticatedRestCall(HttpMethod.POST, serviceUrl, map);                
            }
        }.run();
    }
    
    public void createAudits(List<AuditDTO> dtos) {
        log.debug("Handling request to batch create audit records");
        restHandler.makeAuthenticatedRestCall(HttpMethod.POST, serviceUrl + "/batch", dtos);
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditDTO> getAuditRecordsByEventAndOriginator(String eventType, String originator) {
        String getAuditsByEventTypeAndOriginatorTargetUrl = serviceUrl + "/eventType/" + eventType + "/originator/" + originator;
        log.debug("Fetch request to {}", getAuditsByEventTypeAndOriginatorTargetUrl);
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, getAuditsByEventTypeAndOriginatorTargetUrl, null);
        ArrayList<AuditDTO> results = new ArrayList<AuditDTO>();

        if (re.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> records = (List<Map<String, Object>>) re.getBody();

            final ObjectMapper mapper = new ObjectMapper();
            records.forEach(record -> {
                results.add(mapper.convertValue(record, AuditDTO.class));
            });

        }
        return results;
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditDTO> getAuditRecordsForSource(String source) {
        String getAuditsByOriginatorTargetUrl = serviceUrl + "/originator/" + source;
        log.debug("Fetch request to {}", getAuditsByOriginatorTargetUrl);
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.GET, getAuditsByOriginatorTargetUrl, null);
        ArrayList<AuditDTO> results = new ArrayList<AuditDTO>();

        if (re.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> records = (List<Map<String, Object>>) re.getBody();

            final ObjectMapper mapper = new ObjectMapper();
            records.forEach(record -> {
                results.add(mapper.convertValue(record, AuditDTO.class));
            });

        }
        return results;
    }
}
