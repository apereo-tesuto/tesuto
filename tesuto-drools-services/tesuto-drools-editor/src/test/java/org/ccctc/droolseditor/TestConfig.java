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

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsdb.dynamodb.services.DynamoDBTableService;
import org.ccctc.common.droolsdb.dynamodb.services.FamilyServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleSetRowServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.services.RuleSetServiceImpl;
import org.ccctc.common.droolsdb.dynamodb.utils.ActionParameterDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.ActionRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.ConditionRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetRowDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleVariableDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleVariableRowDTOMapper;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.ccctc.droolseditor.controllers.DecisionTreeController;
import org.ccctc.droolseditor.controllers.RuleTemplateController;
import org.ccctc.droolseditor.services.DecisionTreeViewMapper;
import org.ccctc.droolseditor.services.RuleTemplateViewMapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;

@TestConfiguration
@EnableDynamoDBRepositories(basePackages = "org.ccctc.common.droolsdb", dynamoDBOperationsRef = "dynamoDBOperations")
public class TestConfig {
    @Autowired
    private Environment env;
    
    @Bean
    public AWSCredentials amazonAWSCredentials() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        return credentials;
    }
    
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        System.getProperties().setProperty("sqlite4java.library.path", "native-libs");
        AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();
        return ddb;
    }
    
    @Bean
    public DecisionTreeController decisionTreeController() {
    	DecisionTreeController credentials = new DecisionTreeController();
        return credentials;
    }
    
    
    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean(@Value("classpath*:mappings/*mappings.xml") Resource[] resources) throws Exception {
        final DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
        dozerBeanMapperFactoryBean.setMappingFiles(resources);
        return dozerBeanMapperFactoryBean;
    }
    
    private DynamoDBMapperConfig dynamoDBMapperConfig() {
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setTableNameOverride(tableNameOverride());
        return builder.build();
    }
    
    @Bean
    public DynamoDBOperations dynamoDBOperations() {
        DynamoDBTemplate template = new DynamoDBTemplate(amazonDynamoDB(), dynamoDBMapperConfig());
        return template;
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
    public IFamilyService getCollegeService() {
        FamilyServiceImpl collegeService = new FamilyServiceImpl();        
        return collegeService;
    }
	
	@Bean
	public ConditionRowDTOMapper getConditionRowDTOMapper() {
		ConditionRowDTOMapper conditionRowDTOMapper = new ConditionRowDTOMapper();
		return conditionRowDTOMapper;
	}
	
	@Bean
	public DecisionTreeViewMapper getDecisionTreeViewMapper() {
    	DecisionTreeViewMapper mapper = new DecisionTreeViewMapper();
		return mapper;
	}
	
	
	@Bean
    public DrlService getDrlService() {
        DrlService drlService = new DrlService();        
        return drlService;
    }
    
    @Bean
	public DynamoDBTableService getDynamoDBTableService() {
		DynamoDBTableService dynamoDBTableService = new DynamoDBTableService();
		return dynamoDBTableService;
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
    public RuleTemplateViewMapper getRuleTemplateViewMapper() {
    	RuleTemplateViewMapper ruleTemplateViewMapper = new RuleTemplateViewMapper();
        return ruleTemplateViewMapper;
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
    
    @Bean
    public RuleTemplateController ruleTemplateController() {
    	RuleTemplateController controller = new RuleTemplateController();
        return controller;
    }
    
    private DynamoDBMapperConfig.TableNameOverride tableNameOverride() {
        String tablePrefix = env.getProperty("aws.tablePrefix", "");
        String environmentPrefix = StringUtils.isBlank(tablePrefix) ? "" : tablePrefix + "-";
        return DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(environmentPrefix);
    }
}
