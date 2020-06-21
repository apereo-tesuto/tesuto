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
package org.cccnext.tesuto.content.assembler.item.metadata;

import org.cccnext.tesuto.content.dto.item.metadata.ToolsItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.ToolsItemMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 1/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class ToolsItemMetadataDtoAssemblerImplTest {

    @Autowired
    ToolsItemMetadataDtoAssembler toolsItemMetadataDtoAssembler;

    public static ToolsItemMetadata getToolsItemMetadata() {
        ToolsItemMetadata toolsItemMetadata = new ToolsItemMetadata();
        toolsItemMetadata.setAllowCalculator("Yes");
        toolsItemMetadata.setAllowDictionary("No");
        toolsItemMetadata.setAllowThesaurus("Maybe");
        return toolsItemMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        ToolsItemMetadata toolsItemMetadata = getToolsItemMetadata();
        ToolsItemMetadataDto toolsItemMetadataDtoAssembled = toolsItemMetadataDtoAssembler
                .assembleDto(toolsItemMetadata);
        ToolsItemMetadata toolsItemMetadataDisassembled = toolsItemMetadataDtoAssembler
                .disassembleDto(toolsItemMetadataDtoAssembled);
        assertEquals("ToolsItemMetadata incorrectly assembled, dissassembled", toolsItemMetadata,
                toolsItemMetadataDisassembled);
    }
}
