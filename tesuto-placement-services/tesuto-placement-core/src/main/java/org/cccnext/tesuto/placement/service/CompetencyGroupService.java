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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.placement.model.CompetencyGroup;
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.repository.jpa.CompetencyGroupMappingRepository;
import org.cccnext.tesuto.placement.repository.jpa.CompetencyGroupRepository;
import org.cccnext.tesuto.placement.repository.jpa.CourseRepository;
import org.cccnext.tesuto.placement.repository.jpa.DisciplineRepository;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("competencyGroupService")
@Transactional
public class CompetencyGroupService {

    @Autowired
    private CompetencyGroupRepository repository;

    @Autowired
    private CompetencyGroupMappingRepository mappingRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private CompetencyGroupAssembler assembler;

    public CompetencyGroupRepository getRepository() {
        return repository;
    }

    public void setRepository(CompetencyGroupRepository repository) {
        this.repository = repository;
    }

    public Set<CompetencyGroupViewDto> getCompetencyGroups(Integer courseId) {
        return assembler.assembleDto(repository.findCompetencyGroupsByCourse(courseRepository.getOne(courseId)));
    }

    public CompetencyGroupViewDto get(Integer competencyGroupId) {
        return assembler.assembleDto(repository.findById(competencyGroupId).get());
    }

    public int upsert(CompetencyGroupViewDto competencyGroupViewDto) {
        if (competencyGroupViewDto.getCompetencyGroupId() == null
                || competencyGroupViewDto.getCompetencyGroupId() == 0) {
            competencyGroupViewDto.setCompetencyGroupId(null);
            return create(competencyGroupViewDto);
        }
        return update(competencyGroupViewDto);
    }

    public int create(CompetencyGroupViewDto competencyGroupViewDto) {
        Set<String> mappings = competencyGroupViewDto.getCompetencyIds();
        competencyGroupViewDto.setCompetencyIds(null);
        CompetencyGroup competencyGroup = assembler.disassembleDto(competencyGroupViewDto);
        competencyGroupViewDto.setCompetencyIds(mappings);

        // Mark the dirty flag
        Course course = competencyGroup.getCourse();
        for (DisciplineSequenceCourse disciplineSequenceCourse : course.getDisciplineSequenceCourses()) {
            Discipline discipline = disciplineSequenceCourse.getDisciplineSequence().getDiscipline();
            discipline.setDirty(true);
        }
        CompetencyGroup newCompetencyGroup = repository.save(competencyGroup);
        saveCompetencyGroupMappings(mappings, newCompetencyGroup);
        return newCompetencyGroup.getCompetencyGroupId();
    }

    private void saveCompetencyGroupMappings(Set<String> competencyIds, CompetencyGroup newCompetencyGroup) {

        int competencyGroupId = newCompetencyGroup.getCompetencyGroupId();
        Set<CompetencyGroupMapping> currentMappings = mappingRepository.findByCompetencyGroupId(competencyGroupId);

        removeMappings(competencyIds, currentMappings);

        if (competencyIds == null || competencyIds.size() == 0) {
            return;
        }
        Set<String> filteredCompetencyIds = new HashSet<String>(competencyIds);
        if (CollectionUtils.isNotEmpty(currentMappings)){
            Set<String> storedIds = currentMappings.stream().map(cm -> cm.getCompetencyId())
                    .collect(Collectors.toSet());
            filteredCompetencyIds = competencyIds.stream().filter(id -> !storedIds.contains(id))
                    .collect(Collectors.toSet());
        }

        for (String competencyId : filteredCompetencyIds) {
            CompetencyGroupMapping mapping = new CompetencyGroupMapping();
            mapping.setCompetencyGroupId(competencyGroupId);
            mapping.setCompetencyId(competencyId);
            // Mark the dirty flag
            Course course = newCompetencyGroup.getCourse();
            //course = courseRepository.getOne(course.getCourseId());
            for (DisciplineSequenceCourse disciplineSequenceCourse : course.getDisciplineSequenceCourses()) {
                Discipline discipline = disciplineSequenceCourse.getDisciplineSequence().getDiscipline();
                discipline.setDirty(true);
            }
            mappingRepository.save(mapping);
        }
    }

    private Set<CompetencyGroupMapping> removeMappings(Set<String> competencyIds,
            Set<CompetencyGroupMapping> currentMappings) {

        if (currentMappings != null && currentMappings.size() > 0) {
            if (competencyIds == null || competencyIds.size() == 0) {
                currentMappings.forEach(c -> mappingRepository.delete(c));
                currentMappings.clear();
            }
            Set<CompetencyGroupMapping> deletedMappings = currentMappings.stream()
                    .filter(cm -> !competencyIds.contains(cm.getCompetencyId())).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(deletedMappings)) {
                deletedMappings.forEach(dm -> mappingRepository.delete(dm));
                currentMappings.remove(deletedMappings);
            }
        }
        return currentMappings;
    }

    private int update(CompetencyGroupViewDto competencyGroupViewDto) {
        Set<String> mappings = competencyGroupViewDto.getCompetencyIds();
        competencyGroupViewDto.setCompetencyIds(null);
        CompetencyGroup updateCompetencyGroup = assembler.disassembleDto(competencyGroupViewDto);
        competencyGroupViewDto.setCompetencyIds(mappings);
        CompetencyGroup competencyGroupUpserted = repository.save(updateCompetencyGroup);
        saveCompetencyGroupMappings(mappings, competencyGroupUpserted);
        return competencyGroupUpserted.getCompetencyGroupId();
    }

    public void delete(int competencyGroupId) {
        // Mark the dirty flag
        CompetencyGroup competencyGroup = repository.findById(competencyGroupId).get();
        Course course = competencyGroup.getCourse();
        for (DisciplineSequenceCourse disciplineSequenceCourse : course.getDisciplineSequenceCourses()) {
            Discipline discipline = disciplineSequenceCourse.getDisciplineSequence().getDiscipline();
            discipline.setDirty(true);
            disciplineRepository.save(discipline);
        }
        
        repository.deleteById(competencyGroupId);
    }

}
