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
package org.ccctc.common.droolsengine.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@RestController
@RequestMapping(value = DroolsAdminFactsRestController.REQUEST_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
public class DroolsAdminFactsRestController {
    static final String REQUEST_ROOT = "/ccc/api/drools/v1/facts";
    
    
// TODO - this needs to be rewritten to handle multiple engines within the stack, currently,
// it assumes a default
//    @Autowired
//    private DroolsRulesService droolsRuleService;
    
//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<List<ActionResult>> loadFact(@RequestBody Map<String, String> map) {
//        HttpStatus status = HttpStatus.ACCEPTED;
//        Map<String, Object> facts = new HashMap<String, Object>();
//        map.keySet().forEach(key -> facts.put(key, map.get(key)));
//        try{
//            List<ActionResult> results = droolsRuleService.execute(facts);
//            return new ResponseEntity(results, status); 
//        } catch (Exception exception) {
//            ActionResult result = new ActionResult("Unhandled exception when execution rsults", false);
//            return new ResponseEntity(Arrays.asList(result), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
