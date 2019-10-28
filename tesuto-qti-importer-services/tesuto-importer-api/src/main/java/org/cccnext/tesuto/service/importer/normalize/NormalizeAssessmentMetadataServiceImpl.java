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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.exception.MetadataFormatException;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "normalizeAssessmentMetadataService")
public class NormalizeAssessmentMetadataServiceImpl implements NormalizeAssessmentMetadataService {
    private final static String ENTRY_TESTLET = "ENTRY-TESTLET";

    private List<AssessmentItemDto> itemDtos;
    private HashMap<String, AssessmentItemDto> itemDtoHashMap;

    public List<AssessmentItemDto> getItemDtos() {
        return itemDtos;
    }

    public void setItemDtos(List<AssessmentItemDto> itemDtos) {
        this.itemDtos = itemDtos;

    }

    private HashMap<String, AssessmentItemDto> getItemDtoHashMap() {
        return itemDtoHashMap;
    }

    private void initializeItemDtoHashMap(){
        itemDtoHashMap = new HashMap<>();
        getItemDtos().forEach(assessmentItemDto -> itemDtoHashMap.put(assessmentItemDto.getIdentifier(), assessmentItemDto));
    }

    @Override
    public void normalizeMetadata(List<AssessmentDto> assessmentDtoList, List<AssessmentItemDto> itemDtoList) {
        log.debug("Validating Assessment Metadata ...");
        setItemDtos(itemDtoList);
        assessmentDtoList.forEach(assessmentDto -> normalizeAssessmentDtoMetadata(assessmentDto));
        log.debug("Finished Validating Assessment Metadata");
    }

    private void normalizeAssessmentDtoMetadata(AssessmentDto assessmentDto){
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        if(assessmentMetadataDto != null){
            capitalizeMetadataCompetencyMapDisciplines(assessmentMetadataDto.getCompetencyMapDisciplines());
            List<SectionMetadataDto> sections = assessmentMetadataDto.getSection();
            if(CollectionUtils.isNotEmpty(sections)){
                for(SectionMetadataDto section: sections) {
                    Optional<AssessmentComponentDto> assessmentComponentDto = assessmentDto.getComponent(section.getIdentifier());
                    if(assessmentComponentDto.isPresent()) {
                        normalizeSectionMetadata(section, assessmentComponentDto.get());
                    }
                }
            }
        }
    }

    private void capitalizeMetadataCompetencyMapDisciplines(List<String> mapDisciplines){
        if(CollectionUtils.isNotEmpty(mapDisciplines)) {
            mapDisciplines.replaceAll(String::toUpperCase);
        }
    }
    private void normalizeSectionMetadata(SectionMetadataDto sectionMetadataDto, AssessmentComponentDto assessmentComponentDto){
        normalizeSectionMetadataCompetencyMapDiscipline(sectionMetadataDto);
        normalizeEntryTestLetSectionMetadata(sectionMetadataDto, assessmentComponentDto);
    }

    private void normalizeSectionMetadataCompetencyMapDiscipline(SectionMetadataDto sectionMetadataDto){
        if(sectionMetadataDto != null && sectionMetadataDto.getCompetencyMapDiscipline() != null) {
            sectionMetadataDto.setCompetencyMapDiscipline(sectionMetadataDto.getCompetencyMapDiscipline().toUpperCase());
        }
    }

    private void normalizeEntryTestLetSectionMetadata(SectionMetadataDto sectionMetadataDto, AssessmentComponentDto assessmentComponentDto){
        if(isInvalidSectionMetadata(sectionMetadataDto)){
            initializeItemDtoHashMap(); // only want init if found an invalid section metadata.
            log.warn("SectionMetadata type {} is invalid for section {}", sectionMetadataDto.getType(), sectionMetadataDto.getIdentifier());
            String competencyMapDiscipline = getCompetencyMapDisciplineFromEntrySection(assessmentComponentDto);
            if(StringUtils.isNotEmpty(competencyMapDiscipline)) {
                sectionMetadataDto.setCompetencyMapDiscipline(competencyMapDiscipline);
            }else{
                //Only search the entry-testlet if no competencyMapDiscipline is found it is assumed the author should fix the package.
                throw new MetadataFormatException("Competencies Map Disciplines must be set for assessmentMetadata and itemMetadata");
            }

        }
    }

    private boolean isInvalidSectionMetadata(SectionMetadataDto sectionMetadataDto){
        return (ENTRY_TESTLET.equalsIgnoreCase(sectionMetadataDto.getType()) && sectionMetadataDto.getCompetencyMapDiscipline() == null);
    }

    private String getCompetencyMapDisciplineFromEntrySection(AssessmentComponentDto assessmentComponentDto){
        if(assessmentComponentDto instanceof AssessmentSectionDto){
            AssessmentSectionDto sectionDto = (AssessmentSectionDto) assessmentComponentDto;
            List<AssessmentItemRefDto> itemRefs = sectionDto.getAssessmentItemRefs();
            return getCompetencyMapDisciplineFromItemRefs(itemRefs);
        }
        return null;
    }

    private String getCompetencyMapDisciplineFromItemRefs(List<AssessmentItemRefDto> itemRefs){
        if(CollectionUtils.isNotEmpty(itemRefs)){
            for(AssessmentItemRefDto itemRefDto: itemRefs){
                AssessmentItemDto itemDto =  getItemDtoHashMap().get(itemRefDto.getItemIdentifier());
                String competencyMapDiscipline = getCompetencyMapDisciplineFromItem(itemDto);
                if(competencyMapDiscipline != null){
                    return competencyMapDiscipline;
                }
            }
        }
        return null;
    }

    private String getCompetencyMapDisciplineFromItem(AssessmentItemDto itemDto){
        if(itemDto != null) {
            ItemMetadataDto itemMetadataDto = itemDto.getItemMetadata();
            if (itemMetadataDto != null) {
                CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
                return getCompetencyMapDisciplineFromCompetencies(competenciesItemMetadataDto);
            }
        }
        return null;
    }

    private String getCompetencyMapDisciplineFromCompetencies(CompetenciesItemMetadataDto competenciesItemMetadataDto){
        if(competenciesItemMetadataDto != null) {
            if (CollectionUtils.isNotEmpty(competenciesItemMetadataDto.getCompetencyRef())) {
                for (CompetencyRefItemMetadataDto competencyRefItemMetadataDto : competenciesItemMetadataDto.getCompetencyRef()) {
                    String competencyMapDiscipline = competencyRefItemMetadataDto.getCompetencyMapDiscipline();
                    if (StringUtils.isNotEmpty(competencyMapDiscipline)) {
                        return competencyMapDiscipline;
                    }
                }
            }
        }
        return null;
    }
}
