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
package org.ccctc.common.droolsdb.dynamodb.services;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.dynamodb.dao.CollegeDAO;
import org.ccctc.common.droolsdb.dynamodb.model.College;
import org.ccctc.common.droolsdb.dynamodb.utils.CollegeDTOMapper;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class FamilyServiceImpl implements IFamilyService, InitializingBean {
    @Autowired
    private CollegeDAO collegeDAO;
    
    @Autowired
    private DynamoDBTableService tableService;
    
    @Override
	public void afterPropertiesSet() throws Exception {
    	tableService.createTable(College.TABLE_NAME, College.class);
    }

    private CollegeDTOMapper mapper = new CollegeDTOMapper();
    
    @Override
    public List<FamilyDTO> getFamilies() {
        List<College> colleges = (List<College>) collegeDAO.findAll();
        
        List<FamilyDTO> collegeDTOs = mapper.toCollegeDTOList(colleges);
        return collegeDTOs;
    }

    @Override
    public List<FamilyDTO> getFamilies(String status) {
        if(StringUtils.isBlank(status)) {
            return getFamilies();
        }
        List<College> colleges = (List<College>) collegeDAO.findByStatus(status);
        List<FamilyDTO> familyDTOs = mapper.toCollegeDTOList(colleges);
        return familyDTOs;
    }

    @Override
    public FamilyDTO getFamilyById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ObjectNotFoundException("Cannot retrieve Family by id if id is blank");
        }
        College family = null;
        try {
            family = collegeDAO.findById(id).get();
        }
        catch (NoSuchElementException e) {
            throw new ObjectNotFoundException("Cannot retrieve Family[" + id + "], id not found");
        }
        if (family == null) {
            throw new ObjectNotFoundException("Cannot retrieve Family[" + id + "], id not found");
        }
        FamilyDTO familyDTO = mapper.toCollegeDTO(family);
        return familyDTO;
    }

    @Override
    public FamilyDTO getFamilyByFamilyCode(String familyCode) {
        if (StringUtils.isBlank(familyCode)) {
            throw new ObjectNotFoundException("Cannot retrieve Family by family code if code is blank");
        }
        College college = (College) collegeDAO.findByFamily(familyCode);
        if (college == null) {
            throw new ObjectNotFoundException("Cannot retrieve Family[" + familyCode + "], code not found");
        }
        FamilyDTO familyDTO = mapper.toCollegeDTO(college);
        return familyDTO;
    }

    @Override
    public FamilyDTO save(FamilyDTO familyDTO) {
        if (familyDTO.getId() == null) {
            familyDTO.setId(UUID.randomUUID().toString());
        }
        
        College college = mapper.toCollege(familyDTO);
        collegeDAO.save(college);
        return familyDTO;
    }
    
    @Override
    public void delete(FamilyDTO familyDTO) {
        collegeDAO.deleteById(familyDTO.getId());
    }

    @Override
    public void updateTimestamp(String familyCode) {
        FamilyDTO familyDTO = getFamilyByFamilyCode(familyCode);
        familyDTO.setEdited(new Date());
        save(familyDTO);
    }}
