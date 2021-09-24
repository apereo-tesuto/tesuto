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
package org.cccnext.tesuto.importer.qti.service.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentItemService;
import org.cccnext.tesuto.util.ValidationUtil;


import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "validateAssessmentItemService")
public class ValidateAssessmentItemServiceImpl implements ValidateAssessmentItemService {
    private final String MAP_RESPONSE = "map_response";
    private final String MATCH_CORRECT = "match_correct";
    private final int PRECISION = 3;

    public HashMap<String, SortedSet<Double>> processAssessmentItems(List<AssessmentItemDto> itemDtos) {
        HashMap<String, SortedSet<Double>> setMap = new HashMap<>(); // item id -> all possible scores for that item
        log.debug("Start Validation Items {}", itemDtos.size());
        for (AssessmentItemDto itemDto : itemDtos) {
            log.debug("Start Validation Item {}", itemDto.getIdentifier());
            SortedSet<Double> sortedSet = processItem(itemDto);
            if(CollectionUtils.isNotEmpty(sortedSet)) {
                setMap.put(itemDto.getIdentifier(), processItem(itemDto));
            }
            log.debug("End Validation Item {}", itemDto.getIdentifier());
        }
        log.debug("Finished Validation Items {}", itemDtos.size());
        return setMap;
    }

    private SortedSet<Double> processItem(AssessmentItemDto itemDto) {
        String template = (itemDto.getResponseProcessing() != null) ? (itemDto.getResponseProcessing().getTemplate() != null) ? itemDto.getResponseProcessing().getTemplate() : "" : "";
        SortedSet<Double> possibleScores = new TreeSet<>();
        if (CollectionUtils.isNotEmpty(itemDto.getResponseVars())) {
            for (AssessmentResponseVarDto assessmentResponseVarDto : itemDto.getResponseVars()) {
                AssessmentInteractionDto interactionDto = getMatchingInteractionForResponseVarDto(
                        assessmentResponseVarDto.getIdentifier(), itemDto.getInteractions());
                if (isMapResponse(assessmentResponseVarDto, template)) {
                    possibleScores = addScoresMappedResponse(assessmentResponseVarDto, interactionDto, possibleScores);
                } else if (isMatchCorrect(template)) {
                    possibleScores = addScoresMatchCorrect(possibleScores);
                } else if (isTextInteraction(interactionDto, template)) {
                    //No scoring at this time - Branch rules can not be evaluated based on a score from a text interaction.
                }
            }
        }
        possibleScores = evaluateMaxAndMinForOutcomeDeclaration(itemDto, possibleScores);

        return possibleScores;
    }

    private boolean isTextInteraction(AssessmentInteractionDto interactionDto, String template){
        if(template.isEmpty() && interactionDto instanceof AssessmentExtendedTextInteractionDto) {
            return true;
        }
        return false;
    }

    private boolean isMapResponse(AssessmentResponseVarDto assessmentResponseVarDto, String template){
        if(assessmentResponseVarDto.getMapping() != null && template.contains(MAP_RESPONSE)){
            return true;
        }
        return false;
    }

    private boolean isMatchCorrect(String template){
        if(!template.isEmpty() && template.contains(MATCH_CORRECT)){
            return true;
        }
        return false;
    }

    private SortedSet<Double> addScoresMappedResponse(AssessmentResponseVarDto assessmentResponseVarDto, AssessmentInteractionDto interactionDto,
            SortedSet<Double> possibleScores){
        if (interactionDto instanceof AssessmentChoiceInteractionDto) {
            List<Double> scores = new ArrayList<>(
                    assessmentResponseVarDto.getMapping().getMapping().values());
            possibleScores = addScoresChoiceInteractionMapResponse(interactionDto, possibleScores, scores);
        } else if ((interactionDto instanceof AssessmentTextEntryInteractionDto)
                || (interactionDto instanceof AssessmentInlineChoiceInteractionDto)) {
            List<Double> scores = new ArrayList<>(
                    assessmentResponseVarDto.getMapping().getMapping().values());
            possibleScores = addScoresTextEntryAndInlineChoiceInteraction(possibleScores, scores);
        } else if (interactionDto instanceof AssessmentMatchInteractionDto) {
            //TODO will add at later date
            //Fall through to add default value if it exists
        }
        possibleScores = evaluateMaxAndMinAndDefaultForResponseVar(assessmentResponseVarDto,
                possibleScores);
        return possibleScores;
    }

    private SortedSet<Double> addScoresChoiceInteractionMapResponse(AssessmentInteractionDto interactionDto,
            SortedSet<Double> possibleScores, List<Double> scores) {
        AssessmentChoiceInteractionDto choiceInteractionDto = (AssessmentChoiceInteractionDto) interactionDto;
        int maxChoices = choiceInteractionDto.getMaxChoices();
        int minChoices = choiceInteractionDto.getMinChoices();
        if(ValidationUtil.getUseFullBranchRuleEvaluation()) {
            List<List<Double>> superSet = getSubsets(scores, minChoices, maxChoices);
            return ValidationUtil.addPossibleScore(possibleScores, normalizeSet(superSet));
        }else {
            Collections.sort(scores); // lowest to highest
            SortedSet<Double> simpleSet = new TreeSet<>();
            simpleSet.add(getMinimum(scores, minChoices));
            simpleSet.add(getMaximum(scores, maxChoices));
            return ValidationUtil.addPossibleScore(possibleScores, simpleSet);
        }
    }

    private Double getMinimum(List<Double> sortedList, int minChoices){
        Double min = 0.0d;
        for(int i=0; i<minChoices; i++){
            min += sortedList.get(i);
        }
        return min;
    }

    private Double getMaximum(List<Double> sortedList, int maxChoices){
        Double max =0.0d;
        int maximum = sortedList.size() - maxChoices;
        for(int i=(sortedList.size()-1); i>=maximum; i--){
            max += sortedList.get(i);
        }
        return max;
    }

    private SortedSet<Double> addScoresTextEntryAndInlineChoiceInteraction(SortedSet<Double> possibleScores,
            List<Double> scores) {
        return addPossibleScoresList(possibleScores, scores);
    }

    private SortedSet<Double> addPossibleScoresList(SortedSet<Double> possibleScores, List<Double> scores) {
        if (possibleScores.isEmpty()) {
            return new TreeSet<>(scores);
        } else {
            return ValidationUtil.addPossibleScore(possibleScores, new TreeSet<>(scores));
        }
    }

    private SortedSet<Double> addScoresMatchCorrect(SortedSet<Double> possibleScores) {
        SortedSet<Double> matchCorretSet = new TreeSet<>();
        matchCorretSet.add(0d);
        matchCorretSet.add(1d);
        return ValidationUtil.addPossibleScore(possibleScores, matchCorretSet);
    }

    private SortedSet<Double> evaluateMaxAndMinAndDefaultForResponseVar(AssessmentResponseVarDto responseVarDto,
            SortedSet<Double> set) {
        if (responseVarDto == null || responseVarDto.getMapping() == null) {
            return set;
        }

        if (responseVarDto.getMapping().getUpperBound() != null && (set.isEmpty()
                || responseVarDto.getMapping().getUpperBound() < set.last())) {
            Double max = responseVarDto.getMapping().getUpperBound();
            set = removeMax(max, set);

            //Every score was higher than the upper bound
            if (set.isEmpty()) {
                set.add(max);
            }
        }

        if (responseVarDto.getMapping().getLowerBound() != null && (set.isEmpty()
                || responseVarDto.getMapping().getLowerBound() > set.first())) {
            Double min = responseVarDto.getMapping().getLowerBound();
            set = removeMin(min, set);

            //Every score was lower than the lower bound
            if (set.isEmpty()) {
                set.add(min);
            }
        }

        if (responseVarDto.getMapping().getDefaultValue() != null) {
            set.add(responseVarDto.getMapping().getDefaultValue());
        }

        return set;
    }

    private SortedSet<Double> evaluateMaxAndMinForOutcomeDeclaration(AssessmentItemDto itemDto, SortedSet<Double> set) {
        if (CollectionUtils.isNotEmpty(itemDto.getOutcomeDeclarationDtos())) {
            //TODO currently there is only one outcome var and we are assuming that this matches the identifier score
            //will need to modify this logic
            if (itemDto.getOutcomeDeclarationDtos().get(0).getNormalMaximum() != null && (set.isEmpty()
                    || itemDto.getOutcomeDeclarationDtos().get(0).getNormalMaximum() < set.last())) {
                Double max = itemDto.getOutcomeDeclarationDtos().get(0).getNormalMaximum();
                set = removeMax(max, set);

                if (set.isEmpty()) {
                    set.add(max);
                }
            }
            //TODO currently there is only one outcome var and we are assuming that this matches the identifier score
            //will need to modify this logic
            if (itemDto.getOutcomeDeclarationDtos().get(0).getNormalMinimum() != null && (set.isEmpty()
                    || itemDto.getOutcomeDeclarationDtos().get(0).getNormalMinimum() > set.first())) {
                Double min = itemDto.getOutcomeDeclarationDtos().get(0).getNormalMinimum();
                set = removeMin(min, set);
                if (set.isEmpty()) {
                    set.add(min);
                }
            }
        }
        return set;
    }

    private SortedSet<Double> removeMax(Double max, SortedSet<Double> set) {
        if(set.removeIf(p -> p.doubleValue() > max)){
            set.add(max);
        }
        return set;
    }

    private SortedSet<Double> removeMin(Double min, SortedSet<Double> set) {
        if(set.removeIf(p -> p.doubleValue() < min)){
            set.add(min);
        }
        return set;
    }

    // http://stackoverflow.com/questions/127704/algorithm-to-return-all-combinations-of-k-elements-from-n
    private static List<List<Double>> getCombinations(int k, List<Double> list) {
        List<List<Double>> combinations = new ArrayList<>();
        if (k == 0) {
            combinations.add(new ArrayList<>());
            return combinations;
        }
        for (int i = 0; i < list.size(); i++) {
            Double element = list.get(i);
            List<Double> rest = getSublist(list, i + 1);
            for (List<Double> previous : getCombinations(k - 1, rest)) {
                previous.add(element);
                combinations.add(previous);
            }
        }
        return combinations;
    }

    // http://stackoverflow.com/questions/127704/algorithm-to-return-all-combinations-of-k-elements-from-n
    private static List<Double> getSublist(List<Double> list, int i) {
        List<Double> sublist = new ArrayList<>();
        for (int j = i; j < list.size(); j++) {
            sublist.add(list.get(j));
        }
        return sublist;
    }

    private List<List<Double>> getSubsets(List<Double> superSet, int minNumberOfChoices, int maxNumberOfChoices) {
        List<List<Double>> res = new ArrayList<>();
        for (int i = minNumberOfChoices; i <= maxNumberOfChoices; i++) {
            res.addAll(getCombinations(i, superSet));
        }
        return res;
    }

    private SortedSet<Double> normalizeSet(List<List<Double>> sets) {
        SortedSet<Double> normalizedSet = new TreeSet<>();
        for (List<Double> set : sets) {
            Double tempScore = 0d;
            for (Double score : set) {
                tempScore += score;
            }
            normalizedSet.add(truncateDoubleToPrecision(tempScore));
        }
        return normalizedSet;
    }

    private Double truncateDoubleToPrecision(double toBeTruncated) {
        return new BigDecimal(toBeTruncated).setScale(PRECISION, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private AssessmentInteractionDto getMatchingInteractionForResponseVarDto(String responseIdentifierId,
            List<AssessmentInteractionDto> interactionDtos) {
        if (responseIdentifierId == null || CollectionUtils.isEmpty(interactionDtos)) {
            return null;
        }
        for (AssessmentInteractionDto interactionDto : interactionDtos) {
            if (responseIdentifierId.equals(interactionDto.getResponseIdentifier())) {
                return interactionDto;
            }
        }
        return null;
    }
}
