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

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/colleges", produces = MediaType.APPLICATION_JSON_VALUE)
public class FamilyController {
    
    @Autowired
    IFamilyService familyService;
    
    @RequestMapping(method = RequestMethod.GET)
    public List<FamilyDTO> getFamilies(@RequestParam(value="status", required=false) String status) {
        List<FamilyDTO> familyDTOs = new ArrayList<FamilyDTO>();
        familyDTOs = familyService.getFamilies(status);
        return familyDTOs;
    }
    
    public FamilyDTO getFamilyByCccMisCode(String cccMisCode) {
        return  familyService.getFamilyByFamilyCode(cccMisCode);
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public FamilyDTO getFamily(@PathVariable("id") String id) {
        FamilyDTO family = familyService.getFamilyById(id);
        return family;
    }

    @RequestMapping(method = RequestMethod.POST)
    public FamilyDTO addFamily(@RequestBody FamilyDTO familyDTO) {
        FamilyDTO updatedFamily = familyService.save(familyDTO);
        return updatedFamily;
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void deleteFamily(@PathVariable("id") String id) {
        FamilyDTO familyDTO = familyService.getFamilyById(id);
        familyDTO.setStatus("inactive");
        familyService.save(familyDTO);
    }
}
