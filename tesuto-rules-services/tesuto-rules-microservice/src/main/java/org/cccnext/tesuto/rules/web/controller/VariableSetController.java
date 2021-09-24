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

import java.util.Map;

import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.qa.VariableSetQAService;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/service/v1/variable-set")
public class VariableSetController {

    @Autowired
    VariableSetQAService service;

    @PreAuthorize("hasAuthority('CREATE_TEST_VARIABLE_SET')")
    @RequestMapping(value = "cccid/{cccid}/college-miscode/{college-miscode}/source-type/{source-type}/row/{row}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<VariableSet> generateTestVariableSetFromRow(@PathVariable("cccid") String cccId, 
            @PathVariable(name="college-miscode", required=false) String cccMisCode,
            @PathVariable("source-type") String sourceType,
            @PathVariable("row")  Integer row) throws Exception {
      
        if(StringUtils.equalsIgnoreCase(sourceType,OperationalDataStoreService.SELF_REPORTED) ||StringUtils.equalsIgnoreCase(sourceType,"SELF_REPORTED")) {
            VariableSet variableSet = service.createSelfReportedVariableSet(cccId, cccMisCode, row);
            return new ResponseEntity(variableSet, HttpStatus.OK);
        } else if(StringUtils.equalsIgnoreCase(sourceType,OperationalDataStoreService.VERIFIED)) {
            VariableSet variableSet = service.createErpCalpassVariableSet(cccId, cccMisCode, row);
            return new ResponseEntity(variableSet, HttpStatus.OK);
        }
        
         return new ResponseEntity("Incorrect source-type", HttpStatus.NOT_ACCEPTABLE);
    }
    
    @PreAuthorize("hasAuthority('CREATE_TEST_VARIABLE_SET')")
    @RequestMapping( value = "map", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<VariableSet> generateTestVariableSetFromMap(@RequestBody Map<String,String> facts) throws Exception {
            return new ResponseEntity(service.createVariableSetFromMap(facts), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('CREATE_TEST_VARIABLE_SET')")
    @RequestMapping( method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<VariableSet> generateTestVariableSetFromVariableSet(@RequestBody VariableSet variableSet) throws Exception {
            return new ResponseEntity(service.createVariableSet(variableSet), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('VIEW_TEST_VARIABLE_SET')")
    @RequestMapping(value = "cccid/{cccid}/college-miscode/{college-miscode}/self-reported-opt-in/{self-reported-opt-in}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<VariableSet> getVariableSet(@PathVariable("cccid") String cccId,
            @PathVariable(name="college-miscode") String cccMisCode,
            @PathVariable("self-reported-opt-in") Boolean selfReportedOptIn) throws Exception {
         return new ResponseEntity(service.getExpectedVariableSet(cccId, cccMisCode, selfReportedOptIn), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('VIEW_TEST_VARIABLE_SET')")
    @RequestMapping(value = "cccid/{cccid}/college-miscode/{college-miscode}/self-reported-opt-in/{self-reported-opt-in}/expected-rules", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<VariableSet> getExpectedRulesVariableSet(@PathVariable("cccid") String cccId,
            @PathVariable(name="college-miscode") String cccMisCode,
            @PathVariable("self-reported-opt-in") Boolean selfReportedOptIn) throws Exception {
         return new ResponseEntity(service.getRulesVariableSet(cccId, cccMisCode, selfReportedOptIn), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('VIEW_VARIABLE_SET')")
    @RequestMapping(value = "{variable-set-id}/cccid/{cccid}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<VariableSet> getVariableSet(@PathVariable("variable-set-id") String variableSetId, @PathVariable("cccid") String cccId) throws Exception {
         return new ResponseEntity(service.getRulesVariableSet(cccId, variableSetId), HttpStatus.OK);
    }

}
