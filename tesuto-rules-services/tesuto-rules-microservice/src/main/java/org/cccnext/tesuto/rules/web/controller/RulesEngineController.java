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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.rules.reader.EngineReaderFactoryResource;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.action.result.ErrorActionResult;
import org.ccctc.common.droolsengine.engine.service.IDroolsRulesService;
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
@RequestMapping(value = "/service/v1/rules-engine")
public class RulesEngineController {
	
  
  @Autowired
  private EngineReaderFactoryResource factory;
  
  @PreAuthorize("hasAuthority('API')")
  @RequestMapping(value="oauth2/{engine-id}",method = RequestMethod.POST)
  public ResponseEntity<List<ActionResult>> loadFact(@PathVariable("engine-id")String engineId,@RequestBody Map<String, String> map) {
	  IDroolsRulesService droolsRuleService;
      HttpStatus status = HttpStatus.ACCEPTED;
      droolsRuleService = factory.getDroolsRulesServiceFactory().getDroolsRulesService(engineId);
      Map<String, Object> facts = new HashMap<String, Object>();
      map.keySet().forEach(key -> facts.put(key, map.get(key)));
      try{
          List<ActionResult> results = droolsRuleService.execute(facts);
          return new ResponseEntity<>(results, status); 
      } catch (Exception exception) {
          ActionResult result = new ErrorActionResult(exception, "ERROR_ACTION", "Unhandled exception when execution results");
          return new ResponseEntity<>(Arrays.asList(result), HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

}
