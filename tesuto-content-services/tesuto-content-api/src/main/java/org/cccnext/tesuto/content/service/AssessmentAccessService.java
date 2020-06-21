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
package org.cccnext.tesuto.content.service;

import java.util.Collection;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.content.model.AssessmentAccess;
import org.cccnext.tesuto.content.model.AssessmentAccessId;
import org.cccnext.tesuto.content.model.ScopedIdentifier;

public interface AssessmentAccessService {

    Set<ScopedIdentifier> getAllowedAssessments(String userId, String locationId);
    Collection<? extends AssessmentAccess> findByLocationsAndUserIds(Set<TestLocationDto> locations, Set<String> userIds);

    void create(AssessmentAccess assessmentAccess);

    void delete(AssessmentAccess assessmentAccess);

    void delete(AssessmentAccessId assessmentAccessId);

}
