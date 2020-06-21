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
package org.cccnext.tesuto.importer.qti.qa;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.importer.service.competency.CompetencyImportService;
import org.cccnext.tesuto.importer.service.upload.PackageResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.util.CollectionUtils;

@Service
public class ImportServicesModuleSeedDataService {
    
    @Resource(name = "mathSeedDataService")
    ParseFileService mathSeedData;

    @Resource(name = "mathPaperSeedDataService")
    ParseFileService mathPaperSeedData;

    @Resource(name = "elaSeedDataService")
    ParseFileService englishSeedData;

    @Resource(name = "elaPaperSeedDataService")
    ParseFileService englishPaperSeedData;

    @Resource(name = "interactionSeedDataService")
    ParseFileService interactionSeedData;

    @Resource(name = "interactionPaperSeedDataService")
    ParseFileService interactionPaperSeedData;

    @Autowired
    AssessmentService assessmentService;
    
    @Autowired
    AssessmentItemService assessmentItemService;
    
    @Autowired
    CompetencyImportService competencyImportService;
    
    @Autowired
    CompetencyMapService competencyMapService;
    
    @Autowired
    CompetencyMapOrderService competencyMapOrderService;

    @Value("${seed.data.store}")
    Boolean seedDataStore;
    
    @Value("${seed.data.math.competency.map.uri}")
    String competencyMathMapUri;
    
    @Value("${seed.data.english.competency.map.uri}")
    String competencyEnglishMapUri;
    
    @Value("${seed.data.esl.competency.map.uri}")
    String competencyEslMapUri;
    
    
    public void seedData() throws Exception {
            PackageResults mathResults = mathSeedData.getPackageResults();
            mathResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            mathResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai) );
            PackageResults mathPaperResults = mathPaperSeedData.getPackageResults();
            mathPaperResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            mathPaperResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai) );
            PackageResults englishResults = englishSeedData.getPackageResults();
            englishResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            englishResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai));
            PackageResults englishPaperResults = englishPaperSeedData.getPackageResults();
            englishPaperResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            englishPaperResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai));
            PackageResults interactionResults = interactionSeedData.getPackageResults();
            interactionResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            interactionResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai));
            PackageResults interactionPaperResults = interactionPaperSeedData.getPackageResults();
            interactionPaperResults.getAssessmentDtos().forEach(a -> saveAssessment(a));
            interactionPaperResults.getAssessmentItemDtos().forEach(ai -> saveAssessmentItem(ai));

            saveCompetencyMap("ENGLISH",  competencyEnglishMapUri);
            saveCompetencyMap("ESL",  competencyEslMapUri);
            saveCompetencyMap("MATH",  competencyMathMapUri);
        
    }
    
    private void saveCompetencyMap(String discipline, String url) throws Exception {
        CompetencyMapDto competencyMapDto = competencyMapService.readLatestPublishedVersion(discipline);
        if(competencyMapDto == null) {
            List<CompetencyMapDto> competencyMapDtos = competencyImportService.createCompetencyMapFromFile(new File(url), false);
            if(!CollectionUtils.isNullOrEmpty(competencyMapDtos)) {
                competencyMapDtos.stream().forEach(cm -> 
                competencyMapOrderService.create(cm));
            }
        } else {
            if(competencyMapOrderService.findLatestPublishedIdByCompetencyMapDiscipline(discipline) == null){
                competencyMapOrderService.create(competencyMapDto);
            }
        }
    }

    private void saveAssessment(AssessmentDto assessmentDto) {
        if(CollectionUtils.isNullOrEmpty(assessmentService.read(assessmentDto.getScopedIdentifier()))) {
            int version = assessmentService.getNextVersion(assessmentDto.getNamespace(), assessmentDto.getIdentifier());
            assessmentDto.setVersion(version);
            assessmentService.create(assessmentDto);
        }
    }

    private void saveAssessmentItem(AssessmentItemDto item) {
        if(CollectionUtils.isNullOrEmpty(assessmentItemService.getAllVersions(item.getNamespace(), item.getIdentifier()))) {
            int version = assessmentItemService.getNextVersion(item.getNamespace(), item.getIdentifier());
            item.setVersion(version);
            assessmentItemService.create(item);
        }
    }
}
