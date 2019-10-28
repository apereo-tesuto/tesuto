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
package org.cccnext.tesuto.rules;

import org.ccctc.common.commonidentity.domain.identity.CCCRestTemplate;
import org.ccctc.common.droolscommon.KieContainerFactory;
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
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;
import org.ccctc.common.droolsengine.config.engine.impl.EngineFactoryRulesFromEditor;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;

@SpringBootConfiguration
@EnableDynamoDBRepositories(basePackages = "org.ccctc.common.droolsdb")
public class TestConfig {
    
    @Bean
    DroolsEngineEnvironmentConfiguration droolsEngineEnvironmentConfiguration(){
        return new DroolsEngineEnvironmentConfiguration();
    }
    
    
    @Bean
    public IEngineFactory ruleReaderFactoryImpl() {
        return new EngineFactoryRulesFromEditor(droolsEngineEnvironmentConfiguration(), (RestTemplate)restTemplate(), "test");
    }
    

    @Bean
    public IFamilyService getCollegeService() {
    	FamilyServiceImpl collegeService = new FamilyServiceImpl();        
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
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean(@Value("classpath*:mappings/*mappings.xml") Resource[] resources) throws Exception {
        final DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
        dozerBeanMapperFactoryBean.setMappingFiles(resources);
        return dozerBeanMapperFactoryBean;
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
    public AWSCredentials amazonAWSCredentials() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        return credentials;
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        // This block assumes that you're testing against a local embedded dynamodb.
        System.getProperties().setProperty("sqlite4java.library.path", "native-libs");
        AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();
        return ddb;

        // This block assumes that you want to test against a real AWS instance (i.e. CI,
        // sandbox, etc.
        // Note - this test suite assumes that you can do full table scanes, which
        // is not the case in AWS.  For these tests, we're using a local DynamoDB
        // which allows us to do quite a bit more.
        // Note - When testing against a real AWS instance, your credentials will determine
        // what permissions you have.  You will probably not have Tablescan, and may not be 
        // able to create/delete tables.  You'll need to edit tests if you want to test
        // against real AWS instances.
        // Note - Use 'aws configure --profile <profilename> to set AWS profile credentials
        // you'll need to have a key and secret if you want to use a sandbox instance
        // for testing.  Put them in a profile within ~/.aws/config.
        // TODO - may want to see if the DynamoDB embedded can restrict doing a tablescan,
        // then rewrite the tests to account for that.
//        AmazonDynamoDBClient client = new AmazonDynamoDBClient(amazonAWSCredentials());
//
//        Regions regions = Regions.fromName("us-west-2");
//        Region region = Region.getRegion(regions);
//        client.setRegion(region);
//        return client;
    }

    @Bean
    public KieContainerFactory getKieContainerFactory() {
        KieContainerFactory kieContainerService = new KieContainerFactory();
        return kieContainerService;
    }

    
    @Bean
    public RestTemplate restTemplate() {
        CCCRestTemplate template = new CCCRestTemplate(null);
        return template;
    }
}
