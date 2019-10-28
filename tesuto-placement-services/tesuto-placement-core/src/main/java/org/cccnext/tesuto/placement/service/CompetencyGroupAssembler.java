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
import java.util.stream.Collectors;

import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.placement.model.CompetencyGroup;
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping;
import org.cccnext.tesuto.placement.model.Course;
import org.cccnext.tesuto.placement.repository.jpa.CourseRepository;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jstanley on 7/28/16.
 */
@Service("competencyGroupAssembler")
public class CompetencyGroupAssembler extends AbstractAssembler<CompetencyGroupViewDto, CompetencyGroup> {

    @Autowired
    CourseRepository courseRepository;

    @Override
    protected CompetencyGroupViewDto doAssemble(CompetencyGroup entity) {
        CompetencyGroupViewDto view = new CompetencyGroupViewDto();
        view.setCompetencyGroupId(entity.getCompetencyGroupId());
        view.setPercent(entity.getProbabilitySuccess());
        view.setCourseId(entity.getCourse().getCourseId());
        view.setName(entity.getTitle());
        if (entity.getCompetencyGroupMappings() != null && entity.getCompetencyGroupMappings().size() > 0) {
            view.setCompetencyIds(entity.getCompetencyGroupMappings().stream().map(map -> map.getCompetencyId())
                    .collect(Collectors.toSet()));
        } else {
            view.setCompetencyIds(new HashSet<String>());
        }
        
        return view;
    }

    @Override
    protected CompetencyGroup doDisassemble(CompetencyGroupViewDto view) {
        CompetencyGroup entity = new CompetencyGroup();
        entity.setCompetencyGroupId(view.getCompetencyGroupId());
        entity.setProbabilitySuccess(view.getPercent());
        entity.setTitle(view.getName());
        Course course = courseRepository.findWithSequences(view.getCourseId());
        entity.setCourse(course);

        if (view.getCompetencyIds() != null && view.getCompetencyIds().size() > 0) {
            entity.setCompetencyGroupMappings(view.getCompetencyIds().stream()
                    .map(competencyId -> dissassembleMapping(competencyId, view.getCompetencyGroupId()))
                    .collect(Collectors.toSet()));
        } 
        return entity;
    }

    private CompetencyGroupMapping dissassembleMapping(String competencyId, int competencyGroupId) {
        CompetencyGroupMapping mapping = new CompetencyGroupMapping();
        mapping.setCompetencyGroupId(competencyGroupId);
        mapping.setCompetencyId(competencyId);
        return mapping;
    }
}
