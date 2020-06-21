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
package org.cccnext.tesuto.multiplemeasures;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.multiplemeasures.service.CalPassVariableSetParserServiceImpl;
import org.cccnext.tesuto.multiplemeasures.service.CalpassRestClient;
import org.cccnext.tesuto.multiplemeasures.service.PlacementRequestPublisher;
import org.cccnext.tesuto.multiplemeasures.service.PlacementRequestRestClient;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.service.multiplemeasures.IntersegmentKeyServiceImpl;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreServiceImpl;
import org.cccnext.tesuto.service.multiplemeasures.VariableSetParserService;



import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class CalpassService {

  private OperationalDataStoreService ods;
  private CalpassRestClient restClient;
  private PlacementRequestPublisher publisher;
  private IntersegmentKeyServiceImpl keyService = new IntersegmentKeyServiceImpl();
  private VariableSetParserService vsps = new CalPassVariableSetParserServiceImpl();
  private ObjectMapper mapper = new ObjectMapper();
  private TypeReference<HashMap<String, String>> mapTypeRef = new TypeReference<HashMap<String, String>>() {};

  //properties to be filled in from propertySource
  private String awsEndpoint;
  private String awsRegion;
  private String publisherEndpoint;
  private String requestQueueUrl;
  private String openIdClientId;
  private String openIdSecret;
  private String openIdBaseEndpoint;
  private String placementRequestServiceUrl;

  static public String getOrFail(Map<String,String> properties, String property) {
    String value = properties.get(property);
    if (value == null) {
      throw new RuntimeException(property + " not found");
    }
    return value;
  }

  public CalpassService(String propertySource) throws Exception {
    Map<String,String> properties = initProperties(propertySource);
    restClient = restClient(properties);
    publisher = placementRequestPublisher();
  }

  public void setOperationalDataStoreService(OperationalDataStoreService ods) {
    this.ods = ods;
  }

  public void setRequestQueueUrl(String requestQueueUrl) {
    this.requestQueueUrl = requestQueueUrl;
    if (publisher != null) {
      publisher.setQueueUrl(requestQueueUrl);
    }
  }

  private Map<String,String> initProperties(String propertySource) throws Exception {
        String[] sources = propertySource.split(":");
        if (sources.length != 2) {
            throw new RuntimeException("Invalid format for calpass.service.property.source");
        }
        String sourceType = sources[0];
        String sourcePath = sources[1];
        Map<String,String> properties = null;
        if ("ssm".equals(sourceType)) {
            properties = loadPropertiesFromSsm(sourcePath);
        } else if ("classpath".equals(sourceType)) {
            properties = loadPropertiesFromFile(sourcePath);
        } else {
            throw new RuntimeException("calpass.service.property.source must start with ssm: or classpath:");
        }
        awsEndpoint = getOrFail(properties,"aws.endpoint");
        awsRegion = getOrFail(properties,"cloud.aws.region.static");
        publisherEndpoint = getOrFail(properties,"aws.messages.endpoint");
        openIdClientId = getOrFail(properties,"openid.client.id");
        openIdSecret = getOrFail(properties,"openid.client.secret");
        openIdBaseEndpoint = getOrFail(properties,"openid.base.endpoint");
        placementRequestServiceUrl = getOrFail(properties,"placement.request.service.url");
        return properties;
    }

    private Map<String,String> loadPropertiesFromFile(String fileName) throws Exception {
      try (InputStream is = getClass().getResourceAsStream(fileName)) {
        return mapper.readValue(is, mapTypeRef);
      }
    }

    private Map<String,String> loadPropertiesFromSsm(String ssmPropertiesName) throws Exception {
        GetParameterRequest request = new GetParameterRequest().withName(ssmPropertiesName).withWithDecryption(true);
        AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        GetParameterResult result = client.getParameter(request);
        return mapper.readValue(result.getParameter().getValue(), mapTypeRef);
    }

    private AmazonDynamoDB dynamoDBClient() {
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        clientBuilder.withCredentials(new DefaultAWSCredentialsProviderChain());
        if (!"defaultClient".equals(awsEndpoint)) {
            clientBuilder.withEndpointConfiguration(
              new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion));
            AmazonDynamoDB client = clientBuilder.build();
            return client;
        }
        return AmazonDynamoDBClientBuilder.defaultClient();
    }


    private CalpassRestClient restClient(Map<String,String> properties) {
        restClient = new CalpassRestClient();
        restClient.setProperties(properties);
        return restClient;
    }


    private PlacementRequestPublisher  placementRequestPublisher() {
        PlacementRequestPublisher publisher = new PlacementRequestPublisher();
        publisher.setEndpoint(publisherEndpoint);
        publisher.setQueueUrl(requestQueueUrl);
        publisher.setRegionName(awsRegion);
        publisher.setSendClass(PlacementEventInputDto.class);
        publisher.setRestClient(null);
        publisher.setRestClient(placementRequestRestClient());
        return publisher;
    }

    //used in unit tests
    public PlacementRequestRestClient placementRequestRestClient() {
        ServiceAccountManager serviceAccountManager = new ServiceAccountManager();
        serviceAccountManager.setClientId(openIdClientId);
        serviceAccountManager.setClientSecret(openIdSecret);
        serviceAccountManager.setBaseEndpoint(openIdBaseEndpoint);
        PlacementRequestRestClient restClient = new PlacementRequestRestClient(serviceAccountManager);
        restClient.setResourceUrl(placementRequestServiceUrl);
        return restClient;
    }


    public void handleRequest(String misCode, String cccId, String ssId) throws Exception {
      VariableSet calPasVariableSet = createVariableSetFromCalpas(misCode, cccId, ssId);
      saveVariableSet(cccId, ssId, calPasVariableSet);
      sendPlacementNotification(cccId, misCode);
    }

    private VariableSet createVariableSetFromCalpas(String misCode, String cccId, String ssId) throws Exception {
        String calPasJson = restClient.getCalpasDataJson(ssId, 0);
        if (StringUtils.isBlank(calPasJson)) {
            return null;
        }
        return vsps.parseJsonToVariableSet(misCode, cccId, calPasJson);

    }

    private Student createStudent(String cccId, String ssId) {
        Student student = new Student();
        student.setCccId(cccId);
        student.setSsId(ssId);
        return student;
    }

    private void sendPlacementNotification(String cccid, String miscode) {
        PlacementEventInputDto dto = new PlacementEventInputDto();
        dto.setCccid(cccid);
        dto.setCollegeMisCodes(new HashSet<>(Arrays.asList(miscode)));
        dto.setTrackingId(UUID.randomUUID().toString());
        dto.setProcessOnlyNewPlacements(false);
        publisher.sendMessage(dto);
    }


    private void saveVariableSet(String cccId, String ssId, VariableSet variableSet) {
        if(variableSet == null ) {
            return;
        }
        if (ods.fetchStudent(cccId) == null) {
            Student student = createStudent(cccId, ssId);
            student.setVariableSets(Collections.singleton((variableSet)));
            ods.createStudent(student);
        }
        ods.addVariableSet(cccId, variableSet);
    }
}
