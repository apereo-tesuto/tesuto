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
package org.cccnext.tesuto.preview.service;

import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.importer.qti.service.AssessmentQtiImportService;
import org.cccnext.tesuto.importer.qti.service.QtiResourceRelocator;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CacheUploadService extends BaseUploadService {

    @Autowired
    AssessmentQtiImportService assessmentQtiImportService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    AssessmentItemService assessmentItemService;

    @Autowired
    AssessmentSessionReader deliveryService;

    @Autowired
    @Qualifier("resourceRelocatorEncoded")
    private QtiResourceRelocator resourceRelocator;

    @Override
    protected QtiResourceRelocator getResourceRelocator() {
        return resourceRelocator;
    }

    @Override
    protected AssessmentItemService getAssessmentItemService() {
        return assessmentItemService;
    }


    @Override
    protected AssessmentService getAssessmentService() {
        return assessmentService;
    }

	@Override
	protected AssessmentSessionReader getDeliveryService() {
		return deliveryService;
	}
}
