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
package org.ccctc.droolseditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.validation.IDrlValidator;
import org.ccctc.common.droolsdb.services.DrlSyntaxValidator;
import org.ccctc.droolseditor.services.DecisionTreeViewMapper;
import org.ccctc.droolseditor.services.RuleEditorClient;
import org.ccctc.droolseditor.services.RuleTemplateViewMapper;
import org.ccctc.droolseditor.validation.RestfulDrlValidator;
import org.ccctc.droolseditor.validation.ValidationDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:application.properties")
public class UIConfig {

    @Value("#{${RULE_SET_ROW_VALIDATORS}}")
    private Map<String,String> ruleSetRowValidators;
    
    @Value("${rule.engine.url}")
    private String ruleEngineUrl;
    
    @Value("${MAX_RETRY_COUNT:5}")
    private Integer MAX_RETRY_COUNT;

    @Value("${RETRY_SLEEP_IN_MILLIS:10000}")
    private Integer RETRY_SLEEP_IN_MILLIS;
    
    @Qualifier("oAuthClientRestTemplate")
    @Autowired
    private RestTemplate restTemplate;
    
    @Bean
    public ValidationDistributor ruleSetRowValidationDistributor() {
        ValidationDistributor distributor = new ValidationDistributor();
        return distributor;
    }
    
    @Bean
    public List<IDrlValidator> getDrlValidationServices() {
        List<IDrlValidator> validationServices = new ArrayList<>();
        for(String event:ruleSetRowValidators.keySet()) {
            RestfulDrlValidator validationService = new RestfulDrlValidator();
            validationService.setEvent(event);
            validationService.setTagetUrl(ruleSetRowValidators.get(event));
            validationService.setRestTemplate(restTemplate);
            validationService.setMaxRetryCount(MAX_RETRY_COUNT);
            validationService.setRetrySleepInMillis(RETRY_SLEEP_IN_MILLIS);
            validationServices.add(validationService);
        }
        return validationServices;
    }
    
	@Bean
	public DrlSyntaxValidator getDrlSyntaxValidator() {
		DrlSyntaxValidator validationService = new DrlSyntaxValidator();
		return validationService;
	}

	@Bean
	public DecisionTreeViewMapper getDecisionTreeViewMapper() {
		DecisionTreeViewMapper mapper = new DecisionTreeViewMapper();
		return mapper;
	}

	@Bean
	public RuleTemplateViewMapper getRuleTemplateViewMapper() {
		RuleTemplateViewMapper ruleTemplateViewMapper = new RuleTemplateViewMapper();
		return ruleTemplateViewMapper;
	}
	
	@Bean
	public RuleEditorClient getRuleEditorClient() {
	    RuleEditorClient client =  new RuleEditorClient();
	    client.setRuleEngineUrl(ruleEngineUrl);
	    return client;
	}
}
