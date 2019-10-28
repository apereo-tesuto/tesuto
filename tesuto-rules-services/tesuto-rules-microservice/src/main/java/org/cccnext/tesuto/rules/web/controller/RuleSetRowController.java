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

import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/service/v1/rulesetrow")
public class RuleSetRowController {
    
	@Autowired
	RuleSetRowService service;
    
    @RequestMapping(method = RequestMethod.POST, value = "insert-into/{ruleSetRowId}",produces = "application/text")
    public ResponseEntity<?> insertValues(@PathVariable("ruleSetRowId") String ruleSetRowId, @RequestBody  Map<String,String> variableValues) {
    	return new ResponseEntity(service.updateVariables(ruleSetRowId, variableValues), HttpStatus.ACCEPTED);
    }
}
