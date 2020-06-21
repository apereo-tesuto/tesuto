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

import org.cccnext.tesuto.placement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//PLEASE NOTE DO NOT OVERRIDE INSERT, UPDATE, DELETE WHEN USING AUDIT TABLEs.  Auditing mechanism is dependent on events that
//are not generated when default repository events are overridden.
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("select co from Course co left join  fetch co.disciplineSequenceCourses where co.courseId = ?1")
    Course findWithSequences(int courseId);

    @Query("select csdc.course from DisciplineSequenceCourse csdc where csdc.disciplineId = ?1 ")
    Set<Course> findByDisciplineId(int disciplineId);

    @Query("select course from DisciplineSequenceCourse csdc where csdc.disciplineId = ?1 and csdc.cb21Code=?2")
    Set<Course> findByDisciplineIdAndCB21(int disciplineId, char cb21);

    @Query("select d.collegeId from Discipline d where d.disciplineId in (select disciplineId from DisciplineSequenceCourse dsc where dsc.courseId = ?1)")
    Set<String> getCollegeIdsForCourse(int courseId);
    
    @Override
    @Query("select c from Course c where c.courseId = ?1")
    Course getOne(Integer id);

 }
