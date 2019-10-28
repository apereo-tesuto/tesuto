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
package org.cccnext.tesuto.placement.service;

import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.util.CollectionUtils;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by bruce on 7/20/16.
 */
@Service("courseAssembler")
public class CourseAssembler extends AbstractAssembler<CourseViewDto, Course>{

	@Autowired
	Mapper mapper;

	@Autowired
	CompetencyGroupAssembler competencyGroupAssembler;

	@Override
	protected CourseViewDto doAssemble(Course course) {
		//The sequences need to be loaded so we can get the cb21
		//Assume exactly one cb21 -- view/data model mismatch
	    if(CollectionUtils.isNullOrEmpty(course.getDisciplineSequenceCourses())) {
	        return null;
	    }
		Iterator<DisciplineSequenceCourse> iterator = course.getDisciplineSequenceCourses().iterator();
		if (!iterator.hasNext()) {
			return null; //no view for you!
		} else {
			CourseViewDto view =  mapper.map(course, CourseViewDto.class);
			view.setCompetencyGroups(competencyGroupAssembler.assembleDto(course.getCompetencyGroups()));
			DisciplineSequenceCourse dsc = iterator.next();
			view.setDisciplineId(dsc.getDisciplineId());
			view.setCb21Code(dsc.getCb21Code());
			view.setCourseGroup(dsc.getCourseGroup());
			view.setAuditId(dsc.getAuditId());
			return view;
		}
	}

	@Override
	protected Course doDisassemble(CourseViewDto view) {
		if (view == null) {
			return null;
		}
		Course course = mapper.map(view, Course.class);
		course.setCompetencyGroups(competencyGroupAssembler.disassembleDto(view.getCompetencyGroups()));
		if (view.getCourseId() != null) {
			DisciplineSequenceCourse dsc = new DisciplineSequenceCourse();
			//Again, assuming just one DisciplineSequenceCourse object
			dsc.setCb21Code(view.getCb21Code());
			dsc.setCourseGroup(view.getCourseGroup());
			dsc.setDisciplineId(view.getDisciplineId());
			dsc.setCourse(course);
			dsc.setAuditId(view.getAuditId());
			course.setDisciplineSequenceCourses(Collections.singleton(dsc));
		}
		return course;
	}
}
