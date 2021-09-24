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
package org.ccctc.droolseditor.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RuleEditorClient {

    @Qualifier("oAuthClientRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    String ruleEngineUrl;

    public DrlValidationResults loadRules() {
        DrlValidationResults results = new DrlValidationResults();
        results.setIsValid(false);
        String targetUrl = ruleEngineUrl + "/ccc/api/drools/v1/engine/loadRules";
        HttpEntity entity = new HttpEntity(buildHeaders());
        try {
            ResponseEntity<DrlValidationResults> response = restTemplate.exchange(targetUrl, HttpMethod.POST, entity,
                    DrlValidationResults.class);
            if (!hasValidHttpStatus(response.getStatusCode())) {
                results.addError("Loading rules was not successful and returned with following status: "
                        + response.getStatusCode() + "]");
            } else {
                results = response.getBody();
            }
        } catch (Exception exception) {
            results.addError("Loading rules was not successful for the following reasons" + exception.getMessage());
        }
        return results;
    }
    
    public List<String> rulesEngineDrls(String cccMisCode) {
        String targetUrl = ruleEngineUrl + "/ccc/api/drools/v1/engine/viewRuleSets/" +  cccMisCode;
        HttpEntity entity = new HttpEntity(buildHeaders());
        try {
            ResponseEntity<String[]> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String[].class);
            String message = "";
            if (response.getStatusCode().equals(HttpStatus.OK)  || response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                 message = "Rules have been successfully loaded\n\n";
            } else {
                message = "Checking Rules Status failed with the following response: "
                        + response.getStatusCode() + "\n\n";
            }
            return Arrays.asList(message, String.join("\n", response.getBody()));
        } catch (Exception exception) {
            return Arrays.asList("Rules Status failed for following reasons" + exception.getMessage() + "\n"
                    + exceptionStacktraceToString(exception));
        }
    }

    public String rulesEngineStatus() {
        String targetUrl = ruleEngineUrl + "/ccc/api/drools/v1/engine";
        HttpEntity entity = new HttpEntity(buildHeaders());
        try {
            ResponseEntity<Object> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, Object.class);
            List<Map<String, String>> values = (List<Map<String, String>>) response.getBody();
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return "Rules have been successfully loaded" + convertStatusToString(values);
            }
            return "Checking Rules Status failed with the following esponse: " + response.getStatusCode().toString()
                    + convertStatusToString(values);
        } catch (Exception exception) {
            return "Rules Status failed for following reasons" + exception.getMessage() + "\n"
                    + exceptionStacktraceToString(exception);
        }
    }
    
    private String convertStatusToString(List<Map<String, String>> values) {
        StringBuffer buffer = new StringBuffer("Statuses");
        if (CollectionUtils.isNotEmpty(values)) {
            for (Map<String, String> map : values) {
                buffer.append("\n");
                map.keySet().forEach(key -> buffer.append(key).append(":").append(map.get(key)).append(","));
            }
        }
        return buffer.toString();
    }

    public DrlValidationResults executeFacts(String map) {
        map = StringUtils.deleteWhitespace(map);
        DrlValidationResults results = new DrlValidationResults();
        results.setIsValid(false);
        String targetUrl = ruleEngineUrl + "/ccc/api/drools/v1/facts";
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(getFacts(map), buildHeaders());
        try {
            ResponseEntity<DrlValidationResults> response = restTemplate.exchange(targetUrl, HttpMethod.POST, entity,
                    DrlValidationResults.class);
            if (!hasValidHttpStatus(response.getStatusCode())) {
                results.addError(
                        "Executing rules with supplied csv was not successful and returned with following status: "
                                + response.getStatusCode() + "]");
            } else {
                results = response.getBody();
            }
        } catch (Exception exception) {
            results.addError("Loading rules was not successful for the following reasons" + exception.getMessage());
            results.setExceptionTrace(exceptionStacktraceToString(exception));
            results.setDrl("");
        }
        return results;
    }

    public Map<String, String> getFacts(String map) {
        Map<String, String> facts = new HashMap<>();
        map = map.replace("{", "");
        map = map.replace("}", "");
        String[] pairs = map.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                facts.put(keyValue[0], keyValue[1]);
            }
        }
        return facts;
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public void setRuleEngineUrl(String ruleEngineUrl) {
        this.ruleEngineUrl = ruleEngineUrl;
    }

    public Boolean hasValidEndpoint() {
        return StringUtils.isNotBlank(ruleEngineUrl);
    }

    Boolean hasValidHttpStatus(HttpStatus code) {
        return code.equals(HttpStatus.ACCEPTED) || code.equals(HttpStatus.OK);
    }

    private String exceptionStacktraceToString(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

}
