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

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyPerformanceMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.OverallPerformanceMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value="validateAssessmentMetadataService")
public class ValidateAssessmentMetadataServiceImpl implements ValidateAssessmentMetadataService {

    @Override
    public List<ValidationMessage> processMetadataMap(HashMap<String, AssessmentMetadataDto> metadataDtoMap, List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos){
        List<ValidationMessage> errors = new ArrayList<>();
        if(metadataDtoMap != null) {
            metadataDtoMap.forEach((k, v) -> errors.addAll(processAssessmentMetadata(v, competencyMapDisciplineDtos)));
        }
        return errors;
    }

    private List<ValidationMessage> processAssessmentMetadata(AssessmentMetadataDto assessmentMetadataDto, List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos){
        List<ValidationMessage> errors = new ArrayList<>();
        if(assessmentMetadataDto != null) {
            if (assessmentMetadataDto.getCompetencyMapDisciplines() != null) {
                assessmentMetadataDto.getCompetencyMapDisciplines().forEach(discipline -> errors.addAll(verifyDiscipline(discipline, competencyMapDisciplineDtos)));
            }
            OverallPerformanceMetadataDto overallPerformance = assessmentMetadataDto.getOverallPerformance();
            if (overallPerformance != null) {
                errors.addAll(processOverallPerformance(overallPerformance));
            }
            CompetencyPerformanceMetadataDto competencyPerformance = assessmentMetadataDto.getCompetencyPerformance();
            if (competencyPerformance != null) {
                List<CompetencyCategoryMetadataDto> competencyCategories = competencyPerformance.getCompetencyCategories();
                if (competencyCategories != null && competencyCategories.size() != 0) {
                    for (CompetencyCategoryMetadataDto competencyCategory : competencyCategories) {
                        errors.addAll(processCompetencyCategory(competencyCategory));
                    }
                }
            }
        }
        return errors;
    }

    private List<ValidationMessage> processOverallPerformance(OverallPerformanceMetadataDto overallPerformance) {
        List<ValidationMessage> errors = new ArrayList<>();
        List<PerformanceRangeMetadataDto> overallPerformanceRanges = overallPerformance.getPerformanceRanges();
        if (overallPerformanceRanges != null && overallPerformanceRanges.size() != 5) {
            errors.add(createAssessmentMetadataValidationMessage("Overall performance metadata should contain exactly 5 performance ranges.", "overallPerformance"));
        } else {
            errors.addAll(processPerformanceRanges(overallPerformanceRanges));
        }
        return errors;
    }
    
    private List<ValidationMessage> processCompetencyCategory(CompetencyCategoryMetadataDto competencyCategory) {
        List<ValidationMessage> errors = new ArrayList<>();
        List<PerformanceRangeMetadataDto> categoryPerformanceRanges = competencyCategory.getPerformanceRanges();
        if (categoryPerformanceRanges != null && categoryPerformanceRanges.size() != 3) {
            errors.add(createAssessmentMetadataValidationMessage("Performance by category metadata should contain exactly 3 performance ranges per category.", "performanceRange"));
        } else {
            errors.addAll(processPerformanceRanges(categoryPerformanceRanges));
        }
        return errors;
    }

    private List<ValidationMessage> processPerformanceRanges(List<PerformanceRangeMetadataDto> performanceRanges) {
        List<ValidationMessage> errors = new ArrayList<>();
        Collections.sort(performanceRanges);
        for (int index = 1; index < performanceRanges.size(); index++) {
            if (performanceRanges.get(0).getPosition() != 1) {
                errors.add(createAssessmentMetadataValidationMessage("Performance range position should begin with position 1.", "performanceRange"));
            }
            if ((performanceRanges.get(index).getPosition() - 1) != performanceRanges.get(index - 1).getPosition()) {
                errors.add(createAssessmentMetadataValidationMessage("Performance range positions are not sequential.", "performanceRange"));
            }
            if (performanceRanges.get(index).getMin() != performanceRanges.get(index - 1).getMax()) {
                errors.add(createAssessmentMetadataValidationMessage("Performance range min should equal previous performance position's performance range max.", "performanceRange"));
            }
            if (performanceRanges.get(index).getMax() <= performanceRanges.get(index).getMin()) {
                errors.add(createAssessmentMetadataValidationMessage("Performance range max should be greater than min.", "performanceRange"));
            }
        }
        return errors;
    }

    private List<ValidationMessage> verifyDiscipline(String discipline, List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos){
        List<ValidationMessage> errors = new ArrayList<>();
        List<String> validCompetencyMapDtos = competencyMapDisciplineDtos.stream().map(mapDisciplineDto -> mapDisciplineDto.getDisciplineName()).collect(Collectors.toList());
        boolean containsDiscipline = validCompetencyMapDtos.contains(discipline.toUpperCase());
        if(!containsDiscipline){
            String validationErrorMessage = String.format("The competency map discipline %s is not supported use one of the following: %s", discipline, validCompetencyMapDtos);
            log.error(validationErrorMessage);
            errors.add(createAssessmentMetadataValidationMessage(validationErrorMessage, "competencyMapDiscipline"));
        }
        return errors;
    }

    @Override
    public List<ValidationMessage> validateMetadataMapKeysMatchAssessmentIdentifiers(HashMap<String, AssessmentMetadataDto> metadataDtoHashMap, List<AssessmentDto> assessmentDtos){
        List<ValidationMessage> warnings = new ArrayList<>();
        assessmentDtos.forEach(a-> metadataDtoHashMap.remove(a.getIdentifier()));
        if(metadataDtoHashMap != null && !metadataDtoHashMap.isEmpty()){
            metadataDtoHashMap.forEach((identifier, metadata) -> {
                String validationWarningMessage = String.format("There is not a matching assessment for the metadata with the identifier: %s", identifier);
                log.warn(validationWarningMessage);
                warnings.add(createAssessmentMetadataValidationMessage(validationWarningMessage, "identifier"));
            });
        }
        return warnings;
    }

    private ValidationMessage createAssessmentMetadataValidationMessage(String messsage, String node){
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setMessage(messsage);
        validationMessage.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);
        validationMessage.setNode(node);
        return validationMessage;
    }
}
