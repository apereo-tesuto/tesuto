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
package org.cccnext.tesuto.importer.qti.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.importer.qti.web.service.CompetencyMapUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jasonbrown on 6/21/16.
 */
@Controller
@RequestMapping(value = "/service/v1/competency-map/upload")
public class CompetencyMapUploadController extends BaseController {


    private final String PATH = "/service/v1/competency-maps";
    private final String PATH_ORDER = "/service/v1/competency-map-order/competencies";
    
    @Value("${competency.map.retrieval.url}")
    String competencyRetrieveUrl;
    
    @Value("${competency.map.order.retrieval.url}")
    String competencyOrderRetrieveUrl;

    @Autowired
    CompetencyMapUploadService competencyMapUploadService;
    
    @Autowired
    @Qualifier("competencyMapOrderService")
    CompetencyMapOrderService competencyOrderService;

    @PreAuthorize("hasAuthority('CREATE_COMPETENCY_MAP')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded",
            "multipart/form-data" }, produces = "application/json")
    public @ResponseBody ImportResults createCompetencyMap(Model model, HttpServletRequest request,
            @ModelAttribute("standaloneRunCommand") @Valid StandaloneRunCommand standaloneRunCommand) throws Exception {
        List<CompetencyMapDto> competencyMapDtos = competencyMapUploadService.createCompetencyMapFromFile(standaloneRunCommand.getFile());
        Map<String, String> competencyMapOrders = new HashMap<String, String>();
        if(CollectionUtils.isNotEmpty(competencyMapDtos)) {
            competencyMapDtos.stream().forEach(cm -> competencyMapOrders.put(cm.getDiscipline(),
                    competencyOrderService.create(cm)));
        }
        
        ImportResults results = new ImportResults();
        results.setCompetencyMapOrderUrls(buildComptencyMapOrderUrls(competencyMapOrders,  request));
        results.setCompetencyMapUrls(getCompetencyMapUrls(competencyMapDtos, competencyRetrieveUrl));
        return results;
    }
    
    private  Map<String, String> buildComptencyMapOrderUrls(Map<String, String> competencyMapOrders, HttpServletRequest request) {
        Map<String, String> competencyMapOrderUrls = null;
        if(competencyMapOrders != null && competencyMapOrders.size() > 0) {
            for(String key:competencyMapOrders.keySet()) {
                if(StringUtils.isNotEmpty(competencyMapOrders.get(key))) { 
                   if(competencyMapOrderUrls == null) { 
                       competencyMapOrderUrls = new HashMap<String, String>();
                   }
                   competencyMapOrderUrls.put(key, competencyOrderRetrieveUrl+ "/" + competencyMapOrders.get(key));
                }
            }
        }
        return competencyMapOrderUrls;
    }

    protected String[] getCompetencyMapUrls(List<CompetencyMapDto> competencyMapDtos, String context) {
        String[] competencyMapUrls = new String[competencyMapDtos.size()];
        int i = 0;
        for (CompetencyMapDto competencyMapDto : competencyMapDtos) {
            competencyMapUrls[i++] = String.format("%s/%s", context, competencyMapDto.getDiscipline());
        }
        return competencyMapUrls;
    }
    
}
