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
package org.cccnext.tesuto.delivery.service;

import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.delivery.model.internal.ActivityLog;
import org.cccnext.tesuto.delivery.repository.mongo.ActivityLogRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ActivityLogService {

    @Autowired
    MongoOperations mongoOps;

    @Autowired
    ActivityLogRepository activityLogRepository;

    private final String COLLECTION_NAME = "ActivityLog";

    public void addDocuments(List<Map<String, Object>> jsonMaps) {
        mongoOps.insert(jsonMaps, COLLECTION_NAME);
    }

    public List<ActivityLog> readActivityLogByUserId(String userId) {
        return activityLogRepository.findByUserId(userId);
    }

    public List<ActivityLog> readActivityLogByAsessmentSessionId(String assessmentSessionId) {
        return activityLogRepository.findByAsId(assessmentSessionId);
    }
}
