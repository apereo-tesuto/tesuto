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

import org.ccctc.common.commonidentity.domain.identity.CCCRestTemplate;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;
import org.ccctc.common.commonidentity.openidc.CryptoBearerFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OauthConfig {

    @Value("${openid.base.endpoint}") 
    String baseEndpoint;
    
    @Value("${openid.jwk.endpoint}") 
    String jwkEndpoint;
    
    @Value("${openid.client.id}") 
    String clientId;
    
    @Value("${openid.client.secret}")
    String clientSecret;
    
    @Bean(name="oAuthClientRestTemplate")
    RestTemplate oAuthClientRestTemplate() {
        CCCRestTemplate template = new CCCRestTemplate(securityAccountManager());
        return template;
    }

    CryptoBearerFilter getCryptoBearerFilter() {
        CryptoBearerFilter bearer = new CryptoBearerFilter(jwkEndpoint);
        return bearer;
    }
    
    ServiceAccountManager securityAccountManager() {
        ServiceAccountManager.ServiceAccountManagerBuilder builder = new ServiceAccountManager.ServiceAccountManagerBuilder();
        return builder.baseEndpoint(baseEndpoint).clientId(clientId).clientSecret(clientSecret).scope("openid").build();
    }
    
}
