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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.DisciplineSequence;
import org.cccnext.tesuto.placement.model.VersionedSubjectArea;
import org.cccnext.tesuto.placement.repository.jpa.VersionedSubjectAreaRepository;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.placement.view.student.VersionedSubjectAreaStudentViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bruce on 7/20/16.
 */
@Service("subjectAreaServiceAdapter")
public class SubjectAreaServiceAdapter {

	@Autowired
	SubjectAreaService subjectAreaService;

	@Autowired
	DisciplineAssembler disciplineAssembler;

	@Autowired
	VersionedSubjectAreaAssembler versionedSubjectAreaAssembler;

  @Autowired
  CompetencyAttributesAssembler attributesAssembler;

	@Autowired
	DisciplineSequenceAssembler sequenceAssembler;

	@Autowired
	DisciplineSequenceCourseAssembler sequenceCourseAssembler;

	@Autowired
	CourseAssembler courseAssembler;

	@Autowired
	CB21Assembler cb21Assembler;

  @Autowired
  VersionedSubjectAreaRepository versionedSubjectAreaRepository;

  @Autowired
	VersionedSubjectAreaStudentViewAssembler versionedSubjectAreaStudentViewAssembler;

	@Autowired
	Mapper mapper;

	@Transactional
	public Integer createDiscipline(DisciplineViewDto view) {
		Discipline discipline = disciplineAssembler.disassembleDto(view);
		Discipline newDiscipline = subjectAreaService.createDiscipline(discipline);
		return newDiscipline.getDisciplineId();
	}

	@Transactional(readOnly=true)
	public boolean disciplineSequenceExists(int disciplineId, char cb21, int courseGroup) {
		return subjectAreaService.disciplineSequenceExists(disciplineId, cb21, courseGroup);
	}

	public void validateDiscipline(DisciplineViewDto discipline) {
		Set<String> errorMessages = new HashSet<String>();
		if (StringUtils.isBlank(discipline.getTitle())) {
			errorMessages.add("Title is missing");
		}
		if (discipline.getTitle().length() > 120) {
			errorMessages.add("Title is too long");
		}
		if (discipline.getCompetencyMapDiscipline() != null && discipline.getCompetencyMapDiscipline().length() > 256) {
			errorMessages.add("competency map discipline must be less than 256 chars");
		}
		if (discipline.getSisCode() != null && discipline.getSisCode().length() > 120) {
			errorMessages.add("sisCode must be less than 100 chars");
		}
		if (discipline.getCompetencyMapDiscipline() == null) {
			errorMessages.add("competencyMapDiscipline is required");
		}

		if (errorMessages.size() > 0) {
			throw new ValidationException(errorMessages);
		}
	}

	@Transactional
	public Integer createCourse(int disciplineId, CourseViewDto view) {
		Course course = courseAssembler.disassembleDto(view);

		//The disciplineSequenceCourse objects get created by the assembler, but they represent things not yet in the database
		//The placementService will create them from disciplineId, cb21 and courseGroup
		course.setDisciplineSequenceCourses(null);
		DisciplineSequence sequence = subjectAreaService.getOrCreateSequence(disciplineId, view.getCb21Code(), view.getCourseGroup());
		//Creation part of same transaction will be null
		if(sequence.getDiscipline() != null)
		    sequence.getDiscipline().setDirty(true);
		Course newCourse = subjectAreaService.createCourse( sequence, course);
		return newCourse.getCourseId();
	}

	public void validateCourse(CourseViewDto course) {
		Set<String> errorMessages = new HashSet<String>();
		if (StringUtils.isBlank(course.getName())) {
			errorMessages.add("Course missing valid name.");
		}
		if (StringUtils.isBlank(course.getSubject())) {
			errorMessages.add("Course missing valid Subject.");
		} else if (course.getSubject().length() > 64) {
			errorMessages.add("Course Subject length is greater than 64 characters.");
		}
		if (StringUtils.isBlank(course.getNumber())) {
			errorMessages.add("Course missing valid number.");
		} else if (course.getNumber().length() > 34) {
			errorMessages.add("Course Number length is greater than 34 characters.");
		}
		if (course.getCourseGroup() == -1) {
			errorMessages.add("Course Group missing valid number.");
		}
		if (StringUtils.isBlank(course.getMmapEquivalentCode())) {
			errorMessages.add("Course missing MMAP Equivalent.");
		} else if (course.getMmapEquivalentCode().length() > 100) {
			errorMessages.add("MMAP Equivalent length is greater than 100 characters.");
		}
		if (errorMessages.size() > 0) {
			throw new ValidationException(errorMessages);
		}
	}

	public Set<DisciplineSequenceViewDto> deleteCourse(int courseId) {
		return sequenceAssembler.assembleDto(subjectAreaService.deleteCourse(courseId));
	}

	public void deleteDiscipline(int disciplineId) {
		subjectAreaService.deleteDiscipline(disciplineId);
	}

	public CB21ViewDto getCb21(Character cb21Code) {
		return cb21Assembler.assembleDto(subjectAreaService.getCb21(cb21Code));
	}
	public CourseViewDto getCourse(int courseId) {
		return courseAssembler.assembleDto(subjectAreaService.getCourseWithSequences(courseId));
	}

	public Set<CourseViewDto> getCourses(int disciplineId) {
		return sequenceCourseAssembler.assembleDto(subjectAreaService.getSequenceCoursesByDisciplineId(disciplineId));
	}

	public Set<CourseViewDto> getCourses(int disciplineId, char cb21, int courseGroup) {
		return sequenceCourseAssembler.assembleDto(subjectAreaService.getSequenceCoursesByDisciplineIdAndCB21(disciplineId, cb21, courseGroup));
	}

	@Transactional(readOnly = true)
	public DisciplineViewDto getDiscipline(int disciplineId) {
		Discipline discipline = subjectAreaService.getDiscipline(disciplineId);
		DisciplineViewDto disciplineViewDto = disciplineAssembler.assembleDto(discipline);
		return disciplineViewDto;
	}


	@Transactional
  public VersionedSubjectAreaViewDto getVersionedDiscipline(Discipline discipline) {
		if (discipline.getPublishedSubjectAreaVersion() != null) {
      return versionedSubjectAreaAssembler.assembleDto(discipline.getPublishedSubjectAreaVersion());
    } else {
      return createVersionedSubjectArea(discipline);
    }
	}

	@Transactional
  public VersionedSubjectAreaViewDto getVersionedDiscipline(int disciplineId) {
	  return getVersionedDiscipline(subjectAreaService.getDiscipline(disciplineId));
  }


  public Set<DisciplineViewDto> getDisciplines() {
		return disciplineAssembler.assembleDto(subjectAreaService.getDisciplines());
	}

	public Set<VersionedSubjectAreaViewDto> getVersionedSubjectAreaByCollegeId(Collection<String> collegeIds) {
    return subjectAreaService.getDisciplinesByCollegeId(collegeIds).stream().map(d -> getVersionedDiscipline(d))
      .collect(Collectors.toSet());
  }

	@Transactional(readOnly=true)
	public Set<VersionedSubjectAreaStudentViewDto> getVersionedSubjectAreaStudentViewByCollegeId(Collection<String> collegeIds) {
		Set<Discipline> disciplines = subjectAreaService.getDisciplinesByCollegeId(collegeIds);
		Set<VersionedSubjectAreaStudentViewDto> versionedSubjectAreaStudentViewDtos = versionedSubjectAreaStudentViewAssembler.assembleDto(disciplines);
		return versionedSubjectAreaStudentViewDtos;
	}

	@Transactional(readOnly=true)
	public Set<DisciplineViewDto> getDisciplinesByCollegeId(Collection<String> collegeIds) {
		return disciplineAssembler.assembleDto(subjectAreaService.getDisciplinesByCollegeId(collegeIds));
	}

	@Transactional(readOnly = true)
	public Set<DisciplineViewDto> getDisciplinesByCollegeIdAndDisciplineMap(String collegeId, String competencyMapDiscipline) {
		return disciplineAssembler.assembleDto(
				subjectAreaService.getDisciplinesByCollegeIdAndDisciplineMap(collegeId, competencyMapDiscipline));
	}

	@Transactional(readOnly=true)
	public Set<DisciplineSequenceViewDto> getSequencesByDisciplineId(int disciplineId) {
		return sequenceAssembler.assembleDto(subjectAreaService.getSequencesByDisciplineId(disciplineId));
	}

	@Transactional
	public void updateDiscipline(DisciplineViewDto discipline) {
		Discipline old = subjectAreaService.getDiscipline(discipline.getDisciplineId());
		if (old == null) {
			throw new NotFoundException("Cannot find discipline with id " + discipline.getDisciplineId());
		}

		validateDiscipline(discipline);

		if (!old.getCollegeId().equals(discipline.getCollegeId())) {
			throw new ValidationException("College Id can not be changed.");
		}

		if (!old.getCompetencyMapDiscipline().equals(discipline.getCompetencyMapDiscipline())) {
			throw new ValidationException("Competency Map can not be changed.");
		}

		Discipline updated = disciplineAssembler.disassembleDto(discipline);
		updated.setDirty(true);
		subjectAreaService.updateDiscipline(updated);
	}

	@Transactional
	public void updateCourse(CourseViewDto course) {
		Course old = subjectAreaService.getCourseWithSequences(course.getCourseId());
		if (old == null) {
			throw new NotFoundException("Cannot find course with id " + course.getCourseId());
		}

		subjectAreaService.updateCourse(old, course);
	}

	public void validateDisciplineSequence(DisciplineSequenceViewDto sequence) {
		if (sequence.getMappingLevel() != null && sequence.getMappingLevel().length() > 50) {
			throw new ValidationException((Collections.singleton("Mapping Level cannot be more than 50 characters")));
		}
	}

	@Transactional
	public DisciplineSequenceViewDto upsert(DisciplineSequenceViewDto view) {
		DisciplineSequence sequence = sequenceAssembler.disassembleDto(view);
		return sequenceAssembler.assembleDto(subjectAreaService.upsert(sequence));
	}

	@Transactional(readOnly=true)
	public Set<String> getCollegeIdsForCourse(int courseId) {
		return subjectAreaService.getCollegeIdsForCourse(courseId);
	}

	@Transactional
	public void removeEmptySequences(Set<DisciplineSequenceViewDto> sequences) {
		for (DisciplineSequenceViewDto dto : sequences) {
			subjectAreaService.removeEmptySequence(dto.getDisciplineId(), dto.getCb21Code(), dto.getCourseGroup());
		}
	}


  public VersionedSubjectAreaViewDto getVersionedSubjectAreaDto(Integer disciplineId, Integer version) {
    VersionedSubjectArea subjectArea = versionedSubjectAreaRepository.findByDisciplineIdAndVersion(disciplineId, version);
    return versionedSubjectAreaAssembler.assembleDto(subjectArea);
  }

	public Collection<VersionedSubjectAreaViewDto> getVersionedSubjectAreaDtos(Integer disciplineId) {
		return versionedSubjectAreaAssembler.assembleDto(versionedSubjectAreaRepository.findByDisciplineId(disciplineId));
	}

  public VersionedSubjectAreaViewDto createVersionedSubjectArea(int disciplineId) {
	  Discipline discipline = subjectAreaService.getDisciplineAndSequences(disciplineId);
	  if(discipline == null) {
		  throw new RuntimeException("Unable to find discipline in order to create a versioned subject area. Id requested " + disciplineId);
	  }
	  discipline.setDisciplineSequences(subjectAreaService.getSequencesByDisciplineId(disciplineId));
      return createVersionedSubjectArea(discipline);
  }

  public VersionedSubjectAreaViewDto createVersionedSubjectArea(Discipline discipline) {
	  if (discipline == null) {
	    return null;
    }
    Integer maxVersion = discipline.getMaxSubjectAreaVersion();
    int nextVersion = maxVersion == null ? 1 : maxVersion + 1;
    VersionedSubjectAreaViewDto dto = mapper.map(discipline, VersionedSubjectAreaViewDto.class);
    dto.setDisciplineSequences(sequenceAssembler.assembleDto(discipline.getDisciplineSequences()));
    dto.setCompetencyAttributes(attributesAssembler.assembleDto(discipline.getCompetencyAttributes()));
    dto.setPublishedVersion(nextVersion);
    dto.setPublishedTitle(discipline.getTitle());
    Date now = Calendar.getInstance().getTime();
    dto.setPublishedDate(now);
    dto.setLastEditedDate(now);
    dto.setVersion(nextVersion);
    dto.setPublishedDate(now);
    dto.setPublished(true);
    dto.setDirty(true);
    dto.setLastEditedDate(discipline.getLastUpdatedDate());
    versionedSubjectAreaRepository.save(versionedSubjectAreaAssembler.disassembleDto(dto));
    discipline.setMaxSubjectAreaVersion(nextVersion);
    subjectAreaService.updateDiscipline(discipline);
    return dto;
  }


  public VersionedSubjectAreaViewDto getLatestVersionForSubjectArea (int disciplineId) {
	  return versionedSubjectAreaAssembler.assembleDto(
	    subjectAreaService.getDiscipline(disciplineId).getPublishedSubjectAreaVersion()
    );
  }

	public void unpublishSubjectArea(int subjectAreaId) {
    versionedSubjectAreaRepository.unpublish(subjectAreaId);
  }

	public VersionedSubjectAreaViewDto getPublishedVersionForSubjectArea(Integer disciplineId) {
		return getLatestVersionForSubjectArea(disciplineId);
	}

  public Set<CourseViewDto> getCoursesFromVersionedSubjectArea (VersionedSubjectAreaViewDto versionedSubjectAreaViewDto, Character cb21Code, Integer courseGroup)
  {
        List<DisciplineSequenceViewDto> sequences = new ArrayList<>();
        if (versionedSubjectAreaViewDto != null && versionedSubjectAreaViewDto.getDisciplineSequences() != null) {
            sequences = versionedSubjectAreaViewDto.getDisciplineSequences().stream()
                    .filter(ds -> cb21Code.equals(ds.getCb21Code()) && courseGroup.equals(ds.getCourseGroup()))
                    .collect(Collectors.toList());
        } else {
        	return null;
        }
        if (sequences.size() != 1) {
            throw new org.cccnext.tesuto.exception.ValidationException("Unexpected number of sequence levels. Expected 1, Found " + sequences.size()
                    + " for College:" + versionedSubjectAreaViewDto.getCollegeId()
                    + ", Versioned Subject Area:" + versionedSubjectAreaViewDto.getTitle()
                    + ", Versioned Subject Area ID: " + versionedSubjectAreaViewDto.getDisciplineId()
                    + ", CB21: " + cb21Code.toString()
                    + ", courseGroup: " + courseGroup.toString());        }

        return sequences.get(0).getCourses();

	}

	public void unpublish(int disciplineId) {
	  versionedSubjectAreaRepository.unpublish(disciplineId);
  }

	//this is for cleanup after unit tests
  @Transactional
  public void deleteVersionedSubjectAreaByDisciplineId(int disciplineId) {
	  Discipline discipline = subjectAreaService.getDiscipline(disciplineId);
	  discipline.setMaxSubjectAreaVersion(null);
	  discipline.setPublishedSubjectAreaVersion(null);
	  subjectAreaService.updateDiscipline(discipline);
	  versionedSubjectAreaRepository.deleteByDisciplineId(disciplineId);
  }

}
