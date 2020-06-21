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
package org.cccnext.tesuto.importer.qti.service.validate;

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.service.importer.validate.ValidateItemMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value="validateItemMetadataService")
public class ValidateItemMetadataServiceImpl implements ValidateItemMetadataService {

    @Override
    public List<ValidationMessage> validateMetadataMapKeysMatchItemIdentifiers(HashMap<String, ItemMetadataDto> metadataDtoHashMap, List<AssessmentItemDto> itemDtos) {
        List<ValidationMessage> warnings = new ArrayList<>();
        itemDtos.forEach(i-> metadataDtoHashMap.remove(i.getIdentifier()));
        if(metadataDtoHashMap != null && !metadataDtoHashMap.isEmpty()){
            metadataDtoHashMap.forEach((identifier, metadata) -> {
                String validationWarningMessage = String.format("There is not a matching item for the itemMetadata with the identifier: %s", identifier);
                log.warn(validationWarningMessage);
                warnings.add(createItemMetadataValidationMessage(validationWarningMessage, "identifier"));
            });
        }
        return warnings;
    }

    private ValidationMessage createItemMetadataValidationMessage(String message, String node){
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setMessage(message);
        validationMessage.setFileType(ValidationMessage.FileType.ITEM_METADATA);
        validationMessage.setNode(node);
        return validationMessage;
    }
}
