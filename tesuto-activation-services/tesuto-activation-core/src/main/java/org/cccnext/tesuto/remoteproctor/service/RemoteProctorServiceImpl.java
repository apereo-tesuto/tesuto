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
package org.cccnext.tesuto.remoteproctor.service;

import org.cccnext.tesuto.admin.viewdto.RemoteProctorDto;
import org.cccnext.tesuto.remoteproctor.assembler.RemoteProctorDtoAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Service("remoteProctorService")
public class RemoteProctorServiceImpl implements RemoteProctorService {
	@Autowired RemoteProctorDtoAssembler remoteProctorDtoAssembler;

	@Override
	@Transactional(readOnly = true)
	public RemoteProctorDto readRemoteProctorByTestEventId(Integer testEventId) {
		RemoteProctorDto remoteProctorDto = remoteProctorDtoAssembler.readById(testEventId);
		return remoteProctorDto;
	}
}
