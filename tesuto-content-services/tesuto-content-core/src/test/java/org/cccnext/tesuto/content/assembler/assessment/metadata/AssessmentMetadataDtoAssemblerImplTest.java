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
package org.cccnext.tesuto.content.assembler.assessment.metadata;

import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.model.metadata.AssessmentMetadata;
import org.cccnext.tesuto.content.model.metadata.PrerequisiteMetadata;
import org.cccnext.tesuto.content.model.metadata.SectionMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentMetadataDtoAssemblerImplTest {

    @Autowired
    AssessmentMetadataDtoAssembler metadataDtoAssembler;

    /*
     * Currently AssessmentDto and AssessmenentItem use the same metadata model.
     */
    public static AssessmentMetadata getMetadata() {
        AssessmentMetadata assessmentMetadata = new AssessmentMetadata();
        assessmentMetadata.setIdentifier("i001");
        assessmentMetadata.setType("assessmentmetadata");
        assessmentMetadata.setAuthor("author");
        assessmentMetadata.setAuthoringTool("LSI");
        assessmentMetadata.setAuthoringToolVersion("5.2");
        assessmentMetadata.setAvailable("No");
        assessmentMetadata.setDisplayInHistory("Yes");
        assessmentMetadata.setDisplayGeneralClosing("No");
        assessmentMetadata.setDisplayGeneralInstructions("Yes");
        assessmentMetadata.setAutoActivate("No");
        assessmentMetadata.setScaleMultiplicativeTerm(65.0d);
        assessmentMetadata.setScaleAdditiveTerm(1000.01d);
        assessmentMetadata.setCompetencyMapDisciplines(Arrays.asList("ESL", "ENGLISH"));
        PrerequisiteMetadata prerequisiteMetadata = PrerequisiteMetadataAssemblerDtoImplTest.getPrerequisiteMetadata();
        assessmentMetadata.setPreRequisite(prerequisiteMetadata);
        assessmentMetadata.setInstructions("Take the test");
        assessmentMetadata.setDeliveryType(DeliveryTypeMetadataAssemblerDtoImplTest.getDeliveryTypeMetadata());
        SectionMetadata sectionMetadata = SectionMetadataDtoAssemblerImplTest.getSectionMetadata();
        List<SectionMetadata> sectionMetadataList = Arrays.asList(sectionMetadata);
        assessmentMetadata.setSection(sectionMetadataList);
        assessmentMetadata.setGenerateAssessmentPlacement("True");
        return assessmentMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentMetadata assessmentMetadata = getMetadata();
        AssessmentMetadataDto assessmentMetadataDtoAssembled = metadataDtoAssembler.assembleDto(assessmentMetadata);
        AssessmentMetadata assessmentMetadataDtoDisassembled = metadataDtoAssembler
                .disassembleDto(assessmentMetadataDtoAssembled);
        assertEquals("AssessmentMetadata incorrectly assembled, dissassembled", assessmentMetadata,
                assessmentMetadataDtoDisassembled);
    }
}
