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
package org.cccnext.tesuto.service.importer.normalize;

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value ="normalizeAssessmentItemService")
public class NormalizeAssessmentItemServiceImpl implements NormalizeAssessmentItemService {

    private final String SCORE = "SCORE";

    @Autowired
    NormalizeItemMetadataService normalizeItemMetadataService;

    public void normalizeOutcomeMaxMin(List<AssessmentItemDto> assessmentItemDtos, HashMap<String, SortedSet<Double>> itemMap){
        for(AssessmentItemDto itemDto: assessmentItemDtos) {
            if(itemMap.containsKey(itemDto.getIdentifier())){
                Double max = itemMap.get(itemDto.getIdentifier()).last();
                Double min = itemMap.get(itemDto.getIdentifier()).first();
                updateOutcomeDeclarationMaxAndMin(itemDto.getOutcomeDeclarationDtos(), SCORE, max, min);
            }
            normalizeItemMetadataService.normalizeItemMetadata(itemDto.getItemMetadata());
        }
    }

    private void updateOutcomeDeclarationMaxAndMin(List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtos, String searchType, Double max, Double min){
        outcomeDeclarationDtos.stream().filter(o -> searchType.equals(o.getIdentifier())).forEach(p -> {
            if (p.getNormalMaximum() == null) {
                p.setNormalMaximum(max);
            }else if (!p.getNormalMaximum().equals(max)){
                log.warn("Author has set item outcome maximum to {} but validation has found item outcome maximum to be {}", p.getNormalMaximum(), max);
            }

            if (p.getNormalMinimum() == null) {
                p.setNormalMinimum(min);
            }else if (!p.getNormalMinimum().equals(min)){
                log.warn("Author has set item outcome minimum to {} but validation has found item outcome minimum to be {}", p.getNormalMinimum(), min);
            }
        });
    }

}
