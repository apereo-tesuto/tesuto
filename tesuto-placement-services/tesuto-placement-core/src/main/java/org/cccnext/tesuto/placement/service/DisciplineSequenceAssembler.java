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
import org.cccnext.tesuto.placement.model.CB21;
import org.cccnext.tesuto.placement.model.DisciplineSequence;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 7/20/16.
 */
@Service("disciplineSequenceAssembler")
public class DisciplineSequenceAssembler extends AbstractAssembler<DisciplineSequenceViewDto, DisciplineSequence> {

    @Autowired Mapper mapper;

    @Autowired
    DisciplineSequenceCourseAssembler cdscAssembler;

    @Override
    protected DisciplineSequenceViewDto doAssemble(DisciplineSequence sequence) {
        DisciplineSequenceViewDto view = mapper.map(sequence, DisciplineSequenceViewDto.class);
        view.setCourses(cdscAssembler.assembleDto(sequence.getDisciplineSequenceCourses()));
        view.setLevel(sequence.getCb21().getLevelsBelowTransfer());
        view.setCb21Code(sequence.getCb21Code());
        return view;
    }

    @Override
    protected DisciplineSequence doDisassemble(DisciplineSequenceViewDto view) {
        DisciplineSequence sequence = mapper.map(view, DisciplineSequence.class);
        sequence.setDisciplineSequenceCourses(cdscAssembler.disassembleDto(view.getCourses()));
        CB21 cb21 = new CB21();
        cb21.setCb21Code(view.getCb21Code());
        cb21.setLevelsBelowTransfer(view.getLevel());
        sequence.setCb21(cb21);
        return sequence;
    }
}
