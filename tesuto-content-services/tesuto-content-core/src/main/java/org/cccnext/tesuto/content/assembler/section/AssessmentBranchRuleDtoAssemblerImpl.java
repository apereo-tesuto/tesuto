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

import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "branchRuleDtoAssembler")
public class AssessmentBranchRuleDtoAssemblerImpl implements AssessmentBranchRuleDtoAssembler {

    @Autowired
    AssessmentParentExpressionDtoAssembler expressionParentDtoAssembler;

    @Override
    public AssessmentBranchRuleDto assembleDto(AssessmentBranchRule branchRule) {
        // Drop out immediately if there is nothing to assemble.
        if (branchRule == null) {
            return null;
        }

        AssessmentBranchRuleDto branchRuleDto = new AssessmentBranchRuleDto();
        // TODO: Verify that this is in MathML format so we can handle it
        // properly
        branchRuleDto.setTarget(branchRule.getTarget().toString());
        branchRuleDto.setAssessmentParentExpression(
                expressionParentDtoAssembler.assembleDto(branchRule.getAssessmentParentExpression()));
        return branchRuleDto;
    }

    @Override
    public AssessmentBranchRule disassembleDto(AssessmentBranchRuleDto branchRuleDto) {
        // Drop out immediately if there is nothing to assemble.
        if (branchRuleDto == null) {
            return null;
        }

        AssessmentBranchRule branchRule = new AssessmentBranchRule();
        branchRule.setTarget(branchRuleDto.getTarget().toString());
        branchRule.setAssessmentParentExpression(
                expressionParentDtoAssembler.disassembleDto(branchRuleDto.getAssessmentParentExpression()));
        return branchRule;
    }
}
