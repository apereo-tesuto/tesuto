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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.domain.util.RandomGenerator;



import javax.persistence.criteria.CriteriaBuilder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Jason Brown jbrown@unicon.net on 10/20/16.
 */
public class SelectionServiceImpl implements SelectionService {

    RandomDataGenerator rg = new RandomDataGenerator();

    @Override
    public List<AssessmentComponentDto> calculateSectionComponents(AssessmentSectionDto sectionDto) {
        return calculateComponents(sectionDto.getAssessmentComponents(), sectionDto.getSelection());
    }

    /**
     *
     * https://www.imsglobal.org/question/qtiv2p2p1/QTIv2p2p1-ASI-InformationModelv1p0/imsqtiv2p2p1_asi_v1p0_InfoModelv1p0.html#Data_Selection
     * https://cccnext.jira.com/wiki/display/CCCAS/Selection
     *
     * There are a number of edge cases by which an author may have made a mistake, but do not require an exception in delivery.
     *
     * Edge Case 1. Number of components < select
     *      Handled by: deliver all components 
     * Edge Case 2. Number of select <= 0
     *      Handled by: deliver nothing
     * Edge Case 3. Number of required > select
     *      Handled by: deliver all required.  
     *
     */
    public List<AssessmentComponentDto> calculateComponents(List<AssessmentComponentDto> componentDtos,
            AssessmentSelectionDto selectionDto) {
        if(componentDtos == null || selectionDto == null){
            return componentDtos;
        }
        List<AssessmentComponentDto> filteredComponents = new ArrayList<>();

        //edge case 1
        if(selectionDto.getSelect() > componentDtos.size()) {
            return componentDtos;
        }

        //edge case 2
        if(selectionDto.getSelect() <= 0) {
            return filteredComponents;
        }

        Pair<SortedSet<Integer>,SortedSet<Integer>> indices = determineIndices(componentDtos);
        SortedSet<Integer> notRequiredIndices = indices.getLeft();
        SortedSet<Integer> requiredIndices = indices.getRight();

        int requiredComponentCount = requiredIndices.size();

        int selectComponentTargetCount = Math.max(requiredComponentCount, selectionDto.getSelect());
        int notReqComponentTargetCount = selectComponentTargetCount - requiredComponentCount;

        requiredIndices.addAll(selectRandomIndices(notRequiredIndices, notReqComponentTargetCount));

        requiredIndices.forEach( index -> {
            filteredComponents.add(componentDtos.get(index));
        });

        return filteredComponents;
    }

    private SortedSet<Integer> selectRandomIndices(SortedSet<Integer> indices, int select){
        if(select <= 0){
            return new TreeSet<>();
        }

        Object[] samples = rg.nextSample(indices, select);
        SortedSet sampleSet = new TreeSet<>(Arrays.asList(samples));

        return sampleSet;
    }

    private Pair<SortedSet<Integer>,SortedSet<Integer>> determineIndices(List<AssessmentComponentDto> componentDtos){
        SortedSet<Integer> required = new TreeSet<>();
        SortedSet<Integer> notRequired = new TreeSet<>();
        for(int i=0; i<componentDtos.size(); i++){
            AssessmentComponentDto assessmentComponentDto = componentDtos.get(i);
            if((assessmentComponentDto instanceof AssessmentSectionDto && !((AssessmentSectionDto) assessmentComponentDto).isRequired())
                    || (assessmentComponentDto instanceof AssessmentItemRefDto && !((AssessmentItemRefDto) assessmentComponentDto).isRequired())) {
                notRequired.add(i);
            }else if((assessmentComponentDto instanceof AssessmentSectionDto && ((AssessmentSectionDto) assessmentComponentDto).isRequired())
                    || (assessmentComponentDto instanceof AssessmentItemRefDto && ((AssessmentItemRefDto) assessmentComponentDto).isRequired())){
                required.add(i);
            }else if (!(assessmentComponentDto instanceof AssessmentSectionDto || assessmentComponentDto instanceof AssessmentItemRefDto)) {
                throw new IllegalArgumentException("Only AssessmentSectionDto or AssessmentItemRefDto is expected here, but was an instanceOf "+ componentDtos.get(i).getClass());
            }
        }
        return new ImmutablePair<>(notRequired, required);
    }
}
