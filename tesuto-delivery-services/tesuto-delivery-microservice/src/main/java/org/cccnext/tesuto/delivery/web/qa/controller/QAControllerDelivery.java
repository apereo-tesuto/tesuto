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
package org.cccnext.tesuto.delivery.web.qa.controller;

import org.cccnext.tesuto.delivery.qa.DeliveryServicesModuleSeedDataService;
import org.cccnext.tesuto.springboot.web.BaseController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Controller
@RequestMapping(value = "/service/v1/qa")
public class QAControllerDelivery extends BaseController {

	@Autowired
	DeliveryServicesModuleSeedDataService service;

	@PreAuthorize("hasAuthority('CREATE_QA_OBJECTS')")
	@RequestMapping(method = RequestMethod.POST)
	public void seedData() throws Exception {
		try {
			service.seedData();
		} catch (Exception exp) {
			log.error("Loading seed Data failed", exp);
		}
	}

	@PreAuthorize("hasAuthority('API')")
	@RequestMapping(value = "oauth2", method = RequestMethod.POST)
	public ResponseEntity<Void> seedDataServer() throws Exception {
		try {
			service.seedData();
		} catch (Exception exp) {
			log.error("Loading seed Data failed", exp);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
