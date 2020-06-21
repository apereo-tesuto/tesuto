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

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.rules.validation.AssignedPlacementDrlValidationService;
import org.cccnext.tesuto.rules.validation.PlacementComponentDrlValidationService;
import org.cccnext.tesuto.rules.validation.PlacementDrlValidationService;
import org.ccctc.common.droolscommon.model.DrlDTO;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.services.DrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/service/v1/drl")
public class DrlController {

	@Autowired
	PlacementComponentDrlValidationService placementComponentDrlValidationService;

    @Autowired
    PlacementDrlValidationService placementDrlValidationService;

    @Autowired
    AssignedPlacementDrlValidationService assignedPlacementDrlValidationService;
    
    @Autowired
    private DrlService drlService;
	
    @RequestMapping(method = RequestMethod.POST, value = "validate/component", produces = "application/json")
    public ResponseEntity<DrlValidationResults> validateComponent(@RequestBody DrlValidationData data ) {
    	return new ResponseEntity(placementComponentDrlValidationService.validate(data), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "validate/placement", produces = "application/json")
    public ResponseEntity<DrlValidationResults> validatePlacement(@RequestBody DrlValidationData data ) {
        return new ResponseEntity(placementDrlValidationService.validate(data), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "validate/assignedplacement", produces = "application/json")
    public ResponseEntity<DrlValidationResults> validateASsignedPlacement(@RequestBody DrlValidationData data ) {
        return new ResponseEntity(assignedPlacementDrlValidationService.validate(data), HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value="/{ruleSetId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDrl(@PathVariable("ruleSetId") String ruleSetId) {
        List<String> drls = drlService.generateDrls(ruleSetId);
        DrlDTO drlDTO = new DrlDTO();
        if(CollectionUtils.isNotEmpty(drls)) {
            drlDTO.setDrls(drls.toArray(new String[0]));
        }
        ResponseEntity<Object> result = new ResponseEntity<Object>(drlDTO, null, HttpStatus.OK);
        return result;
    }
}
