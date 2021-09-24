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
package org.cccnext.tesuto.content.assembler.item;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentChoiceInteractionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentExtendedTextInteractionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentInteractionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentMatchInteractionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentTextEntryInteractionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.item.metadata.ItemMetadataDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.shared.AssessmentOutcomeDeclarationDtoAssemberImplTest;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.cccnext.tesuto.content.model.item.AssessmentResponseProcessing;
import org.cccnext.tesuto.content.model.item.AssessmentResponseVar;
import org.cccnext.tesuto.content.model.item.AssessmentStimulusRef;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateDeclaration;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateProcessing;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInteraction;
import org.cccnext.tesuto.content.model.item.metadata.ItemMetadata;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentItemDtoAssemblerImplTest {

    @Autowired
    AssessmentItemDtoAssembler assessmentItemAssembler;

    public static AssessmentItem getAssessmentItem() {
        AssessmentItem assessmentItem = new AssessmentItem();
        assessmentItem.setBody("body");
        assessmentItem.setId("id");
        AssessmentInteraction assessmentInteraction = AssessmentInteractionDtoAssemblerImplTest
                .getAssessmentInteraction();
        List<AssessmentInteraction> interactions = new ArrayList();
        interactions.add(assessmentInteraction);
        interactions.add(AssessmentMatchInteractionDtoAssemblerImplTest.getMatchInteraction());
        interactions.add(AssessmentExtendedTextInteractionDtoAssemblerImplTest.getAssessmentExtendedTextInteraction());
        interactions.add(AssessmentTextEntryInteractionDtoAssemblerImplTest.getAssessmentTextEntryInteraction());
        interactions.add(AssessmentChoiceInteractionDtoAssemblerImplTest.getAssessmentChoiceInteraction());
        assessmentItem.setInteractions(interactions);
        assessmentItem.setLabel("label");
        assessmentItem.setLanguage("language");
        AssessmentOutcomeDeclaration outcomeDeclaration = AssessmentOutcomeDeclarationDtoAssemberImplTest.getAssessmentOutcomeDeclaration();
        assessmentItem.setOutcomeDeclarations(Arrays.asList(outcomeDeclaration));
        AssessmentResponseProcessing responseProcessing = AssessmentResponseProcessingDtoAssemblerImplTest
                .getAssessmentResponseProcessing();
        assessmentItem.setResponseProcessing(responseProcessing);

        AssessmentResponseVar responseVar = AssessmentResponseVarDtoAssemblerImplTest.getAssessmentResponseVar();
        assessmentItem.setResponseVars(Arrays.asList(responseVar));

        AssessmentStimulusRef stimulusRef = AssessmentStimulusRefDtoAssemblerImplTest.getAssessmentStimulusRef();
        assessmentItem.setStimulusRef(stimulusRef);

        AssessmentTemplateProcessing templateProcessing = AssessmentTemplateProcessingDtoAssemblerImplTest
                .getAssessmentTemplateProcessing();
        assessmentItem.setTemplateProcessing(templateProcessing);

        AssessmentTemplateDeclaration templateVar = AssessmentTemplateDeclarationDtoAssemblerImplTest
                .getAssessmentTemplateDeclaration();
        assessmentItem.setTemplateVars(Arrays.asList(templateVar));
        assessmentItem.setTitle("title");
        assessmentItem.setToolName("toolName");
        assessmentItem.setToolVersion("toolVersion");

        ItemMetadata itemMetadata = ItemMetadataDtoAssemblerImplTest.getMetadata();
        assessmentItem.setItemMetadata(itemMetadata);

        return assessmentItem;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentItem assessmentItem = getAssessmentItem();
        AssessmentItemDto assessmentItemDto = assessmentItemAssembler.assembleDto(assessmentItem);
        AssessmentItem assessmentItemDisassembled = assessmentItemAssembler.disassembleDto(assessmentItemDto);
        assertEquals("AssessmentItem incorrectly assembled, dissassembled", assessmentItem, assessmentItemDisassembled);
    }

}
