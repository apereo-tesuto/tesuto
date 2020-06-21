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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cccnext.tesuto.admin.dto.AccommodationDto;
import org.cccnext.tesuto.admin.model.Accommodation;
import org.junit.Before;
import org.junit.Test;

public class AccommodationDtoAssemblerTest {

    @Test
    public void testAssembleDisassemble() {
        AccommodationDtoAssembler accommodationDtoAssembler = new AccommodationDtoAssemblerImpl();
        Accommodation accommodation = getAccommodationModel();
        AccommodationDto assembledAccommodationDto = accommodationDtoAssembler.assembleDto(accommodation);
        Accommodation disassembledAccommodation = accommodationDtoAssembler.disassembleDto(assembledAccommodationDto);
        assertEquals("Accommodation was not Assembled and Dissassambled properly", accommodation,
                disassembledAccommodation);
        assertTrue("Accommodation was not Assembled and Dissassambled properly",
                accommodation.equals(disassembledAccommodation));
    }

    private Accommodation getAccommodationModel() {
        Accommodation accommodation = new Accommodation();
        accommodation.setCode("code");
        accommodation.setDescription("description");
        accommodation.setId(1);
        accommodation.setName("name");
        return accommodation;
    }

    @Before
    public void before() {

    }

}
