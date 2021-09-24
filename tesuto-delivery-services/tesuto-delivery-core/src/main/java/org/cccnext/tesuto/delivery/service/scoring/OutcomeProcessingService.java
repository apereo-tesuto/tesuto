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
package org.cccnext.tesuto.delivery.service.scoring;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 8/24/16.
 */
public interface OutcomeProcessingService {
    List<Outcome> processExternalResourceOutcomeDeclarations(AssessmentSession session);
    void processAssessmentSessionOutcome(AssessmentSession session, List<ItemSession> itemSessions);
    void calculateSessionOutcome(String outcomeIdentifier, AssessmentSession session, List<ItemSession> itemSessions);
}

