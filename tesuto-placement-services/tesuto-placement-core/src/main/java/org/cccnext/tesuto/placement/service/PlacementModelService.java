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
package org.cccnext.tesuto.placement.service;

import java.util.List;

import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.placement.model.CollegePlacementModel;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.repository.jpa.PlacmentModelRepository;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("placementModelService")
public class PlacementModelService {
   
    @Autowired
    CompetencyMapOrderReader competencyMapOrderReader;
    
    @Autowired
    SubjectAreaService placementService;
    
    @Autowired
    PlacmentModelRepository repository;
    
    @Transactional
    public CollegePlacementModel getPlacementModel(Integer disciplineId) {
        CollegePlacementModel placementModel = repository.findByDisciplineIdAndIsDirty(disciplineId, false);

        if(placementModel == null) {
            placementModel = createPlacementModel(disciplineId);
        }
        return placementModel;
    }

    @Transactional
    private CollegePlacementModel createPlacementModel(Integer disciplineId) {
        Discipline discipline = placementService.getDisciplineAndSequences(disciplineId);
       
        if(discipline == null) {
            return null;
        }
        String competencyMapOrderId = competencyMapOrderReader.findLatestPublishedIdByCompetencyMapDiscipline(discipline.getCompetencyMapDiscipline());
        if(competencyMapOrderId == null) {
            return null;
        }

        CollegePlacementModel collegePlacementModel = new CollegePlacementModel();
        collegePlacementModel.setCollegeId(discipline.getCollegeId());
        collegePlacementModel.setCompetencyMapDiscipline(discipline.getCompetencyMapDiscipline());
        collegePlacementModel.setCompetencyMapOrderId(competencyMapOrderId);
        collegePlacementModel.setDirty(false);
        collegePlacementModel.setDisciplineId(disciplineId);
       
        
        collegePlacementModel.setPlacementModel(discipline);
        
        collegePlacementModel.setId(TesutoUtils.newId());
        return repository.save(collegePlacementModel);
    }

    @Transactional
    public void deleteAllPlacementModelsForDisciplineId(int disciplineId) {
        @SuppressWarnings("deprecation")
		List<CollegePlacementModel> models = repository.findByDisciplineId(disciplineId);
        repository.deleteAll(models);
    }

}
