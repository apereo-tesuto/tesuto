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

import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourseId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

//PLEASE NOTE DO NOT OVERRIDE INSERT, UPDATE, DELETE WHEN USING AUDIT TABLEs.  Auditing mechanism is dependent on events that
//are not generated when default repository events are overridden.
public interface DisciplineSequenceCourseRepository extends CrudRepository<DisciplineSequenceCourse, DisciplineSequenceCourseId> {

    @Query("select cdsc from DisciplineSequenceCourse cdsc where cdsc.disciplineId = ?1")
    Set<DisciplineSequenceCourse> findByDisciplineId(int disciplineId);

    @Query("select cdsc from DisciplineSequenceCourse cdsc join fetch cdsc.course where cdsc.disciplineId = ?1")
    Set<DisciplineSequenceCourse> findByDisciplineIdWithCourse(int disciplineId);

    @Query("select cdsc from DisciplineSequenceCourse cdsc join fetch cdsc.course where cdsc.disciplineId = ?1 and cdsc.cb21Code = ?2 and cdsc.courseGroup = ?3")
    Set<DisciplineSequenceCourse> findByDisciplineIdAndCB21AndCourseGroupWithCourse(int disciplineId, char cb21, int courseGroup);

    DisciplineSequenceCourse  findByCourseId(int courseId);
}
