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
import org.ccctc.common.droolsdb.services.IEngineService;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.ccctc.droolseditor.services.FamilyViewMapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = {"org.ccctc.droolseditor","org.ccctc.common.droolsdb"},
               excludeFilters = { @Filter(type = FilterType.REGEX, pattern="org.ccctc.droolseditor.*TestConfig") })
public class RulesApplicationConfig {
    @Bean
	public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean(
			@Value("classpath*:mappings/*mappings.xml") Resource[] resources) throws Exception {
		final DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
		dozerBeanMapperFactoryBean.setMappingFiles(resources);
		return dozerBeanMapperFactoryBean;
	}
    
    @Bean
	public ActionParameterDTOMapper getActionParameterDTOMapper() {
		ActionParameterDTOMapper actionParameterMapper = new ActionParameterDTOMapper();
		return actionParameterMapper;
	}
  
    @Bean
	public ActionRowDTOMapper getActionRowDTOMapper() {
		ActionRowDTOMapper actoinRowDTOMapper = new ActionRowDTOMapper();
		return actoinRowDTOMapper;
	}
    
    @Bean
    public ApplicationDTOMapper getEngineDTOMapper() {
        return new ApplicationDTOMapper();
    }
    
    @Bean
    public IEngineService getEngineService() {
        EngineServiceImpl applicationService = new EngineServiceImpl();        
        return applicationService;
    }
    
    @Bean
    public IFamilyService getCollegeService() {
        FamilyServiceImpl collegeService = new FamilyServiceImpl();        
        return collegeService;
    }
    
    @Bean
    public FamilyViewMapper getCollegeViewMapper() {
        FamilyViewMapper collegeViewMapper = new FamilyViewMapper();
        return collegeViewMapper;
    }
	
	@Bean
	public ConditionRowDTOMapper getConditionRowDTOMapper() {
		ConditionRowDTOMapper conditionRowDTOMapper = new ConditionRowDTOMapper();
		return conditionRowDTOMapper;
	}
	
	@Bean
    public DrlService getDrlService() {
        DrlService drlService = new DrlService();        
        return drlService;
    }
	
    @Bean
	public RuleDTOMapper getRuleDTOMapper() {
		RuleDTOMapper rowMapper = new RuleDTOMapper();
		return rowMapper;
	}	
	
	@Bean
    public RuleService getRuleService() {
        RuleServiceImpl ruleService = new RuleServiceImpl();        
        return ruleService;
    }
	
	@Bean
	public RuleSetDTOMapper getRuleSetMapper() {
		RuleSetDTOMapper rowSetMapper = new RuleSetDTOMapper();
		return rowSetMapper;
	}
	
	@Bean
    public RuleSetRowDTOMapper getRuleSetRowMapper() {
    	RuleSetRowDTOMapper rowSetRowMapper = new RuleSetRowDTOMapper();
        return rowSetRowMapper;
    }
	
	
	@Bean
    public RuleSetRowService getRuleSetRowService() {
    	RuleSetRowService ruleSetRowService = new RuleSetRowServiceImpl();
        return ruleSetRowService;
    }

	@Bean
    public RuleSetService getRuleSetService() {
        RuleSetServiceImpl ruleSetService = new RuleSetServiceImpl();        
        return ruleSetService;
    }

    
    @Bean
	public RuleVariableDTOMapper getRuleVariableDTOMapper() {
		RuleVariableDTOMapper ruleVariableDTOMapper = new RuleVariableDTOMapper();
		return ruleVariableDTOMapper;
	}
    
    @Bean
	public RuleVariableRowDTOMapper getRuleVariableRowDTOMapper() {
		RuleVariableRowDTOMapper ruleVariableRowDTOMapper = new RuleVariableRowDTOMapper();
		return ruleVariableRowDTOMapper;
	}
    
	@Bean //TODO not sure why this is required
    public  PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	return new PropertySourcesPlaceholderConfigurer();
    }
    
}
