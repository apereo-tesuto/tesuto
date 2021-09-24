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

import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto;
import org.cccnext.tesuto.content.model.metadata.DeliveryTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * Created by jasonbrown on 3/17/16.
 */
@Component(value = "deliveryTypeMetadataDtoAssembler")
public class DeliveryTypeMetadataDtoAssemblerImpl implements DeliveryTypeMetadataDtoAssembler {

    @Override
    public DeliveryTypeMetadataDto assembleDto(DeliveryTypeMetadata deliveryTypeMetadata) {
        if (deliveryTypeMetadata == null) {
            return null;
        }
        DeliveryTypeMetadataDto deliveryTypeMetadataDto = new DeliveryTypeMetadataDto();
        deliveryTypeMetadataDto.setOnline(deliveryTypeMetadata.getOnline());
        deliveryTypeMetadataDto.setPaper(deliveryTypeMetadata.getPaper());
        return deliveryTypeMetadataDto;
    }

    @Override
    public DeliveryTypeMetadata disassembleDto(DeliveryTypeMetadataDto deliveryTypeMetadataDto) {
        if (deliveryTypeMetadataDto == null) {
            return null;
        }
        DeliveryTypeMetadata deliveryTypeMetadata = new DeliveryTypeMetadata();
        deliveryTypeMetadata.setOnline(deliveryTypeMetadataDto.getOnline());
        deliveryTypeMetadata.setPaper(deliveryTypeMetadataDto.getPaper());
        return deliveryTypeMetadata;
    }
}
