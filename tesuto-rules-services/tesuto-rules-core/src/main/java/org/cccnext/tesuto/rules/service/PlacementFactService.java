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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.placement.service.PlacementReader;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementRulesSourceDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PlacementFactService implements IFactsPreProcessor, InitializingBean {

    final static String NAME = "PLACEMENT_FACTS";

    @Value("${PLACEMENT_FACTS_ENABLED}")
    private Boolean enabled;

    @Autowired
    @Lazy
    PlacementReader placementRestClient;

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
        log.debug("Validating facts with PlacementFactService");
        List<String> errors = new ArrayList<String>();

        if (!StringUtils.equals(String.valueOf(facts.get(PlacementMapKeys.EVENT_TRIGGER_KEY)),
                PlacementMapKeys.EVENT_TRIGGER_PLACEMENT)) {
            return errors;
        }

        String cccid = getCccid(facts);
        if (StringUtils.isBlank(cccid)) {
            log.error("Cannot create Placement with a blank cccid");
            errors.add("Cannot create Placement with a blank cccid");
        }

        if (!facts.containsKey(PlacementMapKeys.MISCODE_KEY)) {
            log.error("Cannot create Placement with a blank miscode");
            errors.add("Cannot create Placement with a blank miscode");
        }
        
        if (!facts.containsKey(PlacementMapKeys.SUBJECT_AREA_KEY)) {
            log.error("Cannot create Placement with a blank subjectAreaId");
            errors.add("Cannot create Placement with a blank subjectAreaId");
        }
        
        if (!facts.containsKey(PlacementMapKeys.SUBJECT_AREA_VERSION_KEY)) {
            log.error("Cannot create Placement with a blank subjectAreaVersion");
            errors.add("Cannot create Placement with a blank subjectAreaVersion");
            
        }
        
        if (!facts.containsKey(PlacementMapKeys.RULE_SET_ID_KEY)) {
            log.error("Cannot create Placement with a blank rule set id");
            errors.add("Cannot create Placement with a blank rule set id");
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        Integer subjectAreaId = Integer.parseInt((String) facts.get(PlacementMapKeys.SUBJECT_AREA_KEY));
        Integer subjectAreaVersion = Integer.parseInt((String) facts.get(PlacementMapKeys.SUBJECT_AREA_VERSION_KEY));     
        String collegeMisCode = (String) facts.get(PlacementMapKeys.MISCODE_KEY);

        VersionedSubjectAreaViewDto subjectArea = placementRestClient.getVersionSubjectArea(subjectAreaId,
                subjectAreaVersion);

        if (subjectArea == null) {
            log.error("Unabled to retrieve subject area for subject area id {}, and version {}", subjectAreaId,
                    subjectAreaVersion);
            errors.add("Cannot create placement without subject area");
        }

        if (!facts.containsKey(PlacementMapKeys.RULE_SET_ID_KEY)) {
            log.error("Cannot create Placement with a blank rule set id");
            errors.add("Cannot create Placement with a blank rule set id");
        }
       
        facts.put(PlacementMapKeys.COMPETENCY_ATTRIBUTES_KEY, subjectArea.getCompetencyAttributes());
        
        if (!facts.containsKey(PlacementMapKeys.RULE_SET_ID_KEY)) {
            log.error("Cannot create Placement with a blank rule set id");
            errors.add("Cannot create Placement with a blank rule set id");
        }
        
        try {
            PlacementRulesSourceDto source =  getComponentPlacementFromFacts(collegeMisCode, cccid, subjectArea);
                    if(source == null || CollectionUtils.isEmpty(source.getMmapPlacementComponents())) {
                       String message =  String.format("Cannot Generate a Placement with a no mm placements for student %s, college %s and subjectAreaId %s",
                                cccid, collegeMisCode, subjectAreaId);
                        log.error(message);
                        errors.add(message);
                    } else {
                        facts.put(PlacementMapKeys.PLACEMENT_COMPONENTS_KEY,   source);
                    }
        } catch(Exception exception) {
            String message =  String.format("Cannot generate a placement with a no mm component placements for student %s, college %s and subjectAreaId %s, exceptinn thrown: %s",
                    cccid, collegeMisCode, subjectAreaId, exception.getMessage());
            log.error(message);
            errors.add(message);
        }

        return errors;
    }

    public PlacementRulesSourceDto getComponentPlacementFromFacts(String collegeMisCode, String cccid,
            VersionedSubjectAreaViewDto subjectArea) {
        PlacementRulesSourceDto rulesSourceDto = new PlacementRulesSourceDto();

        // retrieve all placement components for the subject area
        Collection<PlacementComponentViewDto> placementComponents = placementRestClient.getPlacementComponents(
                collegeMisCode, cccid);
        for (PlacementComponentViewDto placementComponentViewDto : placementComponents) {
            // only take placement components that match the subject area name
            // and version
            if (placementComponentViewDto.getSubjectAreaId().equals(subjectArea.getDisciplineId())
                    && placementComponentViewDto.getSubjectAreaVersion().equals(subjectArea.getVersion())) {
                if (placementComponentViewDto.getEntityTargetClass().contains(
                        PlacementMapKeys.MMAP_PLACEMENT_COMPONENT_CLASS_NAME)) {
                    rulesSourceDto.getMmapPlacementComponents().add(placementComponentViewDto);
                } else if (placementComponentViewDto.getEntityTargetClass().contains(
                        PlacementMapKeys.TESUTO_PLACEMENT_COMPONENT_CLASS_NAME)) {
                    rulesSourceDto.getAssessmentPlacementComponents().add(placementComponentViewDto);
                }
            }
        }
        return rulesSourceDto;
    }

    public CompetencyAttributesViewDto getCompetencyAttributesForSubjectArea(Integer subjectAreaId,
            Integer subjectAreaVersion) {
        VersionedSubjectAreaViewDto subjectArea = placementRestClient.getVersionSubjectArea(subjectAreaId,
                subjectAreaVersion);
        return subjectArea.getCompetencyAttributes();
    }

}
