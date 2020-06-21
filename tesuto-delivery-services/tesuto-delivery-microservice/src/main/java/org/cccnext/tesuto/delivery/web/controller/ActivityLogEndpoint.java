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
package org.cccnext.tesuto.delivery.web.controller;

import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.delivery.controller.ActivityLogController;
import org.cccnext.tesuto.delivery.model.internal.ActivityLog;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bruce on 2/16/16.
 */
@Controller
@RequestMapping(value = "/service/v1/activityLog")
public class ActivityLogEndpoint extends BaseController {

	@Autowired
    ActivityLogController controller;

    @PreAuthorize("hasAuthority('CREATE_ACTIVITY_LOG_ENTRY')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> post(@RequestBody List<Map<String, Object>> jsonMaps) {
        return controller.post(getCurrentUserId(), jsonMaps);
    }

    @PreAuthorize("hasAuthority('VIEW_ACTIVITY_LOG')")
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public @ResponseBody List<ActivityLog> activityLogView(@RequestParam(required = false) String userId, @RequestParam(required = false) String assessmentSessionId) {
    	 return controller.activityLogView(userId, assessmentSessionId);
    }
}
