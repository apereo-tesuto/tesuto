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
package org.cccnext.tesuto.admin.service;

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

public interface TestLocationService extends TestLocationReader {

    List<TestLocationDto> read();

    TestLocationDto read(String id);

    Set<TestLocationDto> read(Set<String> ids);

    TestLocationDto create(TestLocationDto testLocationDto);

    void delete(String id);

    List<String> validateTestLocation(TestLocationDto testLocationDto);

    TestLocationDto createTestLocationWithAssessments(TestLocationDto testLocationDto, Set<ScopedIdentifierDto> assessmentIdentifiers);

    TestLocationDto editTestLocationWithAssessments(String testLocationId, TestLocationDto testLocationDto, Set<ScopedIdentifierDto> assessments);

    TestLocationDto upsert(TestLocationDto testLocationDto);

    void enableTestLocation(String testLocationId, boolean enabled);

	Set<TestLocation> readModels(Set<String> ids);
}
