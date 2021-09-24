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
package org.cccnext.tesuto.admin.repository;

import org.cccnext.tesuto.admin.model.TestLocationAssessment;
import org.cccnext.tesuto.admin.model.TestLocationAssessmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface TestLocationAssessmentRepository extends JpaRepository<TestLocationAssessment, TestLocationAssessmentId> {
    @Query("from TestLocationAssessment tla where tla.testLocationId = ?1")
    Set<TestLocationAssessment> findAllByTestLocationId(String testLocationId);
}
