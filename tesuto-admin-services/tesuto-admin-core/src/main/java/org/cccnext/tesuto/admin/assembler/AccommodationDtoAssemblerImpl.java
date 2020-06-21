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
package org.cccnext.tesuto.admin.assembler;

import org.cccnext.tesuto.admin.dto.AccommodationDto;
import org.cccnext.tesuto.admin.model.Accommodation;
import org.springframework.stereotype.Component;

@Component(value = "accommodationDtoAssembler")
public class AccommodationDtoAssemblerImpl implements AccommodationDtoAssembler {

    @Override
    public AccommodationDto assembleDto(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setCode(accommodation.getCode());
        accommodationDto.setDescription(accommodation.getDescription());
        accommodationDto.setId(accommodation.getId());
        accommodationDto.setName(accommodation.getName());
        return accommodationDto;
    }

    @Override
    public Accommodation disassembleDto(AccommodationDto accommodationDto) {
        if (accommodationDto == null) {
            return null;
        }
        Accommodation accommodation = new Accommodation();
        accommodation.setCode(accommodationDto.getCode());
        accommodation.setDescription(accommodationDto.getDescription());
        accommodation.setId(accommodationDto.getId());
        accommodation.setName(accommodationDto.getName());
        return accommodation;
    }

}
