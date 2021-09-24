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
package org.ccctc.droolseditor.controllers;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.ccctc.common.droolscommon.model.DrlDTO;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.droolseditor.validation.ValidationDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drl")
public class DrlController {

    @Autowired
    private DrlService service;
    
    @Autowired
    private ValidationDistributor  validator;
    
    @RequestMapping(value="/{ruleSetId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDrl(@PathVariable("ruleSetId") String ruleSetId) {
        List<String> drls = service.generateDrls(ruleSetId);
        DrlDTO drlDTO = new DrlDTO();
        if(CollectionUtils.isNotEmpty(drls)) {
            drlDTO.setDrls(drls.toArray(new String[0]));
        }
        ResponseEntity<Object> result = new ResponseEntity<Object>(drlDTO, null, HttpStatus.OK);
        return result;
    }
    
    @RequestMapping(value="/{ruleSetId}/validate", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void validate(@PathVariable("ruleSetId") String ruleSetId) {
        StringBuffer drl = service.generateDrl(ruleSetId);
        validator.validate(drl.toString());
    }
}
