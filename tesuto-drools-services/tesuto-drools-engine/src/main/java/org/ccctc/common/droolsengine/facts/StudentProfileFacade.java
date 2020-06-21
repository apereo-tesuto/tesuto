/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.ccctc.common.droolsengine.facts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class StudentProfileFacade {
    @Autowired
    private DroolsEngineEnvironmentConfiguration config;
        
    private String profileServiceURI;

    @Autowired
    private RestTemplate restTemplate = null;
    
    /**
     * @throws @IllegalArgumentException if PROFILE_URL not found in system properties
     */
    public String getProfileServiceURI() {
        if (StringUtils.isBlank(this.profileServiceURI)) {
            String profileURL = config.getProfileUrl();
            if (StringUtils.isBlank(profileURL)) {
                throw new IllegalArgumentException("System property [PROFILE_URL] cannot be blank");
            }
            UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(profileURL);
            profileServiceURI = uri.build().toString();
            log.debug("---StudentProfileFacade Initializing, profileServiceURI:[" + profileServiceURI + "]");            
        }
        return this.profileServiceURI;
    }
    
    /**
     * @return a Map<String, Object> of all attributes associated with the student profile, null if student profile not found
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> getStudentProfile(String cccid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String contentBody = "{\"cccids\":[\"" + cccid + "\"]}";
        HttpEntity<Object> entity = new HttpEntity<Object>(contentBody, headers);
        String messageProfileURL = getProfileServiceURI() + "/studentProfiles";
        
        ResponseEntity<List> response = restTemplate.exchange(messageProfileURL, HttpMethod.POST, entity, List.class);
        HttpStatus statusCode = response.getStatusCode();
        List<Map<String, Object>> studentMessageProfiles = (List<Map<String, Object>>) response.getBody();        
        if (statusCode.equals(HttpStatus.NOT_FOUND) || studentMessageProfiles.size() < 1) {
            log.error("ProfileFacade:studentProfiles - [" + cccid + "] not found");
            return null;
        }
        return studentMessageProfiles.get(0);
    }

    public void setConfiguration(DroolsEngineEnvironmentConfiguration config) {
    	this.config = config;
    }
}
