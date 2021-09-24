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
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto;
import org.cccnext.tesuto.content.model.metadata.PrerequisiteMetadata;
import org.cccnext.tesuto.content.model.metadata.SectionMetadata;
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
public class SectionMetadataDtoAssemblerImplTest {

    @Autowired
    SectionMetadataDtoAssembler sectionMetadataDtoAssembler;

    public static SectionMetadata getSectionMetadata() {
        SectionMetadata sectionMetadata = new SectionMetadata();
        sectionMetadata.setIdentifier("section identifier");
        sectionMetadata.setType("section type");
        sectionMetadata.setCompetencyMapDiscipline("competency map discipline");
        return sectionMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        SectionMetadata sectionMetadata = getSectionMetadata();
        SectionMetadataDto sectionMetadataDtoAssembled = sectionMetadataDtoAssembler.assembleDto(sectionMetadata);
        SectionMetadata sectionMetadataDisassembled = sectionMetadataDtoAssembler
                .disassembleDto(sectionMetadataDtoAssembled);
        assertEquals("SectionMetadata incorrectly assembled, dissassembled", sectionMetadata,
                sectionMetadataDisassembled);
    }
}
