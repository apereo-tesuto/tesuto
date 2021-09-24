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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.placement.dto.CollegeCompetencyAverageViewDto;
import org.cccnext.tesuto.placement.dto.CompetencyGroupAverageViewDto;
import org.cccnext.tesuto.placement.dto.CourseSubjectAreaCompetencyGroupAverageViewDto;
import org.cccnext.tesuto.placement.model.CompetencyGroup;
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.model.Discipline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class CompetencyAveragesService {

    @Autowired
    CollegeReader collegeRestClient;

    @Autowired
    SubjectAreaService subjectAreaService;

    @Autowired
    CompetencyMapOrderReader competencyMapOrderReader;

    @Autowired
    PlacementHelperService placementHelperService;

    public CollegeCompetencyAverageViewDto calculateCollegeCompetencyAverages(String collegeId, Integer subjectAreaId, Integer courseId) {
        CollegeCompetencyAverageViewDto collegeCompetencyAverageViewDto = new CollegeCompetencyAverageViewDto();
        CollegeViewDto college = collegeRestClient.getCollegeByMisCode(collegeId);
        collegeCompetencyAverageViewDto.setCollegeId(collegeId);
        collegeCompetencyAverageViewDto.setCollegeName(college.getName());

        if (subjectAreaId != null) {
            if (courseId != null) {
                CourseSubjectAreaCompetencyGroupAverageViewDto averageDto = calculateAveragesForCourseAndSubjectArea(courseId, subjectAreaId);
                collegeCompetencyAverageViewDto.getCourseSubjectAreaCompetencyGroupAverageViewDtoSet().add(averageDto);
            } else {
                collegeCompetencyAverageViewDto.setCourseSubjectAreaCompetencyGroupAverageViewDtoSet(calculateAveragesForAllCoursesForSubjectArea(subjectAreaId));
            }
        } else {
            collegeCompetencyAverageViewDto.setCourseSubjectAreaCompetencyGroupAverageViewDtoSet(calculateAveragesForAllCoursesAndSubjectAreas(collegeId));
        }

        return collegeCompetencyAverageViewDto;
    }

    private Set<CourseSubjectAreaCompetencyGroupAverageViewDto> calculateAveragesForAllCoursesAndSubjectAreas(String collegeId) {
        Set<CourseSubjectAreaCompetencyGroupAverageViewDto> courseSubjectAreaCompetencyGroupAverageViewDtoSet = new HashSet<>();
        for (Discipline discipline : subjectAreaService.getDisciplinesByCollegeId(Arrays.asList(collegeId))) {
            for (Course course : subjectAreaService.getCourses(discipline.getDisciplineId())) {
                courseSubjectAreaCompetencyGroupAverageViewDtoSet.add(calculateAveragesForCourseAndSubjectArea(course.getCourseId(), discipline.getDisciplineId()));
            }
        }
        return courseSubjectAreaCompetencyGroupAverageViewDtoSet;
    }

    private Set<CourseSubjectAreaCompetencyGroupAverageViewDto> calculateAveragesForAllCoursesForSubjectArea(int subjectAreaId) {
        Set<CourseSubjectAreaCompetencyGroupAverageViewDto> courseSubjectAreaCompetencyGroupAverageViewDtoSet = new HashSet<>();
        for (Course course : subjectAreaService.getCourses(subjectAreaId)) {
            courseSubjectAreaCompetencyGroupAverageViewDtoSet.add(calculateAveragesForCourseAndSubjectArea(course.getCourseId(), subjectAreaId));
        }

        return courseSubjectAreaCompetencyGroupAverageViewDtoSet;
    }

    private CourseSubjectAreaCompetencyGroupAverageViewDto calculateAveragesForCourseAndSubjectArea(int courseId, int subjectAreaId) {
        CourseSubjectAreaCompetencyGroupAverageViewDto courseSubjectAreaCompetencyGroupAverageViewDto = new CourseSubjectAreaCompetencyGroupAverageViewDto();

        Course course = subjectAreaService.getCourse(courseId);
        Discipline discipline = subjectAreaService.getDiscipline(subjectAreaId);
        if (course != null && discipline != null) {
            courseSubjectAreaCompetencyGroupAverageViewDto.setCourseId(courseId);
            courseSubjectAreaCompetencyGroupAverageViewDto.setCourseName(course.getName());

            courseSubjectAreaCompetencyGroupAverageViewDto.setSubjectAreaId(subjectAreaId);
            courseSubjectAreaCompetencyGroupAverageViewDto.setSubjectAreaTitle(discipline.getTitle());

            Set<CompetencyGroup> competencies = course.getCompetencyGroups();

            String competencyMapOrderId = competencyMapOrderReader.findLatestPublishedIdByCompetencyMapDiscipline(discipline.getCompetencyMapDiscipline());

            List<CompetencyDifficultyDto> competencyDifficultyDtos = competencyMapOrderReader.getOrderedCompetencies(competencyMapOrderId);

            Map<String, CompetencyDifficultyDto> competencyDifficultyMap = placementHelperService.buildCompetencyDifficultyMap(competencyDifficultyDtos);

            if (competencies != null) {
                for (CompetencyGroup competencyGroup : competencies) {
                    CompetencyGroupAverageViewDto competencyAverageViewDto = new CompetencyGroupAverageViewDto();
                    double average = calculateAverageForCompetencyGroupMapping(competencyGroup.getCompetencyGroupMappings(), competencyDifficultyMap);
                    competencyAverageViewDto.setCompetencyGroupAverage(average);
                    competencyAverageViewDto.setCompetencyGroupId(competencyGroup.getCompetencyGroupId());
                    competencyAverageViewDto.setCompetencyGroupTitle(competencyGroup.getTitle());
                    courseSubjectAreaCompetencyGroupAverageViewDto.getCompetencyGroupAverageViewDtoSet().add(competencyAverageViewDto);
                }
            }
        }

        return courseSubjectAreaCompetencyGroupAverageViewDto;
    }

    private double calculateAverageForCompetencyGroupMapping(Set<CompetencyGroupMapping> competencies, Map<String, CompetencyDifficultyDto> competencyDifficultyMap) {
        int count = 0;
        double total = 0.0;
        for(CompetencyGroupMapping mapping:competencies) {
            CompetencyDifficultyDto difficulty = competencyDifficultyMap.get(mapping.getCompetencyId());
            if(difficulty != null) {
                count += 1;
                total += difficulty.getDifficulty();
            }
        }
        return (total/(double)count);
    }
}
