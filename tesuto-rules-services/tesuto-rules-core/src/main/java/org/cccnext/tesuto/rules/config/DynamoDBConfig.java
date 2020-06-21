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

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsdb.dynamodb.services.DynamoDBTableService;


import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration
@EnableDynamoDBRepositories(basePackages = "org.ccctc.common.droolsdb", dynamoDBOperationsRef = "dynamoDBOperations")
public class DynamoDBConfig {

	@Value(value = "${aws.endpoint}")
	private String awsEndpoint;

	@Value(value = "${cloud.aws.region.static}")
	private String cloudAwsRegionStatic;

	@Value(value = "${aws.tablePrefix}")
	private String tablePrefix;

	
    @Bean
    public DynamoDBTableService getDynamoDBTableService() {
        DynamoDBTableService dynamoDBTableService = new DynamoDBTableService();
        return dynamoDBTableService;
    }
    
    @Bean
    public DynamoDBOperations dynamoDBOperations() {
        DynamoDBTemplate template = new DynamoDBTemplate(amazonDynamoDB(), dynamoDBMapper(),dynamoDBMapperConfig());
        return template;
    }

    private DynamoDBMapperConfig dynamoDBMapperConfig() {
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setTableNameOverride(tableNameOverride());
        return builder.build();
    }
    
    private DynamoDBMapper dynamoDBMapper() {
    	return new DynamoDBMapper(amazonDynamoDB());
    }


	private DynamoDBMapperConfig.TableNameOverride tableNameOverride() {
		String environmentPrefix = StringUtils.isBlank(tablePrefix) ? "" : tablePrefix + "-";
		System.out.println("Using table prefix [" + environmentPrefix + "]");
		// String tablePrefix = env.getProperty("aws.tablePrefix", "");
		// String environmentPrefix = StringUtils.isBlank(tablePrefix) ? "" :
		// tablePrefix + "-";
		if (log.isDebugEnabled()) {
			log.debug("Using table prefix [" + environmentPrefix + "]");
		}
		return DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(environmentPrefix);
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
		return credentials;
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
		clientBuilder.withCredentials(new DefaultAWSCredentialsProviderChain());
		if (!StringUtils.isBlank(awsEndpoint)) {
			clientBuilder.withEndpointConfiguration(
					new AwsClientBuilder.EndpointConfiguration(awsEndpoint, cloudAwsRegionStatic));
		}
		AmazonDynamoDB client = clientBuilder.build();
		return client;
	}

}
