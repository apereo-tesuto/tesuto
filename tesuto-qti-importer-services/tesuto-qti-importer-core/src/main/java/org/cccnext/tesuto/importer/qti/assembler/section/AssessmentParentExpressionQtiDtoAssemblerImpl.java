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
package org.cccnext.tesuto.importer.qti.assembler.section;

import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.*;

/**
 * (1) Branchrules have an expression that have nodeGroupLists with and
 * arraylist of expressionGroups with children that contain baseValue and
 * variable (2) Branchrules have an expression that has children with baseValue
 * and variable
 *
 * From QTI A branchRule is as follows:
 *
 * Branch rule --> Expression -(1)-> NodeGroupList (nodeGroups) --> ArrayList
 * <ExpressionGroup> (groups) --> children --> baseValue & variable |
 * ^____________________________________________________________________| ^ | |
 * |_________(2)
 * ___________________________________________________________________________|
 *
 * Created by jasonbrown on 2/5/16.
 */
@Component(value = "parentExpressionQtiDtoAssembler")
public class AssessmentParentExpressionQtiDtoAssemblerImpl implements AssessmentParentExpressionQtiDtoAssembler {

    @Autowired
    AssessmentChildExpressionQtiDtoAssembler childExpressionQtiDtoAssembler;

    private final ExpressionType[] supportedParentExpressions = { ExpressionType.AND, ExpressionType.OR };

    private final Set<ExpressionType> supportedParentExpressionSet = new HashSet<>(
            Arrays.asList(supportedParentExpressions));

    @Override
    public AssessmentParentExpressionDto assembleDto(Expression expression) {
        if (expression == null) {
            return null;
        }
        AssessmentParentExpressionDto assessmentParentExpressionDto = new AssessmentParentExpressionDto();

        /**
         * Parent Expression Logic for a Branch Rule with And | Or Expression
         * These nodes will have multiple children.
         */
        if (supportedParentExpressionSet.contains(expression.getType())) {
            assessmentParentExpressionDto.setExpressionType(
                    expression.getType()); /* Otherwise null */
            List<AssessmentParentExpressionDto> assessmentParentExpressionDtoList = new ArrayList<>(
                    expression.getExpressions().size());
            List<AssessmentChildExpressionDto> assessmentChildExpressionDtoList = new ArrayList<>(
                    expression.getExpressions().size());
            for (Expression childExpression : expression.getExpressions()) {
                if (supportedParentExpressionSet.contains(childExpression.getType())) {
                    assessmentParentExpressionDtoList.add(assembleDto(childExpression));
                } else if (AssessmentChildExpressionQtiDtoAssemblerImpl.supportedChildExpressionSet
                        .contains(childExpression.getType())) {
                    assessmentChildExpressionDtoList.add(childExpressionQtiDtoAssembler.assembleDto(childExpression));
                } else {
                    throw new IllegalArgumentException(String
                            .format("Parent Expression type is not currently supported: %s", expression.getType()));
                }
            }
            assessmentParentExpressionDto.setAssessmentParentExpressionDtoList(assessmentParentExpressionDtoList);
            assessmentParentExpressionDto.setAssessmentChildExpressionDtoList(assessmentChildExpressionDtoList);
        }
        /**
         * Parent Expression Logic for a Branch Rule without And | Or Expression
         * These nodes will only have one child.
         */
        else if (AssessmentChildExpressionQtiDtoAssemblerImpl.supportedChildExpressionSet
                .contains(expression.getType())) {
            List<AssessmentChildExpressionDto> assessmentChildExpressionDtoList1 = new ArrayList<>(1);
            assessmentChildExpressionDtoList1.add(childExpressionQtiDtoAssembler.assembleDto(expression));
            assessmentParentExpressionDto.setAssessmentChildExpressionDtoList(assessmentChildExpressionDtoList1);
        } else {
            throw new IllegalArgumentException(
                    String.format("Parent Expression type is not currently supported: %s", expression.getType()));
        }

        return assessmentParentExpressionDto;
    }

    @Override
    public Expression disassembleDto(AssessmentParentExpressionDto assessmentExpressionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
