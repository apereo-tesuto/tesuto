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

import java.util.Arrays;

import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.section.AssessmentItemRef;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentItemRefDtoAssemblerImplTest {

    @Autowired
    AssessmentItemRefDtoAssembler assessmentItemRefAssembler;

    public static AssessmentItemRef getAssessmentItemRef() {
        AssessmentItemRef assessmentItemRef = new AssessmentItemRef();
        AssessmentBranchRule branchRule = AssessmentBranchRuleDtoAssemblerImplTest.getAssessmentBranchRule();
        assessmentItemRef.setBranchRules(Arrays.asList(branchRule));
        assessmentItemRef.setCategories(Arrays.asList("category1, category2, category3"));
        assessmentItemRef.setItemIdentifier("href");
        assessmentItemRef.setIdentifier("id");
        assessmentItemRef.setIsFixed(true);
        assessmentItemRef.setIsRequired(false);
        AssessmentItemSessionControl itemSessionControl = AssessmentItemSessionControlDtoAssemblerImplTest
                .getAssessmentItemSessionControl();
        assessmentItemRef.setItemSessionControl(itemSessionControl);

        AssessmentPreCondition preCondition = AssessmentPreconditionDtoAssemblerImplTest.getAssessmentPrecondition();
        assessmentItemRef.setPreConditions(Arrays.asList(preCondition));
        assessmentItemRef.setTemplateDefaultss(Arrays.asList("templateDefaults"));
        assessmentItemRef.setTimeLimits(100.0);
        assessmentItemRef.setVariableMapping("variableMapping");
        assessmentItemRef.setWeight(10.0);
        return assessmentItemRef;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentItemRef assessmentItemRef = getAssessmentItemRef();
        AssessmentItemRefDto assessmentItemRefDto = assessmentItemRefAssembler.assembleDto(assessmentItemRef);
        AssessmentItemRef assessmentItemRefDisassembled = assessmentItemRefAssembler
                .disassembleDto(assessmentItemRefDto);
        assertEquals("AssessmentItemRef incorrectly assembled, dissassembled", assessmentItemRef,
                assessmentItemRefDisassembled);
    }

}
