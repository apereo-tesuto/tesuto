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
package org.ccctc.common.droolsengine.engine_actions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.RulesAction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Abstract class for an action that will make a rest service call
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AbstractRestActionService extends AbstractActionService {

    @Value("${ACTION_RETRY_COUNT:5}")
    private int MAX_RETRY_COUNT;

    @Value("${ACTION_RETRY_DELAY_INMILLIS:10000}")
    private int RETRY_SLEEP_IN_MILLIS;

    @Autowired
    private RestTemplate restTemplate = null;

    public List<String> doExecute(RulesAction action, Map<String, Object> facts) {
        log.debug("Excuting rest call: MAX_RETRY_COUNT:[" + MAX_RETRY_COUNT + "], delay:[" + RETRY_SLEEP_IN_MILLIS + "]");
        List<String> errors = new ArrayList<String>();
        String targetUrl = getTargetUrl(action, facts);
        if (StringUtils.isBlank(targetUrl)) {
            errors.add("TargetURL cannot be blank");
        }

        if (errors.size() > 0) {
            log.error("Unable to process [" + this.getName() + "] due to invalid REST call Target URL");
            return errors;
        }

        HttpHeaders headers = buildHeaders(facts);
        errors.addAll(addCustomHeaders(headers, facts));

        if (errors.size() > 0) {
            log.error("Unable to process [" + this.getName() + "] due to Header configuration errors");
            return errors;
        }

        Object contentBody = buildContentBody(action, facts);
        HttpEntity<Object> entity = new HttpEntity<Object>(contentBody, headers);

        boolean success = false;
        int retryCount = 0;
        while (!success && retryCount++ < MAX_RETRY_COUNT) {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, getHttpMethod(action, facts), entity, String.class);
            if (getExpectedHttpStatuses(action, facts).contains(response.getStatusCode())) {
                success = true;
            } else {
                errors.add("[" + getName() + "] REST Request sent, returned status code of [" + response.getStatusCode()
                                + "], but should be one of [" + getExpectedHttpStatuses(action, facts).toString()
                                + "]. Possible retry pending.");
                sleep(RETRY_SLEEP_IN_MILLIS);
            }
        }

        return errors;
    }

    private void sleep(int sleepDuration) {
        log.debug("Sleeping for [" + sleepDuration + "] milliseconds");
        try {
            Thread.sleep(sleepDuration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("finished sleeping");
    }

    /**
     * Default Implementation of expected HttpMethod. If RulesAction contains an "action" parameter,
     * different Methods will be called. "add" returns HttpMethod.POST, and "delete" returns HttpMethod.DELETE.
     * 
     * @return
     */
    protected HttpMethod getHttpMethod(RulesAction action, Map<String, Object> facts) {
        String actionName = (String) action.getActionParameters().get("action");
        if ("add".equals(actionName)) {
            return HttpMethod.POST;
        } else if ("delete".equals(actionName)) {
            return HttpMethod.DELETE;
        }
        return HttpMethod.GET;
    }

    /**
     * DefaultImplementation of expected HttpStatus after calling an endpoint. If the RulesAction
     * contains an "action" parameter, different statuses will be returned. "add" returns HttpStatus.CREATED,
     * "delete" returns HttpStatus.OK. Override this method if you need additional results.
     * 
     * @return
     */
    protected List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        String actionValue = (String) action.getActionParameters().get("action");
        List<HttpStatus> result = new ArrayList<HttpStatus>();
        if ("add".equals(actionValue)) {
            result.add(HttpStatus.CREATED);
        } else { // if ("delete".equals(actionValue)) {
            result.add(HttpStatus.OK);
        }
        return result;
    }

    /**
     * Must be defined. Sets the target URL to be called by the REST call.
     * 
     * @return
     */
    public abstract String getTargetUrl(RulesAction action, Map<String, Object> facts);

    /**
     * Can and probably will be overridden. Use this to set the content body of the REST call.
     * Not all calls require a content body, but many do. The content body Object will be mapped to a JSON object.
     * The body can be a POJO or List of POJO's.
     * 
     * @param action
     * @param facts
     * @return
     */
    protected Object buildContentBody(RulesAction action, Map<String, Object> facts) {
        return "";
    };

    /**
     * Can be overridden. Use this to add any additional Headers required by the REST endpoint.
     * 
     * @param headers
     * @return
     */
    protected List<String> addCustomHeaders(HttpHeaders headers, Map<String, Object> facts) {
        return new ArrayList<String>();
    }

    private HttpHeaders buildHeaders(Map<String, Object> facts) {
        HttpHeaders headers = getFactsUtils().buildHeaders(facts);
        return headers;
    }
}
