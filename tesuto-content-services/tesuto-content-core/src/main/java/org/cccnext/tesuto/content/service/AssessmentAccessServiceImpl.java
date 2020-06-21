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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.content.model.AssessmentAccess;
import org.cccnext.tesuto.content.model.AssessmentAccessId;
import org.cccnext.tesuto.content.model.AssessmentAccessImpl;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.repository.jpa.AssessmentAccessRepository;
import org.cccnext.tesuto.content.service.AssessmentAccessService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value ="assessmentAccessService")
public class AssessmentAccessServiceImpl implements AssessmentAccessService {

    @Autowired
    private AssessmentAccessRepository repository;

    @Transactional(readOnly = true)
    public Set<ScopedIdentifier> getAllowedAssessments(String userId, String locationId) {
        Integer location = Integer.parseInt(locationId);
        List<AssessmentAccessImpl> accessibleActivations = repository.findByUserIdAndLocationIdAndActive(userId,
                location, true);

        Set<ScopedIdentifier> scopedIdentfiers = new HashSet<ScopedIdentifier>();
        for (AssessmentAccess assessibleActivation : accessibleActivations) {
            if (assessibleActivation.getStartDate() != null) {
                if (assessibleActivation.getStartDate().after(DateTime.now())) {
                    continue;
                }
            }

            if (assessibleActivation.getEndDate() != null) {
                if (assessibleActivation.getEndDate().before(DateTime.now())) {
                    continue;
                }
            }
            scopedIdentfiers.add(assessibleActivation.getScopedIdentifier());
        }
        return scopedIdentfiers;

    }

    @Transactional
    public void create(AssessmentAccess assessmentAccess) {
        repository.save(new AssessmentAccessImpl(assessmentAccess));
    }

    @Transactional
    public void delete(AssessmentAccess assessmentAccess) {
        repository.delete(new AssessmentAccessImpl(assessmentAccess));
    }

    @Transactional
    public void delete(AssessmentAccessId assessmentAccessId) {
        repository.deleteById(assessmentAccessId);
    }

    @Transactional
    public Collection<? extends AssessmentAccess> findByLocationsAndUserIds(Set<TestLocationDto> locations, Set<String> userIds) {
    	Set<String> locationIds= locations.stream().map(l -> l.getId()).collect(Collectors.toSet());
        return repository.findByLocationIdInAndUserIdIn(locationIds, userIds);
    }

}
