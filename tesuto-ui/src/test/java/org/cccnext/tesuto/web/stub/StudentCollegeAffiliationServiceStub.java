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
package org.cccnext.tesuto.web.stub;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.web.service.StudentCollegeAffiliationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class StudentCollegeAffiliationServiceStub implements StudentCollegeAffiliationService {
    @Override
    public void createIfNotExists(String eppn, String studentCccId, String misCode, String authSource) {

    }

    @Override
    public void delete(String eppn, String studentCccId, String misCode) {

    }

    @Override
    public void delete(StudentCollegeAffiliationDto affiliationDto) {

    }

    @Override
    public List<StudentCollegeAffiliationDto> findAll() {
        return null;
    }

    @Override
    public StudentCollegeAffiliationDto find(String eppn, String studentCccId, String misCode) {
        return null;
    }

    @Override
    public List<StudentCollegeAffiliationDto> findTenMostRecent() {
        return null;
    }

    @Override
    public StudentCollegeAffiliationDto findByCccIdAndMisCode(String studentCccId, String misCode) {
        return null;
    }

    @Override
    public List<StudentCollegeAffiliationDto> findByStudentCccId(String studentCccId) {
        return null;
    }
}