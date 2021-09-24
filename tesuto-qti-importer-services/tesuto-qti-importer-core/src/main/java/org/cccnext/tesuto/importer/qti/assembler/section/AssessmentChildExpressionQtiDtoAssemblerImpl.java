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
import org.springframework.stereotype.Component;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.AttributeList;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.node.expression.general.BaseValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jasonbrown on 2/6/16.
 */
@Component(value = "childExpressionQtiDtoAssembler")
public class AssessmentChildExpressionQtiDtoAssemblerImpl implements AssessmentChildExpressionQtiDtoAssembler {

    private static final ExpressionType[] supportedChildExpressions = { ExpressionType.GT, ExpressionType.GTE,
            ExpressionType.EQUAL, ExpressionType.LTE, ExpressionType.LT };

    public static final Set<ExpressionType> supportedChildExpressionSet = new HashSet<>(
            Arrays.asList(supportedChildExpressions));

    @Override
    public AssessmentChildExpressionDto assembleDto(Expression expression) {
        if (expression == null) {
            return null;
        }

        AssessmentChildExpressionDto assessmentChildExpressionDto = new AssessmentChildExpressionDto();
        assessmentChildExpressionDto.setExpressionType(expression.getType()); // checked
                                                                              // in
                                                                              // parent
                                                                              // expression
        for (Expression childExpression : expression.getExpressions()) {
            if (childExpression.getType() == ExpressionType.VARIABLE) {
                AttributeList variableAttributes = childExpression.getAttributes();
                Attribute attribute = variableAttributes.get("identifier");
                assessmentChildExpressionDto.setVariable(attribute.getValue().toString());
            } else if (childExpression.getType() == ExpressionType.BASE_VALUE) {
                BaseValue baseValue = (BaseValue) childExpression;
                assessmentChildExpressionDto.setBaseValue(Double.parseDouble(baseValue.getSingleValue().toQtiString()));
            }
        }
        return assessmentChildExpressionDto;
    }

    @Override
    public Expression disassembleDto(AssessmentChildExpressionDto assessmentChildExpressionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
