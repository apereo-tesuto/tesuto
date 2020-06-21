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
package org.cccnext.tesuto.content.assembler.view;

import org.cccnext.tesuto.content.assembler.view.assessment.AssessmentViewDtoAssembler;
import org.cccnext.tesuto.content.assembler.view.assessment.AssessmentViewDtoAssemblerImpl;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentViewDtoAssemblerTest {

    AssessmentViewDtoAssembler assessmentViewDtoAssembler = new AssessmentViewDtoAssemblerImpl();

    public static AssessmentDto getAssessmentDto() {
        AssessmentDto assessment = new AssessmentDto();
        assessment.setId("id");
        assessment.setLanguage("language");
        assessment.setTitle("title");
        assessment.setToolName("toolName");
        assessment.setToolVersion("toolVersion");
        assessment.setNamespace("namespace");
        assessment.setVersion(0);
        assessment.setIdentifier("identifier");
        assessment.setDuration(100.0);
        AssessmentMetadataDto metadataDto = new AssessmentMetadataDto();
        DeliveryTypeMetadataDto deliveryType = new DeliveryTypeMetadataDto();
        deliveryType.setOnline("false");
        deliveryType.setPaper("truE");
        metadataDto.setDeliveryType(deliveryType);
        metadataDto.setCompetencyMapDisciplines(Arrays.asList("Discipline"));
        assessment.setAssessmentMetadata(metadataDto);
        return assessment;
    }

    @Test
    public void testAssembler() {
        AssessmentDto assessmentDto = getAssessmentDto();
        AssessmentViewDto assessmentViewDto = assessmentViewDtoAssembler.assembleViewDto(assessmentDto);
        assertEquals("Duration is equal", assessmentDto.getDuration(), assessmentViewDto.getDuration());
        assertEquals("Id is equal", assessmentDto.getId(), assessmentViewDto.getId());
        assertEquals("Identifier is equal", assessmentDto.getIdentifier(), assessmentViewDto.getIdentifier());
        assertEquals("Language is equal", assessmentDto.getLanguage(), assessmentViewDto.getLanguage());
        assertEquals("Namespace is equal", assessmentDto.getNamespace(), assessmentViewDto.getNamespace());
        assertEquals("Title is equal", assessmentDto.getTitle(), assessmentViewDto.getTitle());
        assertEquals("Version is equal", assessmentDto.getVersion(), assessmentViewDto.getVersion());
        assertEquals("Disciplines are equal", assessmentDto.getAssessmentMetadata().getCompetencyMapDisciplines(), assessmentViewDto.getDisciplines());
        assertFalse(assessmentViewDto.isOnline());
        assertTrue(assessmentViewDto.isPaper());
    }

}
