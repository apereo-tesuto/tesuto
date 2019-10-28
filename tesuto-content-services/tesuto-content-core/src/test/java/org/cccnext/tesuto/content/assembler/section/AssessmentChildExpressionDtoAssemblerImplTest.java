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

import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentChildExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 2/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentChildExpressionDtoAssemblerImplTest {

    public static AssessmentChildExpression getChildExpression() {
        AssessmentChildExpression assessmentChildExpression = new AssessmentChildExpression();

        assessmentChildExpression.setExpressionType(ExpressionType.GTE);
        assessmentChildExpression.setBaseValue(2.0d);
        assessmentChildExpression.setVariable("a001_testlet.Score");
        return assessmentChildExpression;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentChildExpressionDtoAssembler assessmentChildExpressionDtoAssembler = new AssessmentChildExpressionDtoAssemblerImpl();
        AssessmentChildExpression assessmentChildExpression = getChildExpression();
        AssessmentChildExpressionDto assessmentChildExpressionDto = assessmentChildExpressionDtoAssembler
                .assembleDto(assessmentChildExpression);
        AssessmentChildExpression assessmentChildExpressionDisassembled = assessmentChildExpressionDtoAssembler
                .disassembleDto(assessmentChildExpressionDto);
        assertEquals("AssessmentChildExpression incorrectly assembled, dissassembled", assessmentChildExpression,
                assessmentChildExpressionDisassembled);
    }
}
