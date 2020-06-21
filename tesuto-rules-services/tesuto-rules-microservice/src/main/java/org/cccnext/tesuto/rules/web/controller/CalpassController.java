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
package org.cccnext.tesuto.rules.web.controller;

import org.cccnext.tesuto.multiplemeasures.CalpassService;
import org.cccnext.tesuto.multiplemeasures.dto.CalpassRequest;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping(value = "/service/v1/calpass")
public class CalpassController {

  private CalpassService service;

  @Value("${calpass.service.property.source}") private String propertySource;
  @Value("${aws.multiple.measure.request.queue.url}") private String requestQueueUrl;
  @Autowired private OperationalDataStoreService ods;

  @PostConstruct
  public void init() throws Exception {
    service = new CalpassService(propertySource);
    service.setRequestQueueUrl(requestQueueUrl);
    service.setOperationalDataStoreService(ods);
  }


  @RequestMapping(value = "", method = RequestMethod.POST)
  @PreAuthorize("hasAuthority('API')")
  public ResponseEntity<?> post(@RequestBody CalpassRequest request, UriComponentsBuilder uriBuilder) throws Exception {
    service.handleRequest(request.getMisCode(), request.getCccid(), request.getSsid());
    HttpHeaders headers = new HttpHeaders();
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
}
