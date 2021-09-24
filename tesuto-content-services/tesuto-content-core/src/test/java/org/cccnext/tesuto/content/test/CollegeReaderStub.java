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
package org.cccnext.tesuto.content.test;

import java.util.Set;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;

public class CollegeReaderStub implements CollegeReader {

	@Override
	public CollegeViewDto getCollegeByMisCode(String misCode) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CollegeDto read(String misCode) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public Set<CollegeViewDto> read(Set<String> misCodes) {
		//Auto-generated method stub
		return null;
	}

}
