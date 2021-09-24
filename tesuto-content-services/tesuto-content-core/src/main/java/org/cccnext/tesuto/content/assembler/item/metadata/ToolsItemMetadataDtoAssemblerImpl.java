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
import org.springframework.stereotype.Component;

@Component(value = "toolsItemMetadataDtoAssembler")
public class ToolsItemMetadataDtoAssemblerImpl implements ToolsItemMetadataDtoAssembler {

    @Override
    public ToolsItemMetadataDto assembleDto(ToolsItemMetadata toolsItemMetadata) {
        if (toolsItemMetadata == null) {
            return null;
        }
        ToolsItemMetadataDto toolsItemMetadataDto = new ToolsItemMetadataDto();
        toolsItemMetadataDto.setAllowCalculator(toolsItemMetadata.getAllowCalculator());
        toolsItemMetadataDto.setAllowDictionary(toolsItemMetadata.getAllowDictionary());
        toolsItemMetadataDto.setAllowThesaurus(toolsItemMetadata.getAllowThesaurus());
        return toolsItemMetadataDto;
    }

    @Override
    public ToolsItemMetadata disassembleDto(ToolsItemMetadataDto toolsItemMetadataDto) {
        if (toolsItemMetadataDto == null) {
            return null;
        }
        ToolsItemMetadata toolsItemMetadata = new ToolsItemMetadata();
        toolsItemMetadata.setAllowCalculator(toolsItemMetadataDto.getAllowCalculator());
        toolsItemMetadata.setAllowDictionary(toolsItemMetadataDto.getAllowDictionary());
        toolsItemMetadata.setAllowThesaurus(toolsItemMetadataDto.getAllowThesaurus());
        return toolsItemMetadata;
    }

}
