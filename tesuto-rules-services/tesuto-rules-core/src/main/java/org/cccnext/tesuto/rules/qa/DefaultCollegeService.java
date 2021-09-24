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
package org.cccnext.tesuto.rules.qa;

import java.io.IOException;

import org.cccnext.tesuto.qa.QAService;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DefaultCollegeService implements QAService<FamilyDTO> {  

    @Autowired
    private IFamilyService collegeService;

    @Override
    public void setDefaults() throws IOException {
        for (FamilyDTO college : this.getResources(FamilyDTO.class)) {
            try {
                collegeService.getFamilyById(college.getId());
            } catch (ObjectNotFoundException exp) {
                collegeService.save(college);
            }
        }
    }

    public FamilyDTO addCollege(String familyCode, String name) {
        try {
            return collegeService.getFamilyByFamilyCode(familyCode);
        } catch (ObjectNotFoundException exp) {
            FamilyDTO college = new FamilyDTO();
            college.setFamilyCode(familyCode);
            college.setDescription(name);
            college.setStatus(StandardStatus.ACTIVE);
            return collegeService.save(college);
        }
    }
    
    public void addApplication(String id, EngineDTO application) {
        FamilyDTO college = collegeService.getFamilyByFamilyCode(id);
        if(!college.getEngineDTOs().containsKey(application.getName())) {
            college.getEngineDTOs().put(application.getName(), application);
            collegeService.save(college);
        }
    }

    @Override
    public String getDirectoryPath() {
        return "classpath:defaults/college";
    }
}
