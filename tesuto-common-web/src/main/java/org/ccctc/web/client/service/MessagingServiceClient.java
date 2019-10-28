package org.ccctc.web.client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.web.client.service.dto.MessageRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Configuration
@PropertySource(value = "classpath:messagingService.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/opt/ccc/config/messagingService.properties", ignoreResourceNotFound = true)
@Slf4j
public class MessagingServiceClient extends AbstractServiceClient {

    @Override
    protected void init(final Environment environment) {
        super.init(environment);
        serviceUrl = adjustForEnv(
                        environment.getProperty("messaging.url", "https://gateway.{{ENV}}.cccmypath.org/ccc/api/messages/v1"));
        buildServiceAccountManager(environment);
        log.debug("********** Using service URL {}", serviceUrl);
    }

    /**
     * Use when the information about the targets will be looked up (all you have is the cccid(s)
     * 
     * @param targetCCCIds
     * @param subject
     * @param messageBodyHTML
     * @param misCode
     */
    public void sendMessage(List<String> targetCCCIds, String subject, String messageBodyHTML, String misCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("users", targetCCCIds);        
        map.put("subject", subject);
        map.put("misCode", StringUtils.isEmpty(misCode) ? "000" : misCode);
        log.debug("Handling request to create send notifications using [ {} ]", map);        
        // Add this after the log message, so we aren't polluting the logs with HTML
        map.put("message-body-html", messageBodyHTML);
        
        new Runnable() {
            @Override
            public void run() {
                ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, serviceUrl + "/sendMessages", map);
                log.debug("Attempted notification to {} with result of {}", targetCCCIds, re.getStatusCode());
            }
        }.run();
    }

    /**
     * Easy way to send to a specified MyPath notification, email and/or sms by filling in the MessageRequest object. 
     * @param request
     */
    public void sendMessage(MessageRequest request) {
        if (StringUtils.isNotEmpty(request.getMainPhone())) {
            request.setMainPhoneAuth(true);
        }
        if (StringUtils.isNotEmpty(request.getSecondPhone())) {
            request.setSecondPhoneAuth(true);
        }
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, serviceUrl + "/sendMessagesUsingDetail", request);
        log.debug("Attempted notification to email {} and SMS {} with result of {}", request.getEmail(), request.getMainPhone(),
                        re.getStatusCode());
    }
    
    /**
     * Send email to user defined as the admin of the misCode defined by the messaging service
     */
    public void sendMessageToAdmin(MessageRequest request) {
        String url = serviceUrl + "/colleges/" + request.getMisCode() +"/sendAdminEmail";
        ResponseEntity<?> re = restHandler.makeAuthenticatedRestCall(HttpMethod.POST, url, request);
        log.debug("Attempted notification to admin at {} with result of {}", request.getMisCode(), re.getStatusCode());
    }

    public static void main(String[] args) {
        MessagingServiceClient client = new MessagingServiceClient();
        client.init(new MockEnvironment());

        MessageRequest request = new MessageRequest();
        request.setEmail("chasegawa@unicon.net");
        request.setMainPhone("480-560-1428");
        request.setSubject("${collegeName} - Student file uploaded: ${filename}");
        request.setMessageBodyHtml("<p>The following student file was uploaded: ${filename}</br></br>Please visit ${workdocsUrl} in order to view and/or download the file.</p>");
        
        
        client.sendMessage(request);
    }
}
