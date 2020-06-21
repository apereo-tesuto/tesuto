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

import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentChildExpression;
import org.cccnext.tesuto.content.model.expression.AssessmentParentExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 2/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentParentExpressionDtoAssemblerImplTest {

    @Autowired
    AssessmentParentExpressionDtoAssembler parentExpressionDtoAssembler;

    public static AssessmentParentExpression getParentExpression() {
        AssessmentParentExpression assessmentParentExpression = new AssessmentParentExpression();
        assessmentParentExpression.setExpressionType(ExpressionType.AND);
        assessmentParentExpression.setAssessmentParentExpressionList(null);

        List<AssessmentChildExpression> assessmentChildExpressionList = new ArrayList<>(2);
        assessmentChildExpressionList.add(AssessmentChildExpressionDtoAssemblerImplTest.getChildExpression());
        assessmentChildExpressionList.add(AssessmentChildExpressionDtoAssemblerImplTest
                .getChildExpression()); /* add a second */
        assessmentParentExpression.setAssessmentChildExpressionList(assessmentChildExpressionList);

        return assessmentParentExpression;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentParentExpressionDtoAssembler assessmentParentExpressionDtoAssembler = new AssessmentParentExpressionDtoAssemblerImpl();
        AssessmentParentExpression assessmentParentExpression = getParentExpression();
        AssessmentParentExpressionDto assessmentParentExpressionDto = parentExpressionDtoAssembler
                .assembleDto(assessmentParentExpression);
        AssessmentParentExpression assessmentParentExpressionDisassembled = parentExpressionDtoAssembler
                .disassembleDto(assessmentParentExpressionDto);
        assertEquals("AssessmentParentExpression incorrectly assembled, dissassembled", assessmentParentExpression,
                assessmentParentExpressionDisassembled);
    }
}
