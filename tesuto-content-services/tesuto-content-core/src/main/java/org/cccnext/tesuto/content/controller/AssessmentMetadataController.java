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
package org.cccnext.tesuto.content.controller;

import org.cccnext.tesuto.activation.service.ActivationReader;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.activation.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 2/11/16.
 */
@Service
public class AssessmentMetadataController {

    @Autowired
    private ActivationReader activationService;

    @Autowired
    private AssessmentService assessmentService;

    public ResponseEntity<?> getMetadataById(String currentUserName, String activationId) {
        Activation activation = activationService.find(activationId);
        //Note: this is secure as long a students can not be created through client with out adding a suffix
        //Currently this is impossible and not expected to change.
        if (!activation.getUserId().equals(currentUserName)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        AssessmentMetadataDto assessmentMetadataDto = assessmentService
                .readLatestPublishedVersionMetadata(activation.getAssessmentScopedIdentifier());
        return new ResponseEntity<AssessmentMetadataDto>(assessmentMetadataDto, HttpStatus.OK);
    }
}
