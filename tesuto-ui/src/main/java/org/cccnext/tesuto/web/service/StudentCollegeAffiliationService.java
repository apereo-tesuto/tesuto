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
package org.cccnext.tesuto.web.service;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface StudentCollegeAffiliationService extends StudentCollegeAffiliationReader {

    void createIfNotExists(String eppn, String studentCccId, String misCode, String authSource);

    void delete(String eppn, String studentCccId, String misCode);

    void delete(StudentCollegeAffiliationDto affiliationDto);

    List<StudentCollegeAffiliationDto> findAll();

    StudentCollegeAffiliationDto find(String eppn, String studentCccId, String misCode);

}
