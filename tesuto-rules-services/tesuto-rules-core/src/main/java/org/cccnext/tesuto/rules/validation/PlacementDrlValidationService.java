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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.PlacementRulesSourceDto;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.exceptions.InvalidRuleSetResults;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.services.DrlSyntaxValidator;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class PlacementDrlValidationService extends AbstractDrlValidationService {
    @Autowired
    private PlacementFactsParser placementFactsParser;

    @Autowired
    private PlacementValidator placementValidator;

    @Autowired
    private DrlSyntaxValidator syntaxValidator;

    public DrlValidationResults validate(DrlValidationData data) {
        DrlValidationResults results = syntaxValidator.validate(data.getDrl());

        if (!results.getIsValid() || !data.getCsvValidationRequired()) {
            return results;
        }

        results.setIsValid(false);
        Pair<List<Map<String,Object>>, List<Object>> factsSetAndResults =  Pair.of(new ArrayList<Map<String,Object>>(), new ArrayList<>());
        try {
            factsSetAndResults = placementFactsParser.getTestFactsExpectedResults(data.getValidationCsv());
        } catch (Exception exception) {
            Map<String,Object> facts = new HashMap<>();
            facts.put("PLACEMENT_COMPONENTS", new PlacementRulesSourceDto());
            facts.put("COMPETENCY_ATTRIBUTES", new CompetencyAttributesViewDto());
            facts.put("actions", new ArrayList<RulesAction>());
            factsSetAndResults.getLeft().add(facts);
            results.setExceptionMessage("Validation facts could not be parsed because" + exception.getMessage() + "\n");
            results.setExceptionTrace(exceptionStacktraceToString(exception));
            results.setIsValid(false);
        }

        List<List<RulesAction>> actions = new ArrayList<>();
        StatelessKieSession kSession = loadKieSession(data.getDrl());
        // kSession.addEventListener( new DebugAgendaEventListener() );
        // kSession.addEventListener( new DebugRuleRuntimeEventListener() );
        try {
            for(Map<String,Object> facts:factsSetAndResults.getLeft()) {
                kSession.execute(facts);
                actions.add((List<RulesAction>) facts.get("actions"));
            }
        } catch(Exception exception) {
            results.setIsValid(false);
            results.setExceptionMessage("Failed on execute \n");
            results.setExceptionTrace(exceptionStacktraceToString(exception));
            return results;
        }
        try {
            if(!factsSetAndResults.getRight().isEmpty()) {
                placementValidator.validateExpectedResults(actions, factsSetAndResults.getRight());
                results.setIsValid(true);
            }
        } catch(InvalidRuleSetResults exception) {
            results.setExceptionMessage(exception.getMessage());
            results.getErrors().addAll(exception.getErrors());
            results.setExceptionTrace(exceptionStacktraceToString(exception));
        } catch(Exception exception) {
            results.setExceptionMessage(exception.getMessage());
            results.setExceptionTrace(exceptionStacktraceToString(exception));
        }

        return results;
    }
}
