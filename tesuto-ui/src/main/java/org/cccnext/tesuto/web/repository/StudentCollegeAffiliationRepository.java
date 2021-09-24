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
package org.cccnext.tesuto.web.repository;

import java.util.List;

import org.cccnext.tesuto.web.model.StudentCollegeAffiliation;
import org.cccnext.tesuto.web.model.StudentCollegeAffiliationId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface StudentCollegeAffiliationRepository extends CrudRepository<StudentCollegeAffiliation, StudentCollegeAffiliationId> {

	List<StudentCollegeAffiliation> findByStudentCccIdAndMisCode(String studentCccId, String misCode);

	public List<StudentCollegeAffiliation> findByStudentCccId(String studentCccId);

	@Query("select sca from StudentCollegeAffiliation sca order by sca.loggedDate desc")
	List<StudentCollegeAffiliation> findMostRecentLogins(Pageable page);
}
