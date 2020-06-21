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
package org.cccnext.tesuto.content.assembler.section;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.content.assembler.item.AssessmentStimulusRefDtoAssemblerImplTest;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.item.AssessmentStimulusRef;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.section.AssessmentItemRef;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.section.AssessmentOrdering;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;
import org.cccnext.tesuto.content.model.section.AssessmentRubricBlock;
import org.cccnext.tesuto.content.model.section.AssessmentSection;
import org.cccnext.tesuto.content.model.section.AssessmentSelection;
import org.cccnext.tesuto.content.model.shared.AssessmentComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentSectionDtoAssemblerImplTest {

    @Autowired
    AssessmentSectionDtoAssembler assessmentSectionAssembler;

    public static AssessmentSection getAssessmentSection() {
        // Three levels deep.
        AssessmentSection levelOne = getAssessmentSectionWithouts();
        AssessmentSection levelTwo = getAssessmentSectionWithouts();
        AssessmentSection levelThree = getAssessmentSectionWithouts();
        levelTwo.setAssessmentSections(Arrays.asList(levelThree));
        levelOne.setAssessmentSections(Arrays.asList(levelTwo));

        AssessmentSection assessmentSection = getAssessmentSectionWithouts();
        assessmentSection.setAssessmentSections(Arrays.asList(levelOne));
        return assessmentSection;
    }

    public static AssessmentSection getAssessmentSectionWithouts() {
        AssessmentSection assessmentSection = new AssessmentSection();
        AssessmentItemRef AssessmentItemRef = AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef();
        assessmentSection.setAssessmentItemRefs(Arrays.asList(AssessmentItemRef));

        AssessmentStimulusRef assessmentStimulusRef = AssessmentStimulusRefDtoAssemblerImplTest
                .getAssessmentStimulusRef();
        assessmentSection.setAssessmentStimulusRef(assessmentStimulusRef);

        AssessmentBranchRule assessmentBranchRule = AssessmentBranchRuleDtoAssemblerImplTest.getAssessmentBranchRule();
        assessmentSection.setBranchRules(Arrays.asList(assessmentBranchRule));
        assessmentSection.setId("id");
        assessmentSection.setIsFixed(true);
        assessmentSection.setIsRequired(false);

        AssessmentItemSessionControl itemSessionControl = AssessmentItemSessionControlDtoAssemblerImplTest
                .getAssessmentItemSessionControl();
        assessmentSection.setItemSessionControl(itemSessionControl);

        assessmentSection.setKeepTogether(true);

        AssessmentOrdering ordering = AssessmentOrderingDtoAssemblerImplTest.getAssessmentOrdering();
        assessmentSection.setOrdering(ordering);

        AssessmentPreCondition preCondition = AssessmentPreconditionDtoAssemblerImplTest.getAssessmentPrecondition();
        assessmentSection.setPreConditions(Arrays.asList(preCondition));

        AssessmentRubricBlock rubricBlock = AssessmentRubricBlockDtoAssemblerImplTest.getAssessmentRubricBlock();
        assessmentSection.setRubricBlocks(Arrays.asList(rubricBlock));

        AssessmentSelection selection = AssessmentSelectionDtoAssemblerImplTest.getAssessmentSelection();
        assessmentSection.setSelection(selection);

        List<AssessmentComponent> components = new ArrayList<>();
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        components.add(getAssessmentSectionWithoutNestedComponent());
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());

        assessmentSection.setAssessmentComponents(components);
        assessmentSection.setTimeLimits("timeLimits");
        assessmentSection.setTitle("title");
        assessmentSection.setVisible(true);
        return assessmentSection;
    }

    public static AssessmentSection getAssessmentSectionWithoutNestedComponent() {
        AssessmentSection assessmentSection = new AssessmentSection();
        AssessmentItemRef AssessmentItemRef = AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef();
        assessmentSection.setAssessmentItemRefs(Arrays.asList(AssessmentItemRef));

        AssessmentStimulusRef assessmentStimulusRef = AssessmentStimulusRefDtoAssemblerImplTest
                .getAssessmentStimulusRef();
        assessmentSection.setAssessmentStimulusRef(assessmentStimulusRef);

        AssessmentItemSessionControl itemSessionControl = AssessmentItemSessionControlDtoAssemblerImplTest
                .getAssessmentItemSessionControl();
        assessmentSection.setItemSessionControl(itemSessionControl);

        assessmentSection.setKeepTogether(true);

        AssessmentOrdering ordering = AssessmentOrderingDtoAssemblerImplTest.getAssessmentOrdering();
        assessmentSection.setOrdering(ordering);

        AssessmentPreCondition preCondition = AssessmentPreconditionDtoAssemblerImplTest.getAssessmentPrecondition();
        assessmentSection.setPreConditions(Arrays.asList(preCondition));

        AssessmentRubricBlock rubricBlock = AssessmentRubricBlockDtoAssemblerImplTest.getAssessmentRubricBlock();
        assessmentSection.setRubricBlocks(Arrays.asList(rubricBlock));

        AssessmentSelection selection = AssessmentSelectionDtoAssemblerImplTest.getAssessmentSelection();
        assessmentSection.setSelection(selection);

        List<AssessmentComponent> components = new ArrayList<>();
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        components.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        assessmentSection.setAssessmentComponents(components);

        assessmentSection.setVisible(true);
        return assessmentSection;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentSection assessmentSection = getAssessmentSection();
        AssessmentSectionDto assessmentSectionDto = assessmentSectionAssembler.assembleDto(assessmentSection);
        AssessmentSection assessmentSectionDisassembled = assessmentSectionAssembler
                .disassembleDto(assessmentSectionDto);
        assertEquals("AssessmentSection incorrectly assembled, dissassembled", assessmentSection,
                assessmentSectionDisassembled);
    }

}
