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
package org.cccnext.tesuto.rules.web.controller;

import java.util.List;

import org.cccnext.tesuto.rules.service.RuleSetViewMapper;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/service/v1/ruleset")
public class RuleSetController {

    @Autowired
    RuleSetService service;

    @Autowired
    RuleSetViewMapper mapper;

    @RequestMapping(method = RequestMethod.POST, value = "save/{cccMisCode}")
    public ResponseEntity<String> create(@PathVariable("cccMisCode") String cccMisCode,
            @RequestBody List<String> ruleSetRowIds) {
        RuleSetDTO ruleset = new RuleSetDTO();
        ruleset.setEngine("tesuto");
        ruleset.setFamily(cccMisCode);
        ruleset.setRuleSetRowIds(ruleSetRowIds);
        return new ResponseEntity(service.save(ruleset).getId(), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(method = RequestMethod.POST, value = "search", produces = "application/json")
    public ResponseEntity<?> find(@RequestBody RuleAttributeFacetSearchForm form) {
        return new ResponseEntity(mapper.mapTo(service.find(form)), HttpStatus.ACCEPTED);
    }
    

    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(method = RequestMethod.GET, value = "miscode/{miscode}/competency/{competency}", produces = "application/json")
    public ResponseEntity<?> getRulesetByCollegeAndCompetency(@PathVariable("miscode")String miscode, @PathVariable("competency") String competencyMapDiscipline) {
    	RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
    	form.setFamily(miscode);
    	form.setCompetencyMapDiscipline(competencyMapDiscipline);
    	form.setStatus("published"); 
        return new ResponseEntity(mapper.mapTo(service.find(form)), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getRuleSets(@RequestParam(value="application", required=false) String application,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="cccMisCode", required=false) String cccMisCode) {
        List<RuleSetDTO> ruleSets = service.getRuleSetsByParameters(application, status, cccMisCode);
        return  new ResponseEntity(ruleSets, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{ruleSetId}", produces = "application/json")
    public ResponseEntity<RuleSetDTO> getRuleSet(@PathVariable("ruleSetId") String ruleSetId) {
      RuleSetDTO ruleSetDTO = service.getRuleSet(ruleSetId);
      ResponseEntity<RuleSetDTO> ruleSetDTOResponseEntity = new ResponseEntity(ruleSetDTO, HttpStatus.OK);
      return ruleSetDTOResponseEntity;
    }
}
