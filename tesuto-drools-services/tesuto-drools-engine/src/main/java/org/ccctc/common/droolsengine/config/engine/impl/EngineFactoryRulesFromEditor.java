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
package org.ccctc.common.droolsengine.config.engine.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.DrlInvalidSyntaxException;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.DrlDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.kie.api.runtime.KieContainer;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Build kcontainers based on DRLs in the drools-editor database.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class EngineFactoryRulesFromEditor extends AbstractEngineFactory {

    public EngineFactoryRulesFromEditor(DroolsEngineEnvironmentConfiguration config, RestTemplate restTemplate, String engine) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.engineName = engine;
    }

    @Override
    public KieContainer getContainer(FamilyDTO familyDTO) {
        List<String> drls = getDrls(familyDTO);
        try {
            KieContainer kContainer = kContainerFactory.createKieContainerFromDrlStrings(drls);
            return kContainer;
        }
        catch (Exception exception) {
            throw new DrlInvalidSyntaxException("Failed loading rulesets for college: " + familyDTO.getFamilyCode(), drls.toString(), 
                                                null, exception);
        }
    }

    String getDrlFromRuleSetId(String ruleSetId) {
        List<String> drls = getDrlsFromRuleSetId(ruleSetId);
        return StringUtils.join(drls, "\n\n");
    }

    @Override
    public List<String> getDrls(FamilyDTO familyDTO) {;
        List<RuleSetDTO> ruleSetDTOs = getRuleSetDTOs(familyDTO.getFamilyCode());
        List<String> drls = new ArrayList<String>();
        for (RuleSetDTO ruleSetDTO : ruleSetDTOs) {
            String ruleSetId = ruleSetDTO.getId();
            try {
                List<String> addDrls = getDrlsFromRuleSetId(ruleSetId);
                if (CollectionUtils.isNotEmpty(addDrls)) {
                    drls.addAll(addDrls);
                }
            }
            catch (Exception exception) {
                throw new DrlInvalidSyntaxException(
                                String.format("Failed loading rulesSet:  %s and college %s", ruleSetId, familyDTO.getFamilyCode()), 
                                null, null, exception);
            }
        }
        return drls;
    }

    public List<String> getDrlsFromRuleSetId(String ruleSetId) {
        List<String> drls = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>("", headers);

        ResponseEntity<DrlDTO> response = restTemplate.exchange(getEditorDrlURL() + "/" + ruleSetId, HttpMethod.GET, entity,
                        DrlDTO.class);
        HttpStatus statusCode = response.getStatusCode();
        log.debug("Response to getStudentProfile, statusCode:[" + statusCode + "]");
        if (!statusCode.equals(HttpStatus.OK)) {
            log.error("Unable to retrieve DRL for ruleSetID[" + ruleSetId + "], status:[" + statusCode + "]");
            return drls;
        }
        if (response.getBody() != null && response.getBody().getDrls() != null && response.getBody().getDrls().length > 0) {
            drls.addAll(Arrays.asList(response.getBody().getDrls()));
        }

        if (response.getBody() != null && response.getBody().getDrl() != null) {
            drls.add(response.getBody().getDrl());
        }
        if (log.isDebugEnabled()) {
            log.debug("DRL for ruleSetId[" + ruleSetId + "] retrieves, drl:[" + drls + "]");
        }
        return drls;
    }

    String getEditorDrlURL() {
        return config.getDroolsEditorURL() + "/drl";
    }

    String getEditorRuleSetURL(String cccMisCode) {
        return config.getDroolsEditorURL() + "/rulesets?status=published&application=" + engineName + "&cccMisCode=" + cccMisCode;
    }

    public List<RuleSetDTO> getRuleSetDTOs(String cccMisCode) {
        HttpEntity<Object> entity = getEntityWithHeaders();
        ResponseEntity<RuleSetDTO[]> response = restTemplate.exchange(getEditorRuleSetURL(cccMisCode), HttpMethod.GET, entity, RuleSetDTO[].class);
        HttpStatus statusCode = response.getStatusCode();
        log.debug("Response to request to get rule set, statusCode:[" + statusCode + "]");
        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            log.error("rule set for [" + cccMisCode + "] not found");
            return null;
        }
        List<RuleSetDTO> ruleSetDTOs = new ArrayList<RuleSetDTO>();
        if (response.getBody().length > 0) {
            ruleSetDTOs = Arrays.asList(response.getBody());
        }
        return ruleSetDTOs;
    }

    @Override
    public boolean iskContainerBeingBuilt(FamilyDTO familyDTO) {
        // TODO Implement appropriate logic
        return false;
    }
}
