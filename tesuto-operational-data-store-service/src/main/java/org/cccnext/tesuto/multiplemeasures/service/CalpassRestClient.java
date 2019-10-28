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
package org.cccnext.tesuto.multiplemeasures.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.cccnext.tesuto.multiplemeasures.CalpassService;


import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class CalpassRestClient {

    private ObjectMapper mapper = new ObjectMapper();
    private String tokenUrl;

    private String resourceUrl;
    private String scope = "api";
    private String password;
    private String username ;
    private String grant_type = "password";
    private String client_id = "remote";
    private String currentToken = null;

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    int expiresIn = 3600;

    private Date tokenDate;

    public void setProperties(Map<String,String> properties) {
        setClient_id(CalpassService.getOrFail(properties,"calpas_client_id"));
        setGrant_type(CalpassService.getOrFail(properties,"calpas_grant_type"));
        setPassword(CalpassService.getOrFail(properties,"calpas_password"));
        setResourceUrl(CalpassService.getOrFail(properties,"calpas_resource_url"));
        setScope(CalpassService.getOrFail(properties,"calpas_scope"));
        setTokenUrl(CalpassService.getOrFail(properties,"calpas_token_url"));
        setUsername(CalpassService.getOrFail(properties,"calpas_username"));
    }

    public Map<String, Boolean> getCalpasData(String ssid, int attempts)
            throws URISyntaxException, ClientProtocolException, IOException, UnirestException {

        Map<String, String> map = mapper.readValue(getCalpasDataJson(ssid, attempts),
                new TypeReference<Map<String, String>>() {
                });
        Map<String, Boolean> calpasMap = new HashMap<>();
        map.keySet().forEach(key -> calpasMap.put(key, Boolean.parseBoolean(map.get(key))));
        return calpasMap;
    }

    public String getCalpasDataJson(String ssid, int attempts)
            throws URISyntaxException, ClientProtocolException, IOException, UnirestException {
        String token = obtainAccessToken();

        return executeUniRestGetRequest(ssid, token, attempts);
    }

    private String obtainAccessToken()
            throws ClientProtocolException, IOException, URISyntaxException, UnirestException {
        if (currentToken == null || new Date().getTime() - tokenDate.getTime() >= expiresIn) {
            String token = executeUniRestTokenRequest();
            if(StringUtils.isBlank(token)) {
                throw new RuntimeException("Unable to get RestToken for Calpas Endpoint");
            }
            Map<String, String> map = mapper.readValue(token,
                    new TypeReference<Map<String, String>>() {
                    });
            if (map.containsKey("expires_in")) {
                expiresIn = Integer.parseInt(map.get("expires_in"));
                tokenDate = new Date();
                currentToken = map.get("access_token");
            }
        }
      log.debug("Access Token obtained");
        return currentToken;
    }

    public String executeUniRestTokenRequest() throws UnirestException {
        List<NameValuePair> postParameters = new ArrayList<>();
        if (StringUtils.isNotBlank(scope))
            postParameters.add(new BasicNameValuePair("scope", scope));
        if (StringUtils.isNotBlank(password))
            postParameters.add(new BasicNameValuePair("password", password));
        if (StringUtils.isNotBlank(username))
            postParameters.add(new BasicNameValuePair("username", username));
        if (StringUtils.isNotBlank(grant_type))
            postParameters.add(new BasicNameValuePair("grant_type", grant_type));
        if (StringUtils.isNotBlank(client_id))
            postParameters.add(new BasicNameValuePair("client_id", client_id));
        StringBuilder bodyBuilder = new StringBuilder();
        postParameters.forEach(p -> bodyBuilder.append(p.getName()).append("=").append(p.getValue()).append("&"));
        String body = bodyBuilder.toString();
        int end = body.lastIndexOf("&");
        body = body.substring(0, end);
        HttpResponse<String> response = Unirest.post(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded").header("Cache-Control", "no-cache")
                .body(body).asString();
        if(response.getStatus() != 200) {
            throw new RuntimeException("Unable to obtain access token return message: " + response.getStatusText());
        }
        return response.getBody();
    }

    public String executeUniRestGetRequest(String ssid, String token, int attempts) throws UnirestException, ClientProtocolException, URISyntaxException, IOException {
        HttpResponse<String> response = Unirest.get(resourceUrl + ssid)
                .header("Authorization", "Bearer " + token).header("Content-type", "application/json")
                .header("Cache-Control", "no-cache").asString();
       if(response.getStatus() == 401) {
           if(attempts > 5) {
               throw new RuntimeException("Unable to authenticate to calpas");
           }
           currentToken = null;
           getCalpasData(ssid, attempts++);
       }
      log.debug("Request returned " + response.getStatus() + " " + response.getStatusText());
      log.debug("Response body " + response.getBody());
      return response.getBody();
    }

}
