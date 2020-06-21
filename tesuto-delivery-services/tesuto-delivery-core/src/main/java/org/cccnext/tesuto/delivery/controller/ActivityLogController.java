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
package org.cccnext.tesuto.delivery.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.delivery.model.internal.ActivityLog;
import org.cccnext.tesuto.delivery.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bruce on 2/16/16.
 */

@Service
public class ActivityLogController {

    @Autowired
    ActivityLogService service;


    public ResponseEntity<Void> post(String userId, List<Map<String, Object>> jsonMaps) {
        String timeNow = new Long(System.currentTimeMillis()).toString();
        jsonMaps.forEach(map -> {
            map.put("time", timeNow);
            if (userId != null) {
                map.put("userId", userId);
            }
        });
        service.addDocuments(jsonMaps);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public @ResponseBody List<ActivityLog> activityLogView(@RequestParam(required = false) String userId, @RequestParam(required = false) String assessmentSessionId) {
        List<ActivityLog> activityLogList = new LinkedList<>(); // null object pattern
        if (userId != null) {
            activityLogList = service.readActivityLogByUserId(userId);
        } else if (assessmentSessionId != null) {
            activityLogList = service.readActivityLogByAsessmentSessionId(assessmentSessionId);
        }
        return activityLogList;
    }
}
