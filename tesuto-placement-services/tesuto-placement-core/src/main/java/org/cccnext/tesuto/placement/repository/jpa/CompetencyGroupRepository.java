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
package org.cccnext.tesuto.placement.repository.jpa;

import java.util.Set;

import org.cccnext.tesuto.placement.model.CompetencyGroup;
import org.cccnext.tesuto.placement.model.Course;
import org.springframework.data.repository.CrudRepository;

//PLEASE NOTE DO NOT OVERRIDE INSERT, UPDATE, DELETE WHEN USING AUDIT TABLEs.  Auditing mechanism is dependent on events that
//are not generated when default repository events are overridden.
public interface CompetencyGroupRepository  extends CrudRepository<CompetencyGroup,Integer> {

    Set<CompetencyGroup> findCompetencyGroupsByCourse(Course course);
    
}
