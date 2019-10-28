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
import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.SequenceInfoViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.placement.view.student.CourseStudentViewDto;
import org.cccnext.tesuto.placement.view.student.PlacementStudentViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlacementStudentViewAssembler extends AbstractAssembler<PlacementStudentViewDto, Placement> {

	@Autowired
	Mapper mapper;

	@Autowired
	SubjectAreaServiceAdapter adapter;

	@Override
	protected PlacementStudentViewDto doAssemble(Placement entity) {
		PlacementStudentViewDto viewDto = mapper.map(entity, PlacementStudentViewDto.class);
		VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = adapter.getVersionedSubjectAreaDto(entity.getDisciplineId(), entity.getSubjectAreaVersion());
		List<DisciplineSequenceViewDto> disciplineSequences = versionedSubjectAreaViewDto.getDisciplineSequences().stream()
				.filter(dp -> dp.getCb21Code() == entity.getCb21Code())
				.filter(dp -> dp.getCourseGroup() == entity.getCourseGroup()).collect(Collectors.toList());
		if (disciplineSequences.size() != 1) {
			throw new org.cccnext.tesuto.exception.ValidationException("Unexpected number of sequence levels. Expected 1, Found " + disciplineSequences.size()
					+ " for College:" + versionedSubjectAreaViewDto.getCollegeId()
					+ ", Versioned Subject Area:" + versionedSubjectAreaViewDto.getTitle()
					+ ", Versioned Subject Area ID: " + versionedSubjectAreaViewDto.getDisciplineId()
					+ ", CB21: " + entity.getCb21Code().toString()
					+ ", courseGroup: " + entity.getCourseGroup().toString());
		}
		DisciplineSequenceViewDto disciplineSequence = disciplineSequences.get(0);
		SequenceInfoViewDto sequenceInfoViewDto = mapper.map(disciplineSequence, SequenceInfoViewDto.class);
		viewDto.setSequenceInfo(sequenceInfoViewDto);
		if (viewDto.getSequenceInfo().getShowStudent()) {
			Set<CourseViewDto> courseViewDtos = adapter.getCoursesFromVersionedSubjectArea(versionedSubjectAreaViewDto, entity.getCb21Code(), entity.getCourseGroup());
			viewDto.setCourses(courseViewDtos.stream().map(course -> mapper.map(course, CourseStudentViewDto.class)).collect(Collectors.toSet()));
		}
		return viewDto;
	}

	@Override
	protected Placement doDisassemble(PlacementStudentViewDto dto) {
		throw new UnsupportedOperationException("We should not be disassembling these.");
	}

}
