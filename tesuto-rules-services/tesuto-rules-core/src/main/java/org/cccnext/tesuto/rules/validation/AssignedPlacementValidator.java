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
package org.cccnext.tesuto.rules.validation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.exceptions.InvalidRuleSetResults;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class AssignedPlacementValidator {
    public void validateExpectedResults(List<List<RulesAction>> actionsSet, List<Object> expectedResults) {
        int index = 0;
        int testNumber = 0;
        List<AssignedPlacementError> placementErrors = new ArrayList<>();
        for (List<RulesAction> actions : actionsSet) {
            PlacementViewDto expectedResult = (PlacementViewDto) expectedResults.get(index++);
            AssignedPlacementError placementError = new AssignedPlacementError();
            placementError.setTestNumber(++testNumber);
            if (actions.size() != 1 && expectedResult != null) {
                placementError.setExpectedNumberOfActions(1);
                placementError.setNumberOfActions(actions.size());
                placementErrors.add(placementError);
                continue;
            } else if (actions.size() == 0 && expectedResult == null) {
                // expected no rule to be completed
                continue;
            } else if (actions.size() > 1) {
                placementError.setExpectedNumberOfActions(1);
                placementError.setNumberOfActions(actions.size());
                placementErrors.add(placementError);
                continue;
            }

            RulesAction action = actions.get(0);
            Map<String, Object> attributes = action.getActionParameters();
            // test for action name?

            PlacementViewDto placement = (PlacementViewDto) attributes.get("assignedPlacement");
            LocalDateTime now = LocalDateTime.now();
            if (placement.getAssignedDate().after(Date.from(now.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant()))
                    && placement.getAssignedDate().before(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))) {
                expectedResult.setAssignedDate(placement.getAssignedDate()); // close enough? Let's not fail our equals check
            }
            if (!placement.equals(expectedResult)) {
                placementError.setActualPlacement(placement);
                placementError.setExpectedPlacement(expectedResult);
                placementErrors.add(placementError);
            }
        }
        if(placementErrors.size() != 0) {
            List<String> ers = placementErrors.stream().map(e -> e.toString()).collect(Collectors.toList());
            throw new InvalidRuleSetResults("Unable to validate assigned placement", "", ers);
        }
    }
}
