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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.content.service.CompetencyMapReader;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.placement.model.CB21;
import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.DisciplineSequence;
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.model.DisciplineSequenceId;
import org.cccnext.tesuto.placement.repository.jpa.CB21Repository;
import org.cccnext.tesuto.placement.repository.jpa.CourseRepository;
import org.cccnext.tesuto.placement.repository.jpa.DisciplineRepository;
import org.cccnext.tesuto.placement.repository.jpa.DisciplineSequenceCourseRepository;
import org.cccnext.tesuto.placement.repository.jpa.DisciplineSequenceRepository;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectAreaService {

	@Autowired
	private CB21Repository cb21Repository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private DisciplineRepository disciplineRepository;

	@Autowired
	private DisciplineSequenceRepository disciplineSequenceRepository;

	@Autowired
	private DisciplineSequenceCourseRepository disciplineSequenceCourseRepository;

	@Autowired
	CompetencyMapDisciplineReader competencyMapDisciplineReader;

	@Autowired
	CompetencyMapReader competencyMapReader;
	
	@Autowired
	CompetencyAttributesService attributesService;

	@Transactional
	public Discipline createDiscipline(Discipline discipline) {
		//TODO placementComponentAssess should always be set to fals as long as assessments are not in use
		if(discipline.getCompetencyAttributes().isPlacementComponentAssess())
			discipline.setCompetencyMapVersion(getCurrentCompetencyMapVersion(discipline));
		// TODO: Verify
		discipline.setDirty(true);
		CompetencyAttributes dto = attributesService.upsert(discipline.getCompetencyAttributes());
		discipline.setCompetencyAttributes(dto);
		Discipline newDiscipline = disciplineRepository.save(discipline);
		return newDiscipline;
	}

	@Transactional
	public DisciplineSequence getOrCreateSequence(int disciplineId, char cb21, int courseGroup) {
		if (!disciplineSequenceExists(disciplineId, cb21, courseGroup)) {
			DisciplineSequence newDisciplineSequence = new DisciplineSequence();
			newDisciplineSequence.setDiscipline(getDiscipline(disciplineId));
			newDisciplineSequence.setCb21(getCb21(cb21));
			newDisciplineSequence.setCourseGroup(courseGroup);
			newDisciplineSequence.setShowStudent(true);
			upsert(newDisciplineSequence);
		}
		DisciplineSequence ds = disciplineSequenceRepository.getOne(new DisciplineSequenceId(disciplineId, cb21, courseGroup));
		return ds;
	}

	@Transactional
	public Course createCourse(DisciplineSequence sequence, Course course) {
		Course newCourse = courseRepository.save(course);
		addSequenceCourseRelations(sequence, newCourse);
		return newCourse;
	}

	@Transactional
	public Set<DisciplineSequence> deleteCourse(int courseId) {
		Course course = getCourseWithSequences(courseId);
		Set<DisciplineSequence> disciplineSequences = course.getDisciplineSequences();
		for (DisciplineSequence disciplineSequence : disciplineSequences) {
			Discipline discipline = disciplineSequence.getDiscipline();
			discipline.setDirty(true);
			disciplineRepository.save(discipline);
		}
		courseRepository.delete(course);
		return disciplineSequences;
	}

	@Transactional
	public void deleteDiscipline(int disciplineId) {		
		disciplineRepository.deleteById(disciplineId);
	}

	@Transactional
	public boolean disciplineSequenceExists(int disciplineId, char cb21, int courseGroup) {
		if (!Character.isAlphabetic(cb21)) {
			//This check is handy if cb21 is not set in a request
			return false;
		}
		return disciplineSequenceRepository.existsById(new DisciplineSequenceId(disciplineId, cb21, courseGroup));
	}

	@Transactional
	public Course getCourse(int courseId) {
		return courseRepository.getOne(courseId);
	}

	@Transactional
	public Course getCourseWithSequences(int courseId) {
		return courseRepository.findWithSequences(courseId);
	}

	//used in unit tests
	@Transactional(readOnly = true)
	public Iterable<CB21> getAllCb21() {
		return cb21Repository.findAll();
	}

	@Transactional(readOnly = true)
	public CB21 getCb21(char cb21Code) {
		return cb21Repository.findById(cb21Code).get();
	}

	@Transactional(readOnly = true)
	public Set<Course> getCourses(int disciplineId) {
		return courseRepository.findByDisciplineId(disciplineId);
	}

	@Transactional(readOnly = true)
	public Set<Course> getCourses(int disciplineId, char cb21Code) {
		return courseRepository.findByDisciplineIdAndCB21(disciplineId, cb21Code);
	}

	@Transactional(readOnly = true)
	public Set<DisciplineSequenceCourse> getSequenceCoursesByDisciplineId(int disciplineId) {
		return disciplineSequenceCourseRepository.findByDisciplineIdWithCourse(disciplineId);
	}


	@Transactional(readOnly = true)
	public Set<DisciplineSequenceCourse> getSequenceCoursesByDisciplineIdAndCB21(int disciplineId, char cb21, int courseGroup) {
		return disciplineSequenceCourseRepository.findByDisciplineIdAndCB21AndCourseGroupWithCourse(disciplineId, cb21, courseGroup);
	}

	@Transactional(readOnly = true)
	public Discipline getDiscipline(int disciplineId) {
		return disciplineRepository.getOne(disciplineId);
	}

	//used in unit testing
	@Transactional(readOnly = true)
	public Discipline getDisciplineAndSequences(int disciplineId) {
		return disciplineRepository.findDisciplineWithSequences(disciplineId);
	}

	@Transactional(readOnly = true)
	public Set<Discipline> getDisciplines() {
		Set<Discipline> disciplines = new HashSet<>();
		for (Discipline discipline: disciplineRepository.findAll()) {
			disciplines.add(discipline);
		}
		return disciplines;
	}

	@Transactional(readOnly = true)
	public Set<Discipline> getDisciplinesByCollegeId(Collection<String> collegeIds) {
		return disciplineRepository.findDisciplinesByCollegeId(collegeIds);
	}

	@Transactional(readOnly = true)
	public Set<Discipline> getDisciplinesByCollegeIdAndDisciplineMap(String collegeId, String competencyMapDiscipline) {
		return disciplineRepository.findByCollegeIdAndCompetencyMapDiscipline(collegeId, competencyMapDiscipline);
	}

	@Transactional(readOnly = true)
	public Set<DisciplineSequence> getSequencesByDisciplineId(int disciplineId) {
		try {
			return disciplineSequenceRepository.findByDisciplineIdWithCourse(disciplineId);
		} catch(Exception exception) {
			return null;
		}
	}

	@Transactional
	public void updateDiscipline(Discipline discipline) {
		attributesService.upsert(discipline.getCompetencyAttributes());
		disciplineRepository.save(discipline);
	}

	@Transactional
	public void updateCourse(Course course, CourseViewDto update) {
		course.setCid(update.getCid());
		course.setCompetencyGroupLogic(update.getCompetencyGroupLogic());
		course.setDescription(update.getDescription());
		course.setName(update.getName());
		course.setNumber(update.getNumber());
		course.setSubject(update.getSubject());
		course.setMmapEquivalentCode(update.getMmapEquivalentCode());

		// we need to change the sequence <-> course relation ship, if the cb21 or courseGroup changed.
		// THE FOLLOWING IS GOING TO BE A REAL ISSUE. WILL NEED TO ADD DISPLINE ID TO CourseViewDTO if it because truly many to many.
		Optional<DisciplineSequenceCourse> newDsc = course.getDisciplineSequenceCourses().stream()
				.filter(c -> c.getCb21Code() == update.getCb21Code() && c.getCourseGroup() == update.getCourseGroup()).findFirst();
		// IN ADDITION TO THE PREVIOUS PROBLEM, WE NEED TO KNO WHICH SEQUENCE CHANGED, IF THERE IS A MANY TO MANY RELATIONSHIP
		DisciplineSequenceCourse existingDsc = course.getDisciplineSequenceCourses().iterator().next();
		if(!newDsc.isPresent() || !newDsc.get().equals(existingDsc)) {
			// delete the previously mapped DisciplineSequenceCourse, and disassociate with course and sequence
			removeSequenceCourseRelations(existingDsc, course);
			int disciplineId = existingDsc.getDisciplineId();
			// create new DisciplineSequenceCourse and associate with Course and DisciplineSequence
			DisciplineSequence newSequence = getOrCreateSequence( disciplineId, update.getCb21Code(), update.getCourseGroup());
			Discipline discipline = newSequence.getDiscipline();
			discipline.setDirty(true);
			disciplineRepository.save(discipline);
			addSequenceCourseRelations(newSequence, course);
		}
		courseRepository.save(course);
	}

	private void removeSequenceCourseRelations(DisciplineSequenceCourse dsc, Course course) {
		DisciplineSequence sequence = dsc.getDisciplineSequence();
		course.getDisciplineSequenceCourses().remove(dsc);
		sequence.getDisciplineSequenceCourses().remove(dsc);
		disciplineSequenceCourseRepository.delete(dsc);
		if (sequence.getDisciplineSequenceCourses().isEmpty()) {
			disciplineSequenceRepository.delete(sequence);
		}
	}

	@Transactional
	public void removeEmptySequence(int disciplineId, char cb21, int courseGroup) {
		Set<DisciplineSequenceCourse> dscSet = disciplineSequenceCourseRepository.findByDisciplineIdAndCB21AndCourseGroupWithCourse(disciplineId, cb21, courseGroup);
		if (dscSet != null && dscSet.isEmpty()) {
			disciplineSequenceRepository.deleteById(new DisciplineSequenceId(disciplineId, cb21, courseGroup));
		}
	}

	@Transactional
	public void updatePersistedCourse(Integer courseId, CourseViewDto update) {
		Course course = getCourseWithSequences(courseId);
		updateCourse(course, update);
	}

	@Transactional
	public DisciplineSequence upsert(DisciplineSequence sequence) {
		// Mark the dirty flag, for some reason the entity is disconnected here, so we're pulling the discipline separately.
		Discipline discipline = disciplineRepository.getOne(sequence.getDisciplineId());
		discipline.setDirty(true);
		disciplineRepository.save(discipline);

		return disciplineSequenceRepository.save(sequence);
	}



	@Transactional(readOnly = true)
	public Set<String> getCollegeIdsForCourse(int courseId) {
		return courseRepository.getCollegeIdsForCourse(courseId);
	}

	private Integer getCurrentCompetencyMapVersion(Discipline discipline) {
		CompetencyMapDisciplineDto competencyDiscipline = competencyMapDisciplineReader.read(discipline.getCompetencyMapDiscipline());
		if (competencyDiscipline == null) {
			throw new ValidationException(Collections.singleton("Cannot find Competency Map Discipline " + discipline.getCompetencyMapDiscipline()));
		}
		CompetencyMapDto map = competencyMapReader.readLatestPublishedVersion(competencyDiscipline.getDisciplineName());
		return map.getVersion();
	}

	private void addSequenceCourseRelations(DisciplineSequence sequence, Course course) {
		DisciplineSequenceCourse dsc = new DisciplineSequenceCourse();
		dsc.setDisciplineSequence(sequence);
		dsc.setCourse(course);
		// we need to save to get the audit ID loaded.
		DisciplineSequenceCourse savedDsc = disciplineSequenceCourseRepository.save(dsc);
		sequence.getDisciplineSequenceCourses().add(savedDsc);
		course.setDisciplineSequenceCourses(new HashSet<>());
		course.getDisciplineSequenceCourses().add(disciplineSequenceCourseRepository.save(savedDsc));
	}
}
