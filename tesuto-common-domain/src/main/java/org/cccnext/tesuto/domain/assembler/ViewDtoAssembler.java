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
package org.cccnext.tesuto.domain.assembler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ViewDtoAssembler<ViewDto, Dto> {

    public ViewDto assembleViewDto(Dto Dto);

    public Dto disassembleViewDto(ViewDto dto);

    public default Set<ViewDto> assembleViewDto(final Set<Dto> dtos) {
        if (dtos == null)
            return null;
        Set<ViewDto> viewDtos = dtos.stream().map((Dto dto) -> {
            return assembleViewDto(dto);
        }).collect(Collectors.toSet());
        return viewDtos;
    }

    public default List<ViewDto> assembleViewDto(final List<Dto> dtos) {
        if (dtos == null)
            return null;
        List<ViewDto> viewDtos = dtos.stream().map((Dto Dto) -> {
            return assembleViewDto(Dto);
        }).collect(Collectors.toList());
        return viewDtos;
    }

    public default Set<Dto> disassembleViewDto(final Set<ViewDto> viewDtos) {
        if (viewDtos == null)
            return null;
        Set<Dto> dtos = viewDtos.stream().map((ViewDto viewDto) -> {
            return disassembleViewDto(viewDto);
        }).collect(Collectors.toSet());
        return dtos;
    }

    public default List<Dto> disassembleViewDto(final List<ViewDto> viewDtos) {
        if (viewDtos == null)
            return null;
        List<Dto> dtos = viewDtos.stream().map((ViewDto viewDto) -> {
            return disassembleViewDto(viewDto);
        }).collect(Collectors.toList());

        return dtos;
    }

}
