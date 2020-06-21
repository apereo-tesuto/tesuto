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

import org.cccnext.tesuto.content.dto.metadata.PrerequisiteMetadataDto;
import org.cccnext.tesuto.content.model.metadata.PrerequisiteMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 1/22/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class PrerequisiteMetadataAssemblerDtoImplTest {
    @Autowired
    PrerequisiteMetadataDtoAssembler prerequisiteMetadataDtoAssembler;

    public static PrerequisiteMetadata getPrerequisiteMetadata() {
        PrerequisiteMetadata prerequisiteMetadata = new PrerequisiteMetadata();
        prerequisiteMetadata.setAssessmentIdRef("acaisurvey100001");
        return prerequisiteMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        PrerequisiteMetadata prerequisiteMetadata = getPrerequisiteMetadata();
        PrerequisiteMetadataDto prerequisiteMetadataDtoAssembled = prerequisiteMetadataDtoAssembler
                .assembleDto(prerequisiteMetadata);
        PrerequisiteMetadata prerequisiteMetadataDisassembled = prerequisiteMetadataDtoAssembler
                .disassembleDto(prerequisiteMetadataDtoAssembled);
        assertEquals("PrerequisiteMetadata incorrectly assembled, dissassembled", prerequisiteMetadata,
                prerequisiteMetadataDisassembled);
    }
}
