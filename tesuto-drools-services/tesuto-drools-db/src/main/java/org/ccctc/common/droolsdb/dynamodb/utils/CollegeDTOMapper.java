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
package org.ccctc.common.droolsdb.dynamodb.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.dynamodb.model.Application;
import org.ccctc.common.droolsdb.dynamodb.model.College;

public class CollegeDTOMapper {

    public FamilyDTO toCollegeDTO(College college) {
        FamilyDTO collegeDTO = new FamilyDTO()
                .setId(college.getId())
                .setFamilyCode(college.getFamily())
                .setDescription(college.getDescription())
                .setStatus(college.getStatus())
                .setEngines(toApplicationDTOMap(college.getEngines()));
        return collegeDTO;
    }
    
    public College toCollege(FamilyDTO collegeDTO) {
        College college = new College();
        college.setStatus(collegeDTO.getStatus());
        college.setId(collegeDTO.getId());
        college.setDescription(collegeDTO.getDescription());
        college.setFamily(collegeDTO.getFamilyCode());
        college.setEngines(toApplicationMap(collegeDTO.getEngineDTOs()));
        return college;
    }
    
    public List<FamilyDTO> toCollegeDTOList(List<College> colleges) {
        List<FamilyDTO> collegeDTOs = new ArrayList<FamilyDTO>();
        if (colleges != null) {
            for (College college : colleges) {
                FamilyDTO collegeDTO = toCollegeDTO(college);
                collegeDTOs.add(collegeDTO);
            }            
        }
        return collegeDTOs;
    }
    
    public Map<String, Application> toApplicationMap(Map<String, EngineDTO> applicationDTOs) {
        Map<String, Application> applications = new HashMap<String, Application>();
        for (Entry<String, EngineDTO> entry : applicationDTOs.entrySet()) {
            Application application = toApplication(entry.getValue());
            applications.put(entry.getKey(), application);
        }
        return applications;
        
    }
    
    public Map<String, EngineDTO> toApplicationDTOMap(Map<String, Application> applications) {
        Map<String, EngineDTO> applicationDTOs = new HashMap<String, EngineDTO>();
        if (applications != null && applications.size() > 0) {
            for (Entry<String, Application> entry : applications.entrySet()) {
                EngineDTO applicationDTO = toApplicationDTO(entry.getValue());
                applicationDTOs.put(entry.getKey(), applicationDTO);
            }
        }
        return applicationDTOs;
    }
    
    public EngineDTO toApplicationDTO(Application application) {
        EngineDTO applicationDTO = new EngineDTO()
                .setDataSource(application.getDataSource())
                .setName(application.getName())
                .setArtifactId(application.getArtifactId())
                .setGroupId(application.getGroupId())
                .setVersion(application.getVersion());
        return applicationDTO;
    }
    
    public Application toApplication(EngineDTO applicationDTO) {
        Application application = new Application();
        application.setName(applicationDTO.getName());
        application.setDataSource(applicationDTO.getDataSource());
        application.setGroupId(applicationDTO.getGroupId());
        application.setArtifactId(applicationDTO.getArtifactId());
        application.setVersion(applicationDTO.getVersion());
        return application;
    }
}
