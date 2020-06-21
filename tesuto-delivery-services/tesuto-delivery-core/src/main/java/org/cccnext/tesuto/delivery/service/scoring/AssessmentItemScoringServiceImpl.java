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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.item.AssessmentCorrectResponseDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.view.ItemSessionResponseViewDto;


import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("assessmentItemScoringService")
public class AssessmentItemScoringServiceImpl implements AssessmentItemScoringService {

    public Float scoreAssessmentItem(AssessmentResponseVarDto responseVar,
            AssessmentResponseProcessingDto responseProcessing, ItemSessionResponseViewDto candidateResponse) {
        return scoreAssessmentItem(responseVar, responseProcessing, candidateResponse.getValues());
    }

    public Float scoreAssessmentItem(AssessmentResponseVarDto responseVar,
            AssessmentResponseProcessingDto responseProcessing, Response candidateResponse) {
        return scoreAssessmentItem(responseVar, responseProcessing, candidateResponse.getValues());
    }

    public Float scoreAssessmentItem(AssessmentResponseVarDto responseVar,
            AssessmentResponseProcessingDto responseProcessing, List<String> candidateValues) {

        float rawScore = 0;

        if (candidateValues.isEmpty()) {
            log.debug("Candidate Response is empty. No Scoring is possible. Returning null.");

            return null;
        } else {
            // Determine Response Processing Template
            // Accept Response Processing: Match Correct, Map Response
            if (responseProcessing == null) {
                return null;
            }
            String templateUri = responseProcessing.getTemplate();
            if (StringUtils.isBlank(templateUri)) {
                return null;
            }
            String template = getLastPathSegment(templateUri);

            if (StringUtils.isBlank(template)) {
                return null;
            }
            switch (template.toLowerCase()) {
            case "match_correct":
                rawScore = scoreMatchCorrect(responseVar.getCorrectResponse(), candidateValues);
                break;
            case "map_response":
                rawScore = scoreMapResponse(responseVar.getMapping(), candidateValues);
                break;
            default:
                log.error("Unsupported Scoring Template:" + template.toLowerCase());
                throw new UnsupportedScoringTemplateException(
                        "Scoring template " + template.toLowerCase() + " is not supported.");

            }
        }

        // Upper bound / lower bound?

        return rawScore;
    }

    // Match Correct Template
    // Contains logic from:
    // http://www.imsglobal.org/question/qti_v2p2/rptemplates/match_correct
    // Returns 1 if all responses match the correct list, else 0.
    private float scoreMatchCorrect(AssessmentCorrectResponseDto correctResponse, List<String> candidateValues) {
        float matchCorrectScore = 0;

        if (correctResponse == null || correctResponse.getValues() == null) {
            return 0;
        }
        Set<String> correctSet = new HashSet<>(correctResponse.getValues());
        Set<String> candidates = new HashSet<>(candidateValues);
        if (correctSet.equals(candidates)) {
            matchCorrectScore = 1;
        } else {
            matchCorrectScore = 0;
        }

        return matchCorrectScore;
    }

    // Map Response Template
    // Contains logic from:
    // http://www.imsglobal.org/question/qti_v2p2/rptemplates/map_response
    // Returns 1 if all responses match the correct list, else 0.
    private float scoreMapResponse(AssessmentItemResponseMappingDto correctResponse, List<String> candidateValues) {
        float matchCorrectScore = 0;

        if (correctResponse == null || correctResponse.getMapping() == null) {
            return 0;
        }

        Map<String, Double> mapping = correctResponse.getMapping();

        for (String candidateResponseValue : candidateValues) {
            if (mapping.containsKey(candidateResponseValue)) {
                matchCorrectScore += mapping.get(candidateResponseValue).floatValue();
            }
        }

        if (correctResponse.getUpperBound() != null
                && matchCorrectScore > correctResponse.getUpperBound().floatValue()) {
            matchCorrectScore = correctResponse.getUpperBound().floatValue();
        }

        if (correctResponse.getLowerBound() != null
                && matchCorrectScore < correctResponse.getLowerBound().floatValue()) {
            matchCorrectScore = correctResponse.getLowerBound().floatValue();
        }

        return matchCorrectScore;
    }

    private String getLastPathSegment(String uri) {
        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }

}
