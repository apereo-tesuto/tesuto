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
package org.ccctc.common.droolsengine.config.family.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.family.AbstractFamilyConfigurator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Build a family reader that gets config from the drools-editor rest services.
 * Logging for this reader prefixed with "+--+"
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class FamilyConfiguratorFromEditor extends AbstractFamilyConfigurator {

    @Autowired
    private RestTemplate restTemplate = null;

    private String getAllFamiliesFromEditorUrl() {
        return config.getDroolsEditorURL() + "/rules-editor/colleges?status=active";
    }

    private List<FamilyDTO> getAllFamilyDtos() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>("", headers);
        log.debug("+--+ Trying endpoint to get families: " + getAllFamiliesFromEditorUrl());
        
        ResponseEntity<String> response = restTemplate.exchange(getAllFamiliesFromEditorUrl(), HttpMethod.GET, entity, String.class);
        List<FamilyDTO> results = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            results = mapper.readValue(response.getBody(), new TypeReference<List<FamilyDTO>>() {
            });
        }
        catch (Exception e) {
            log.warn("+--+ FamilyReaderFromEditor: unable to getAllFamilyDtos()");
            throw e;
        }
        log.debug("+--+ getAllFamilyDtos() returning " + results);
        return results;
    }

    /**
     * If the drools-editor is not be available, we will get errors from the rest call so we want to retry periodically for at least
     * a couple of minutes (the editor shouldn't take that long to become available).
     * 
     * @see org.ccctc.common.droolsengine.config.family.IFamilyConfigurator#getColleges()
     */
    @Override
    public List<FamilyDTO> getFamilies(boolean skipRetries) {
        List<FamilyDTO> results = null;
        int retries = skipRetries ? 30 : 0; // avoid retries if we can for testing
        do {
            try {
                results = getAllFamilyDtos();
                retries = 30;
            }
            catch (Exception e) {
                // The editor service might not have started fully yet, so we want to sleep and then retry
                try {
                    log.debug("+--+ Drools-Editor not available", e);
                    if (retries < 30) {
                        Thread.sleep(10);
                    }
                }
                catch (InterruptedException ignore) {
                }
                retries++;
            }
        } while (retries < 30);
        return results;
    }

    public String getName() {
        return "editor";
    }

    public void setDroolsEngineEnvironmentConfiguration(DroolsEngineEnvironmentConfiguration config) {
        this.config = config;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
