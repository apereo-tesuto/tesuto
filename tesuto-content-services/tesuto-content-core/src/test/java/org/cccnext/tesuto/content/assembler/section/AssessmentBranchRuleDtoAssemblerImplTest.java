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

import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentBranchRuleDtoAssemblerImplTest {

    @Autowired
    AssessmentBranchRuleDtoAssembler branchRuleDtoAssembler;

    public static AssessmentBranchRule getAssessmentBranchRule() {
        AssessmentBranchRule assessmentBranchRule = new AssessmentBranchRule();

        assessmentBranchRule.setTarget("target");
        assessmentBranchRule
                .setAssessmentParentExpression(AssessmentParentExpressionDtoAssemblerImplTest.getParentExpression());
        return assessmentBranchRule;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentBranchRuleDtoAssembler assessmentBranchRuleAssembler = new AssessmentBranchRuleDtoAssemblerImpl();
        AssessmentBranchRule assessmentBranchRule = getAssessmentBranchRule();
        AssessmentBranchRuleDto assessmentBranchRuleDto = branchRuleDtoAssembler.assembleDto(assessmentBranchRule);
        AssessmentBranchRule assessmentBranchRuleDisassembled = branchRuleDtoAssembler
                .disassembleDto(assessmentBranchRuleDto);
        assertEquals("AssessmentBranchRule incorrectly assembled, dissassembled", assessmentBranchRule,
                assessmentBranchRuleDisassembled);
    }

}
