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
package org.cccnext.tesuto.rules.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.placement.service.PlacementReader;
import org.cccnext.tesuto.placement.view.AssignedPlacementRulesSourceDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class AssignPlacementFactService  implements IFactsPreProcessor, InitializingBean {

    final static String NAME = "ASSIGN_PLACEMENT_FACTS";

    @Autowired
    PlacementReader placementRestClient;

    @Value("${ASSIGN_PLACEMENT_FACTS_ENABLED}")
    private Boolean enabled;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isEnabled(Map<String, Object> facts) {
        return enabled;
    }

    public String getCccid(Map<String, Object> facts) {
        String cccid = (String) facts.get(PlacementMapKeys.CCCID_KEY);
        if (StringUtils.isBlank(cccid)) {
            return "";
        }
        return cccid;
    }

    @Override
    public List<String> processFacts(Map<String, Object> facts) {
        log.debug("Validating facts with AssignPlacementFactService");
        List<String> errors = new ArrayList<String>();

        if (!StringUtils.equals(String.valueOf( facts.get(PlacementMapKeys.EVENT_TRIGGER_KEY)), 
                PlacementMapKeys.EVENT_TRIGGER_ASSIGNMENT)) {
            return errors;
        }

        String cccid = getCccid(facts);
        if (StringUtils.isBlank(cccid)) {
            log.error("Cannot assign Placement with a blank cccid");
            errors.add("Cannot assign Placement with a blank cccid");
        }
        
        if(!facts.containsKey(PlacementMapKeys.MISCODE_KEY)) {
            log.error("Cannot assign Placement with a blank miscode");
            errors.add("Cannot assign Placement with a blank miscode");
        }
        
        if(!facts.containsKey(PlacementMapKeys.SUBJECT_AREA_KEY)) {
            log.error("Cannot assign Placement with a blank miscode");
            errors.add("Cannot assign Placement with a blank miscode");
        }
        
        if (!facts.containsKey(PlacementMapKeys.RULE_SET_ID_KEY)) {
            log.error("Cannot create Placement with a blank rule set id");
            errors.add("Cannot create Placement with a blank rule set id");
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        String collegeMisCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        
        Integer subjectAreaId = (Integer)Integer.parseInt((String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY));
        
        try {
            AssignedPlacementRulesSourceDto source =  getAssignedPlacement(collegeMisCode, cccid, subjectAreaId);
                    if(source == null || CollectionUtils.isEmpty(source.getPlacements())) {
                       String message =  String.format("Cannot assign Placement with a no placements for student %s, college %s and subjectAreaId %s",
                                cccid, collegeMisCode, subjectAreaId);
                        log.error(message);
                        errors.add(message);
                    } else {
                        facts.put(PlacementMapKeys.PLACEMENTS_KEY,   source);
                    }
        } catch(Exception exception) {
            String message =  String.format("Cannot assign Placement with a no placements for student %s, college %s and subjectAreaId %s, exceptinn thrown: ",
                    cccid, collegeMisCode, subjectAreaId, exception.getMessage());
            log.error(message);
            errors.add(message);
        }
        return errors;
    }
    
    // Required to do it this way because initial application of rules request does not take a Map<String,Object> and is not in our code base.
    public AssignedPlacementRulesSourceDto getAssignedPlacement(String collegeMisCode, String cccid, Integer subjectAreaId) {        
        AssignedPlacementRulesSourceDto rulesDto = new AssignedPlacementRulesSourceDto();
        Collection<PlacementViewDto> placementViewDtos = placementRestClient.getPlacements(collegeMisCode, cccid);
        //TODO Confirm that we want to filter only of subjectAreaId and not also subjectAreaVersionId
        if(placementViewDtos != null)
        rulesDto.setPlacements(
                placementViewDtos.stream().filter( it -> it.getDisciplineId().equals(subjectAreaId))
                    .collect(Collectors.toList()));
        else
        	 rulesDto.setPlacements(new ArrayList<PlacementViewDto>());

        return rulesDto;
    }

}
