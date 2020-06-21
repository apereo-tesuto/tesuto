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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.ccctc.common.commonidentity.domain.identity.CCCRestTemplate;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;

public class PlacementRequestRestClient {
    
    private ObjectMapper mapper = new ObjectMapper();
    private String resourceUrl = "https://localhost:8081/placement-service/service/v1/placement-request";
    private String urlSuffix= "/colleges/{college-miscode}/cccid/{cccid}?component-type=mmap";
    private  CCCRestTemplate restTemplate;
    

    public PlacementRequestRestClient(ServiceAccountManager accountManager) {
        restTemplate = new CCCRestTemplate();
        restTemplate.setJwtGetter(accountManager);
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void requestPlacement(String placementEventInputDto)
            throws  IOException {
        PlacementEventInputDto dto = mapper.readValue(placementEventInputDto, PlacementEventInputDto.class);
        String suffix = urlSuffix.replace( "{college-miscode}",     StringUtils.join(dto.getCollegeMisCodes(), ",")).replace("{cccid}", dto.getCccid());
        restTemplate.exchange(resourceUrl+ suffix, HttpMethod.PUT, null, Void.class);
    }

}
