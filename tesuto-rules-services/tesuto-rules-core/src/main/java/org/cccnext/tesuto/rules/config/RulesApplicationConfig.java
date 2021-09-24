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
package org.cccnext.tesuto.rules.config;

import org.cccnext.tesuto.placement.client.PlacementRestClient;
import org.cccnext.tesuto.placement.service.PlacementReader;
import org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils;
import org.cccnext.tesuto.rules.service.RuleSetViewMapper;
import org.cccnext.tesuto.rules.validation.ApmsErrorHeaders;
import org.cccnext.tesuto.rules.validation.ErrorHeaders;
import org.ccctc.common.droolsdb.dynamodb.services.EngineServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.FamilyServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleSetRowServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleSetServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.utils.ActionParameterDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.ActionRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.ApplicationDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.ConditionRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleVariableDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleVariableRowDTOMapper;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.common.droolsdb.services.DrlSyntaxValidator;
import org.ccctc.common.droolsdb.services.IEngineService;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.impl.EngineFactoryRulesFromMaven;
import org.ccctc.common.droolsengine.config.service.impl.ServiceConfiguratorFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


@Configuration
@ComponentScan(basePackages={"org.ccctc.common"}
,excludeFilters= {@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,classes={ServiceConfiguratorFactoryImpl.class})})
public class RulesApplicationConfig {

   // @Bean
    //public ConfigurationReaderFactory getConfigurationReaderFactory() {
    //    return new ConfigurationReaderFactory();
    //}
//
	@Value("default.engine.application")
	String mavenEngineName;
	
	@Autowired
	private DroolsEngineEnvironmentConfiguration config;
	
    @Bean
    public IFamilyService getCollegeService() {
    	FamilyServiceImpl collegeService = new FamilyServiceImpl();
        return collegeService;
    }
    
    @Bean
    public IEngineService getEngineService() {
    	IEngineService collegeService = new EngineServiceImpl();
        return collegeService;
    }

    @Bean
    public RuleService getRuleService() {
        RuleServiceImpl ruleService = new RuleServiceImpl();
        return ruleService;
    }

    @Bean
    public RuleSetService getRuleSetService() {
        RuleSetServiceImpl ruleSetService = new RuleSetServiceImpl();
        return ruleSetService;
    }

    @Bean
    public RuleSetRowService getRuleSetRowService() {
        RuleSetRowService ruleSetRowService = new RuleSetRowServiceImpl();
        return ruleSetRowService;
    }
    
    @Bean
    public ApplicationDTOMapper getEngineDTOMapper() {
        return new ApplicationDTOMapper();
    }   

    @Bean
    public RuleSetRowDTOMapper getRuleSetRowMapper() {
        RuleSetRowDTOMapper rowSetRowMapper = new RuleSetRowDTOMapper();
        return rowSetRowMapper;
    }

    @Bean
    public RuleSetDTOMapper getRuleSetMapper() {
        RuleSetDTOMapper rowSetMapper = new RuleSetDTOMapper();
        return rowSetMapper;
    }

    @Bean
    public RuleDTOMapper getRuleDTOMapper() {
        RuleDTOMapper rowMapper = new RuleDTOMapper();
        return rowMapper;
    }

    @Bean
    public RuleVariableRowDTOMapper getRuleVariableRowDTOMapper() {
        RuleVariableRowDTOMapper ruleVariableRowDTOMapper = new RuleVariableRowDTOMapper();
        return ruleVariableRowDTOMapper;
    }

    @Bean
    public RuleVariableDTOMapper getRuleVariableDTOMapper() {
        RuleVariableDTOMapper ruleVariableDTOMapper = new RuleVariableDTOMapper();
        return ruleVariableDTOMapper;
    }

    @Bean
    public ActionParameterDTOMapper getActionParameterDTOMapper() {
        ActionParameterDTOMapper actionParameterMapper = new ActionParameterDTOMapper();
        return actionParameterMapper;
    }

    @Bean
    public ConditionRowDTOMapper getConditionRowDTOMapper() {
        ConditionRowDTOMapper conditionRowDTOMapper = new ConditionRowDTOMapper();
        return conditionRowDTOMapper;
    }

    @Bean
    public ActionRowDTOMapper getActionRowDTOMapper() {
        ActionRowDTOMapper actoinRowDTOMapper = new ActionRowDTOMapper();
        return actoinRowDTOMapper;
    }

    @Bean
    public DrlService getDrlService() {
        DrlService drlService = new DrlService();
        return drlService;
    }
    
    @Bean
    public DrlSyntaxValidator getDrlSyntaxValidator() {
        DrlSyntaxValidator validationService = new DrlSyntaxValidator();
        return validationService;
    }

    @Bean
    public RuleSetViewMapper getRuleSetRowViewMapper() {
        RuleSetViewMapper mapper = new RuleSetViewMapper();
        return mapper;
    }
    
    @Bean
    public MultipleMeasureFactProcessingUtils getMultipleMeasureFactProcessingUtils() {
        MultipleMeasureFactProcessingUtils factProcessingUtils = new MultipleMeasureFactProcessingUtils();
        return factProcessingUtils;
    }
    
    @Bean
    public PlacementReader getPlacementReader() {
        PlacementRestClient placementRestClient = new PlacementRestClient();
        return placementRestClient;
    }
    @Bean
    public ErrorHeaders getApmsErrorHeaders() {
    	ApmsErrorHeaders apmsErrorHeaders = new ApmsErrorHeaders();
        return apmsErrorHeaders;
    }
    
    @Bean
    public  EngineFactoryRulesFromMaven engineFactoryRulesFromMaven() {
        return new EngineFactoryRulesFromMaven(config, mavenEngineName);
    }
    
    
}
