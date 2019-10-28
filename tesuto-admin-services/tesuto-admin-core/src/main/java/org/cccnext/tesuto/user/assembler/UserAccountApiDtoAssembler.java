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
package org.cccnext.tesuto.user.assembler;

import org.cccnext.tesuto.admin.dto.UserAccountApiDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.domain.assembler.DtoAssembler;
import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.model.UserAccountApi;
import org.cccnext.tesuto.user.service.UserAccountExistsException;

import java.util.List;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface UserAccountApiDtoAssembler extends DtoAssembler<UserAccountApiDto, UserAccountApi> {

    UserAccountApiDto create(UserAccountApiDto userAccountApiDto);

    UserAccountApiDto update(UserAccountApiDto userAccountApiDto);

    UserAccountApiDto readDtoByUsername(String username);

    UserAccountApiDto readDtoById(String userAccountApiId);

    List<UserAccountApiDto> readAll();

    @Override
    UserAccountApiDto assembleDto(UserAccountApi userAccountApi);

    @Override
    UserAccountApi disassembleDto(UserAccountApiDto userAccountApiDto);
}
