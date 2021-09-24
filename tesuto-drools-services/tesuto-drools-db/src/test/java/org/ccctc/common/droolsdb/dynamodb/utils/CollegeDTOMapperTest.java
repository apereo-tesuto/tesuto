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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.dynamodb.model.Application;
import org.ccctc.common.droolsdb.dynamodb.model.College;
import org.ccctc.common.droolsdb.dynamodb.utils.CollegeDTOMapper;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CollegeDTOMapperTest {

    private CollegeDTOMapper mapper = new CollegeDTOMapper();
    private FamilyDTO collegeDTO;
    
    @Before
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream ruleDTOIs = getClass().getResourceAsStream("/sampleCollege.json");
            collegeDTO = mapper.readValue(ruleDTOIs, FamilyDTO.class);
        } catch (JsonParseException e) {
            fail("Unable to parse Json:[" + e.getMessage() + "]");
        } catch (JsonMappingException e) {
            fail("Unable to map Json:[" + e.getMessage() + "]");
        } catch (IOException e) {
            fail("Unable to read Json from file:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void toCollege() {
        College college = mapper.toCollege(collegeDTO);
        assertEquals(collegeDTO.getFamilyCode(), college.getFamily());
        assertEquals(collegeDTO.getDescription(), college.getDescription());
        assertEquals(collegeDTO.getId(), college.getId());
        assertEquals(collegeDTO.getStatus(), college.getStatus());
        assertEquals(collegeDTO.getEngineDTOs().size(), college.getEngines().size());
        EngineDTO applicationDTO = collegeDTO.getEngineDTO("sns-listener");
        Application application = college.getEngine("sns-listener");
        assertEquals(applicationDTO.getName(), application.getName());
        assertEquals(applicationDTO.getDataSource(), application.getDataSource());
        assertEquals(applicationDTO.getGroupId(), application.getGroupId());
        assertEquals(applicationDTO.getArtifactId(), application.getArtifactId());
        assertEquals(applicationDTO.getVersion(), application.getVersion());
    }
    
    @Test
    public void toCollegeDTO() {
        College college = mapper.toCollege(collegeDTO);
        FamilyDTO rebuiltCollegeDTO = mapper.toCollegeDTO(college);
        
        assertEquals(collegeDTO.getFamilyCode(), rebuiltCollegeDTO.getFamilyCode());
        assertEquals(collegeDTO.getDescription(), rebuiltCollegeDTO.getDescription());
        assertEquals(collegeDTO.getId(), rebuiltCollegeDTO.getId());
        assertEquals(collegeDTO.getStatus(), rebuiltCollegeDTO.getStatus());
        assertEquals(collegeDTO.getEngineDTOs().size(), rebuiltCollegeDTO.getEngineDTOs().size());
        EngineDTO applicationDTO = collegeDTO.getEngineDTO("sns-listener");
        EngineDTO rebuildApplicationDTO = rebuiltCollegeDTO.getEngineDTO("sns-listener");
        assertEquals(applicationDTO.getName(), rebuildApplicationDTO.getName());
        assertEquals(applicationDTO.getDataSource(), rebuildApplicationDTO.getDataSource());
        assertEquals(applicationDTO.getGroupId(), rebuildApplicationDTO.getGroupId());
        assertEquals(applicationDTO.getArtifactId(), rebuildApplicationDTO.getArtifactId());
        assertEquals(applicationDTO.getVersion(), rebuildApplicationDTO.getVersion());
    }
    
}
