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
package org.cccnext.tesuto.activation.web.controller;

	import java.util.Set;

import org.cccnext.tesuto.activation.BatchActivationController;
import org.cccnext.tesuto.activation.ProtoActivation;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(value = "service/v1/activation-batch")
public class BatchActivationEndpoint extends BaseController {

    @Autowired
    private BatchActivationController controller;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('CREATE_BATCH_ACTIVATION')")
    public ResponseEntity<?> post(@RequestBody Set<ProtoActivation> activations, UriComponentsBuilder uriBuilder) {
    	return controller.post(getUser(),getCurrentUserId(), activations, uriBuilder);
    }
}
