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
package org.cccnext.tesuto.web.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.rules.client.RuleSetRestClient;
import org.cccnext.tesuto.springboot.service.ConfigConfirmationService;
import org.cccnext.tesuto.springboot.service.RestClientConfirmationService;
import org.cccnext.tesuto.web.client.ConfigurationClient;
import org.cccnext.tesuto.web.controller.ui.StatusUIController;
import org.cccnext.tesuto.web.model.DashboardSession;
import org.ccctc.common.droolscommon.model.FamilyDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.LogType;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.PurgeQueueResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class DashboardService {


	@Value("${cloud.aws.env}")
	private String AWS_ENVIRONMENT;

	@Value("${rules.service.base.url}")
	private String RULES_SERVICE_BASE_URL;

	@Value("${REDIS_HOST_NAME}")
	private String redisHostName;

	@Value("${REDIS_PORT}")
	private Integer redisPort;

	private String awsLambdaFunctionName = "assess-%s-admin-dashboard";

	@Value("${cloud.aws.cloudwatch.url}")
	private String CLOUDWATCH_URL;

	@Value("${cloud.aws.cloudwatch.prefix}")
	private String CLOUDWATCH_PREFIX;

	@Autowired
	StudentCollegeAffiliationService studentCollegeAffiliationService;

	@Autowired
	RuleSetRestClient rulesClient;

	@Autowired
	ConfigConfirmationService configConfirmationService;

	@Autowired
	RestClientConfirmationService restClientConfirmationService;

	@Value("${uiIdleTimeoutDuration}")
	private String uiIdleTimeoutDuration;

	@Autowired
	List<ConfigurationClient> configurations;
	
	 @Autowired
	 @Qualifier("stringRedisTemplate")
	 RedisTemplate template; 

	@Value("${tesuto.active.services}")
	String activeServices;

	public DashboardSession getDashboardSession(Boolean isLocalProfile) {
		DashboardSession session = new DashboardSession();
		if (!isLocalProfile) {
			try {
				session.setMetrics(getMetricsFromLambda());
			} catch (Exception e) {
				log.error("An error occurred while attempting to get metrics for the system dashboard!", e);
				session.getErrors().add(
						"An exception occurred while processing the metrics lambda results. The lambda's response was not UTF-8 encoded.");
			}
		} else {
			session.setMetrics("");
		}

		session.setUiIdleTimeoutDuration(uiIdleTimeoutDuration);
		session.setRecentStudentLogins(getRecentStudentLogins());

		session.setCollegeRules(getCollegesFromRulesMicroservice());

		setConfigurations(session);

		session.setCloudwatchLogURL(CLOUDWATCH_URL + CLOUDWATCH_PREFIX);
		return session;
	}

	public void setConfigurations(DashboardSession session) {

		session.getMicroServiceProperties().put("uiProperties", getAssessProperties());
		session.getMicroServicePropertyKeyToNodeMap().put("uiProperties", "Assess Dashboard UI");
		try {
			List<RestClientTestResult> results = getRestClientStatus();
			session.getMicroServiceRestClientResults().put("uiProperties", results);
			session.getMicroServiceRestClientStatus().put("uiProperties", restClientSummary(results));
		} catch (Exception ex) {
			session.getErrors().add("Unable to obtain rest client results for uiProperties service may be down.");
			session.getMicroServiceRestClientResults().put("uiProperties", null);
			session.getMicroServiceRestClientStatus().put("uiProperties", "Unable to obtain rest client reuslts for uiProperties service may be down.");
		}

		for (ConfigurationClient configuration : configurations) {
			if (this.activeServices.contains(configuration.getMicroserviceName())) {
				try {
					session.getMicroServiceProperties().put(configuration.getMicroserviceName() + "Properties",
							configuration.getConfigurations());
					session.getMicroServicePropertyKeyToNodeMap().put(
							configuration.getMicroserviceName() + "Properties",
							configuration.getMicroserviceDisplayName());

				} catch (Exception ex) {
					session.getErrors().add("Unable to obtain configuration for " + configuration.getMicroserviceName()
							+ " service may be down.");
				}
				try {
					List<RestClientTestResult> results = configuration.getRestClientStatus();
					session.getMicroServiceRestClientResults().put(configuration.getMicroserviceName() + "Properties",
							results);
					session.getMicroServiceRestClientStatus().put(configuration.getMicroserviceName() + "Properties",
							restClientSummary(results));
				} catch (Exception ex) {
					log.error("Unable to obtain rest client results for " +  configuration.getMicroserviceName(), ex);
					session.getErrors().add("Unable to obtain rest client results for "
							+ configuration.getMicroserviceName() + " service may betestRetrieveObject down.");
					session.getMicroServiceRestClientResults().put(configuration.getMicroserviceName() + "Properties", null);
					session.getMicroServiceRestClientStatus().put(configuration.getMicroserviceName() + "Properties", "Unable to obtain rest client reuslts for uiProperties service may be down.");

				}
			}
		}
	}

	public String getMetricsFromLambda() throws UnsupportedEncodingException {
		final AWSLambda lambda = AWSLambdaClientBuilder.standard().build();

		/*
		 * This was the original, simpler way of invoking the lambda. Left in, for the
		 * time being, for historical purposes. DashboardLambdaService
		 * dashboardLambdaService =
		 * LambdaInvokerFactory.builder().lambdaClient(lambda).build(
		 * DashboardLambdaService.class);
		 * 
		 * return(dashboardLambdaService.invokeLambda());
		 */
		InvokeRequest request = new InvokeRequest()
				.withFunctionName(String.format(awsLambdaFunctionName, AWS_ENVIRONMENT))
				.withInvocationType(InvocationType.RequestResponse).withLogType(LogType.None); // use LogType.Tail to
																								// get some log info
																								// back
		String result = new String(lambda.invoke(request).getPayload().array(), "UTF-8");
		result = result.replace("\\", ""); // remove the \'s.
		if (result.length() >= 4) {
			result = result.substring(1, result.length() - 1); // remove the leading and trailing "'s
		}
		return result;
	}

	public List<StudentCollegeAffiliationDto> getRecentStudentLogins() {
		return studentCollegeAffiliationService.findTenMostRecent();
	}

	public List<CollegeViewDTO> getCollegesFromRulesMicroservice() {
		List<FamilyDTO> colleges = rulesClient.findColleges("active");
		if (colleges != null && !colleges.isEmpty())
			return rulesClient.findColleges("active").stream().map(c -> new CollegeViewDTO(c))
					.collect(Collectors.toList());
		return new ArrayList<>();
	}

	public List<String> getAssessProperties() {
		return Arrays.asList(configConfirmationService.logProperties().split("\n"));
	}

	public int purgeMessagesFromSQSQueue(String queueName) {
		final AmazonSQS sqs = AmazonSQSClientBuilder.standard().build();
		GetQueueUrlResult urlResult = sqs.getQueueUrl(queueName);
		String queueUrl = urlResult.getQueueUrl();
		PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest().withQueueUrl(queueUrl);
		PurgeQueueResult purgeQueueResult = sqs.purgeQueue(purgeQueueRequest);
		return purgeQueueResult.getSdkHttpMetadata().getHttpStatusCode();
	}

	public void purgeStudentSearchKeysFromRedis() {
		Set<String> keys = template.keys("*StudentService*");
		template.opsForHash().delete("*StudentService*", keys.toArray(new String[0]));
	}

	public String restClientSummary(List<RestClientTestResult> testResults) {
		StringBuilder clientSummaryResults = new StringBuilder();
		testResults.forEach(r -> clientSummaryResults.append(restClientSummary(r)));
		return clientSummaryResults.toString();
	}

	public String restClientSummary(RestClientTestResult testResult) {
		return  testResult.getHttpStatus() + "::" + testResult.getClassName() + ":" + testResult.getMethodName() + "-SEPARATOR-";
	}

	public List<RestClientTestResult> getRestClientStatus() {
		return restClientConfirmationService.validateEndpoints();
	}

}
