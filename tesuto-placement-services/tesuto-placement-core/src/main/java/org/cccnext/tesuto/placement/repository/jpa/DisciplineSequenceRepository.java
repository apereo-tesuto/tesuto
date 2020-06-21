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

import org.cccnext.tesuto.placement.model.DisciplineSequence;
import org.cccnext.tesuto.placement.model.DisciplineSequenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

//PLEASE NOTE DO NOT OVERRIDE INSERT, UPDATE, DELETE WHEN USING AUDIT TABLEs.  Auditing mechanism is dependent on events that
//are not generated when default repository events are overridden.
public interface DisciplineSequenceRepository extends JpaRepository<DisciplineSequence, DisciplineSequenceId> {

    @Query("select seq from DisciplineSequence seq left join fetch seq.disciplineSequenceCourses seq_co left join fetch seq_co.course where seq.disciplineId = ?1")
    public Set<DisciplineSequence> findByDisciplineIdWithCourse(int disciplineId);
    
    @Override
    @Query("select seq from DisciplineSequence seq left join fetch seq.disciplineSequenceCourses where seq.disciplineId = :#{#id.disciplineId} and seq.cb21Code = :#{#id.cb21Code} and seq.courseGroup = :#{#id.courseGroup}")
    public DisciplineSequence getOne(@Param("id") DisciplineSequenceId disciplineId);
}
