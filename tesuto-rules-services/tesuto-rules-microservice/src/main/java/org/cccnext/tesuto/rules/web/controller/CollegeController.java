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

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/service/v1/colleges", produces = MediaType.APPLICATION_JSON_VALUE)
public class CollegeController {
    
    @Autowired
    IFamilyService collegeService;

    @RequestMapping(method = RequestMethod.GET)
    public List<FamilyDTO> getColleges(@RequestParam(value="status", required=false) String status) {
        List<FamilyDTO> collegeDTOs = new ArrayList<FamilyDTO>();
        collegeDTOs = collegeService.getFamilies(status);
        return collegeDTOs;
    }

    @RequestMapping(value="{cccmiscode}", method = RequestMethod.GET)
    public FamilyDTO getCollegeByCccMisCode(@PathVariable("cccmiscode") String cccMisCode) {
    	FamilyDTO college = collegeService.getFamilyByFamilyCode(cccMisCode);
        return college;
    }
}
