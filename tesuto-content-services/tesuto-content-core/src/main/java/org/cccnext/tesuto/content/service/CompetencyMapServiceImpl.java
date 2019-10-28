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
package org.cccnext.tesuto.content.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.assembler.competency.CompetencyMapDtoAssembler;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMap;
import org.cccnext.tesuto.content.repository.mongo.CompetencyMapRepository;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.util.TesutoUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "competencyMapService")
public class CompetencyMapServiceImpl implements CompetencyMapService {

    @Autowired
    CompetencyMapRepository competencyMapRepository;

    @Autowired
    CompetencyMapDtoAssembler competencyMapDtoAssembler;

    @Override
    public CompetencyMapDto create(CompetencyMapDto competencyMapDto) {
        if (StringUtils.isBlank(competencyMapDto.getDiscipline())) {
            throw new PoorlyFormedRequestException("To create an competency, a discipline is required");
        }

        CompetencyMap competencyMap = competencyMapDtoAssembler.disassembleDto(competencyMapDto);
        if (StringUtils.isBlank(competencyMap.getId())) {
            competencyMap.setId(TesutoUtils.newId());
        }

        CompetencyMap savedCompetencyMap = competencyMapRepository.save(competencyMap);

        // There is a possibility that competencyMaps are not done being saved across Mongo nodes.  This code
        // makes sure the writes have time to settle across Mongo slaves.
        CompetencyMap competencyMapCheck  = null;
        for (int i = 0; i < 10 && competencyMapCheck == null; ++i ) {
            competencyMapCheck = competencyMapRepository.findByDisciplineAndVersion(
                    savedCompetencyMap.getDiscipline(), savedCompetencyMap.getVersion());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Seriously don't care about this exception.
            }
        }
        if (competencyMapCheck == null || !savedCompetencyMap.getId().equals(competencyMapCheck.getId())) { // Note we are checking the Mongo GUID here.
            /* A Mongo insert silently fails if there is already a document with
             * the same (discipline, version), because of a unique index 2 maps
             * were uploaded with the same discipline and version. The first upload
             * is kept. The second one causes this error because the version is the
             * same. Retrying this upload will cause a version bump and update the conflicting
             * content. This is likely a competency map authoring conflict that needs
             * to be resolved from a content consistency standpoint.
             */
            log.error("savedCompetencyMap: {}", savedCompetencyMap);
            log.error("competencyMapCheck: {}", competencyMapCheck);
            throw new MongoException("Insert failed -- unique constraint violation or data propagation failed");
        }

        publishLatestVersion(savedCompetencyMap);
        return competencyMapDtoAssembler.assembleDto(savedCompetencyMap);
    }

    private void publishLatestVersion(CompetencyMap latestCompetencyMap) {
        List<CompetencyMap> competencyMapList = null;
        // Guarantee Mongo propagation.
        for (int i = 0; i < 10 && (competencyMapList == null || !competencyMapList.contains(latestCompetencyMap)); ++i) {
            competencyMapList = competencyMapRepository.findByDisciplineOrderByVersionDesc(
                    latestCompetencyMap.getDiscipline());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Seriously don't care about this exception.
            }
        }
        Iterator<CompetencyMap> competencyMapIterator = competencyMapList.iterator();
        Boolean published = true;
        while (competencyMapIterator.hasNext()) {
            CompetencyMap competencyMap = competencyMapIterator.next();
            if (competencyMap.isPublished() != published) {
                competencyMap.setPublished(published);
                competencyMapRepository.save(competencyMap);
                log.debug(String.format("CompetencyMap published: %s : %s : %s : %s : %s : %s", competencyMap.getId(),
                        competencyMap.getDiscipline(), competencyMap.getIdentifier(), competencyMap.getVersion(),
                        competencyMap.isPublished(), published));
            }
            published = false;
        }
    }

    @Override
    public List<CompetencyMapDto> create(List<CompetencyMapDto> competencyMapDtos) {
        List<CompetencyMapDto> storedCompetencyMaps = new ArrayList<>();
        for (CompetencyMapDto competencyMapDto : competencyMapDtos) {
            storedCompetencyMaps.add(create(competencyMapDto));
        }
        return storedCompetencyMaps;
    }

    @Override
    public void delete(String identifier) {
        competencyMapRepository.deleteById(identifier);
    }

    @Override
    public CompetencyMapDto readById(String identifier) {
        CompetencyMap competencyMap = competencyMapRepository.findById(identifier).get();
        return competencyMapDtoAssembler.assembleDto(competencyMap);
    }

    //Removed Scoped identifier. What the what.
    @Override
    public CompetencyMapDto readLatestPublishedVersion(String discipline) {
        List<CompetencyMap> competencyMaps = competencyMapRepository.findByDisciplineAndPublishedOrderByVersionDesc(discipline, true );
        if (CollectionUtils.isEmpty(competencyMaps)){
            return null;
        }
        return competencyMapDtoAssembler.assembleDto(competencyMaps.get(0));
    }

    @Override
    public List<CompetencyMapDto> readPublishedUnique() {
        Iterable<CompetencyMap> competencyMaps = competencyMapRepository.findByPublished(true);
        Map<String, CompetencyMap> mapOfcompetencyMaps = new HashMap<>();

        for (CompetencyMap competencyMap : competencyMaps) {
            if (mapOfcompetencyMaps.containsKey(competencyMap.getIdentifier())) {
                CompetencyMap storeCompetencyMap = mapOfcompetencyMaps.get(competencyMap.getIdentifier());
                if (storeCompetencyMap.getVersion() < competencyMap.getVersion()) {
                    mapOfcompetencyMaps.put(competencyMap.getIdentifier(), competencyMap);
                }
            } else {
                mapOfcompetencyMaps.put(competencyMap.getIdentifier(), competencyMap);
            }
        }
        return competencyMapDtoAssembler.assembleDto(IteratorUtils.toList(competencyMaps.iterator()));
    }

    @Override
    public int getNextVersion(String discipline) {
        int nextVersion = findMaxVersion(discipline);
        if (nextVersion == -1 || nextVersion == 0) { // -1 is returned if there
            // is no previous version
            // (upload).
            nextVersion = 1; // We'll start with version 1
        } else {
            ++nextVersion;
        }
        return nextVersion;
    }

    private int findMaxVersion(String discipline) {
        List<CompetencyMap> competencyMaps = competencyMapRepository.findByDisciplineOrderByVersionDesc(discipline);
        if (CollectionUtils.isEmpty(competencyMaps)) {
            return -1;
        }
        CompetencyMap competencyMap = competencyMaps.get(0);
        return competencyMap.getVersion();
    }

    @Override
    public Boolean setPublishFlag(String discipline, String identifier, int version, boolean isPublished) {
        Boolean valueAfterSaving = null;
        CompetencyMap competencyMap = competencyMapRepository.findByDisciplineAndVersion(discipline, version);
        if (competencyMap != null) {
            competencyMap.setPublished(isPublished);
            competencyMapRepository.save(competencyMap);
            valueAfterSaving = competencyMap.isPublished();
        }
        return valueAfterSaving;
    }

    @Override
    public List<CompetencyMapDto> read() {
        Iterable<CompetencyMap> competencyMaps = competencyMapRepository.findAll();
        return competencyMapDtoAssembler.assembleDto(IteratorUtils.toList(competencyMaps.iterator()));
    }

    @Override
    public List<CompetencyMapDto> read(String discipline) {
        List<CompetencyMap> competencyMaps = competencyMapRepository.findByDiscipline(discipline);
        return competencyMapDtoAssembler.assembleDto(competencyMaps);
    }

    @Override
    public CompetencyMapDto readByVersion(String discipline, Integer version) {
        CompetencyMap competencyMap = competencyMapRepository.findByDisciplineAndVersion(discipline, version);
        return competencyMapDtoAssembler.assembleDto(competencyMap);
    }

    @Override
    public CompetencyMapDto readByTitleAndVersion(String title, Integer version) {
        CompetencyMap competencyMap = competencyMapRepository.findByTitleAndVersion(title, version);
        return competencyMapDtoAssembler.assembleDto(competencyMap);
    }

}
