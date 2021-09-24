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
package org.cccnext.tesuto.rules.stub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.cccnext.tesuto.placement.service.PlacementReader;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

public class PlacementReaderStub implements PlacementReader {

    List<PlacementViewDto> placements = new ArrayList<>();
    
    List<PlacementComponentViewDto> placementComponents = new ArrayList<>();
    
    public void clearPlacements() {
        placements.clear();
    }
    
    public void clearPlacementComponents() {
        placementComponents.clear();
    }

    public void addPlacements(String collegeId, String cccid, Integer disciplineId, Integer subjectAreaVersion,
            Integer count) {

        for (int i = 0; i < count; i++) {
            PlacementViewDto view = new PlacementViewDto();
            view.setCollegeId(collegeId);
            view.setCccid(cccid);
            view.setSubjectAreaVersion(subjectAreaVersion);
            view.setDisciplineId(disciplineId);
            placements.add(view);
        }

    }

    public void addPlacementComponents(String collegeId, String cccid, Integer subjectAreaId,
            Integer subjectAreaVersion, String[] entityTargetClasses, Integer count) {

        for (int i = 0; i < count; i++) {
            for (String entityTargetClass : entityTargetClasses) {
                PlacementComponentViewDto view = new PlacementComponentViewDto();
                view.setCollegeId(collegeId);
                view.setCccid(cccid);
                view.setSubjectAreaVersion(subjectAreaVersion);
                view.setSubjectAreaId(subjectAreaId);
                view.setEntityTargetClass(entityTargetClass);
                placementComponents.add(view);
            }
        }
    }

    @Override
    public VersionedSubjectAreaViewDto getVersionSubjectArea(Integer subjectAreaId, Integer subjectAreaVersion) {
        VersionedSubjectAreaViewDto subjectArea = new VersionedSubjectAreaViewDto();
        subjectArea.setDisciplineId(subjectAreaId);
        subjectArea.setVersion(subjectAreaVersion);
        subjectArea.setCompetencyAttributes(new CompetencyAttributesViewDto());
        return subjectArea;
    }

    @Override
    public List<PlacementComponentViewDto> getPlacementComponents(String collegeMisCode, String cccid) {
        return placementComponents.stream().filter(pc-> pc.getCollegeId().equals(collegeMisCode) && pc.getCccid().equals(cccid))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<PlacementViewDto> getPlacements(String collegeMisCode, String cccid) {
        return placements.stream().filter(p-> p.getCollegeId().equals(collegeMisCode) && p.getCccid().equals(cccid))
                .collect(Collectors.toList());
    }

}
