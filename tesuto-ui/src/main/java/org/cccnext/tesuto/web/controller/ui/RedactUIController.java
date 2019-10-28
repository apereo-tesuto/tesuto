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

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/redact")
public class RedactUIController extends BaseController {

	@Value("${disable.assessments}")
    private boolean disableAssessments;
	
	@Override
	public UserAccountDto getUser() {
		return null;
	}

	@PreAuthorize("hasAuthority('VIEW_REDACT_ASSESSMENT_UI')")
	@RequestMapping(value = "upload", method = RequestMethod.GET)
	public String previewUpload(Model model) {
		if(disableAssessments) {
			return "NotAvailable";
		}
		model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
		return "RedactUpload";
	}
	
}
