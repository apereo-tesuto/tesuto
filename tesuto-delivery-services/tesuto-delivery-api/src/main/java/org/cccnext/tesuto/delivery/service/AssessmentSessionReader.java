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

import java.util.Map;

import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;

public interface AssessmentSessionReader {
	
	 /**
     * Retrieve an assessmentSessionId. The session is just intended for
     * serialization so the method is untyped to avoid pulling the whole
     * assessment session view model out of the delivery module.
     *
     * @param assessmentSessionId
     * @return The session (as an object)
     * @throws AssessmentSessionNotFoundException
     */
	 public Object findAssessmentSession(String assessmentSessionId) throws AssessmentSessionNotFoundException;


	 /**
	     * Create a generic assessment session. Is not persisted.
	     *
	     * @param namespace
	     * @param identifier
	     * @return
	     */
	 public Object createContentAssessmentSession(String namespace, String assessmentId);
	 
	 public String createUserAssessmentSession(String userId,
             ScopedIdentifier scopedIdentifer, DeliveryType deliveryType, Map<String, String> settings);
	 
	 public String createUserAssessmentSession(String userId, ScopedIdentifier assessmentId, int assessmentVersion,
             DeliveryType deliveryType,
             Map<String, String> settings);
	 
	 public Integer getAssessmentVersionForSession(String assessmentSessionId);


}
