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
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by bruce on 7/22/16.
 */
@Service
public class DisciplineSequenceCourseAssembler extends AbstractAssembler<CourseViewDto, DisciplineSequenceCourse> {

    @Autowired
    Mapper mapper;

    @Autowired
    private CourseAssembler courseAssembler;

    @Override
    protected CourseViewDto doAssemble(DisciplineSequenceCourse cdsc) {

        CourseViewDto view = courseAssembler.assembleDto(cdsc.getCourse());
        view.setCb21Code(cdsc.getCb21Code());
        view.setCourseGroup(cdsc.getCourseGroup());
        view.setDisciplineId(cdsc.getDisciplineId());
        view.setAuditId(cdsc.getAuditId());
        return view;
    }

    @Override
    protected DisciplineSequenceCourse doDisassemble(CourseViewDto view) {
        DisciplineSequenceCourse cdsc = mapper.map(view, DisciplineSequenceCourse.class);
        cdsc.setCourse(courseAssembler.disassembleDto(view));
        cdsc.setCb21Code(view.getCb21Code());
        cdsc.setCourseGroup(view.getCourseGroup());
        cdsc.setAuditId(view.getAuditId());
        return cdsc;
    }

}
