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
package org.cccnext.tesuto.web.controller.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.web.model.DashboardSession;
import org.cccnext.tesuto.web.service.DashboardService;
import org.cccnext.tesuto.web.service.UrlService;
import org.codehaus.jackson.map.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Controller
@RequestMapping(value = "/status")
public class StatusUIController extends BaseController {


    @Autowired
    private UrlService urlService;

    @Autowired
    DashboardService dashboardService;

    @Value("${spring.profiles.active}")
    String activeSpringProfiles;
     
    final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> IOExceptionHandler(IOException ex) {
        return new ResponseEntity<>(error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_SYSTEM_DASHBOARD')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String rootContext(Model model) throws IOException {
        DashboardSession session = dashboardService.getDashboardSession(isLocalProfile());
        
        addJSONToModelOrError(model, "sessionConfigs", session);
        urlService.addBaseUrls(model);
        return "/StatusDashboard";
    }

    private boolean isLocalProfile() {
        return Arrays.asList(activeSpringProfiles.split(",")).contains("local");
    }

    private List<String> addJSONToModelOrError(Model model, String attributeName, Object payload) {
        List<String> errors = new ArrayList<>();

        String payloadString = "";
        try {
            payloadString = mapper.writeValueAsString(payload);
        } catch (IOException e) {
            log.error("An error occurred while attempting to serialize an object to JSON.", e);
            errors.add("An exception occurred while attempting to serialize " + attributeName + " to JSON.");
        }

        model.addAttribute(attributeName, payloadString);
        return errors;
    }

    @PreAuthorize("hasAnyAuthority('VIEW_SYSTEM_DASHBOARD')")
    @RequestMapping(value = "/queue/{name}", method = RequestMethod.DELETE)
    public void purgeMessagesFromSQSQueue(HttpServletResponse response,
                                          @PathVariable("name") String queueName) {
        response.setStatus(dashboardService.purgeMessagesFromSQSQueue(queueName));
    }

    @PreAuthorize("hasAnyAuthority('VIEW_SYSTEM_DASHBOARD')")
    @RequestMapping(value = "/cache/", method = RequestMethod.DELETE)
    public void purgeStudentSearchKeysFromRedis(HttpServletResponse response) {
        dashboardService.purgeStudentSearchKeysFromRedis();
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
