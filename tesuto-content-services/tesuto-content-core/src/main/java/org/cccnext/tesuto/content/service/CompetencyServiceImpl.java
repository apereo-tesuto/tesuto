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

import com.mongodb.MongoException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.assembler.competency.CompetencyDtoAssembler;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.model.competency.Competency;
import org.cccnext.tesuto.content.repository.mongo.CompetencyRepository;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.util.TesutoUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "competencyService")
public class CompetencyServiceImpl implements CompetencyService{

    @Autowired
    CompetencyRepository competencyRepository;

    @Autowired
    CompetencyDtoAssembler competencyDtoAssembler;

    //Create can not be public because the Competencies must be created as a unit to insure the correct version
    // for the competencyRef values.
    private Competency create(CompetencyDto competencyDto) {
        if (StringUtils.isBlank(competencyDto.getDiscipline())) {
            throw new PoorlyFormedRequestException("To create an assessment, a discipline is required");
        }

        Competency competency = competencyDtoAssembler.disassembleDto(competencyDto);
        if (StringUtils.isBlank(competency.getId())) {
            competency.setId(TesutoUtils.newId());
        }

        Competency savedCompetency = competencyRepository.save(competency);

        // There is a possibility that competencyMaps are not done being saved across Mongo nodes.  This code
        // makes sure the writes have time to settle across Mongo slaves.
        Competency competencyCheck = null;
        for (int i = 0; i < 10 && competencyCheck == null; ++i ) {
            competencyCheck = competencyRepository.findByDisciplineAndIdentifierAndVersion(
                    savedCompetency.getDiscipline(), savedCompetency.getIdentifier(), savedCompetency.getVersion());
            if (competencyCheck == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // I truly don't care about this exception.
                }
            }
        }
        if (competencyCheck == null || !savedCompetency.getId().equals(competencyCheck.getId())) { // Note we are checking the Mongo GUID here.
            /* A Mongo insert silently fails if there is already a document with
             * the same (discipline, identifier, version), because of a unique index 2 competencies
             * were uploaded with the same discipline, identifier and version. The first upload
             * is kept. The second one causes this error because the version is the
             * same. Retrying this upload will cause a version bump and update the conflicting
             * content. This is likely a competency authoring conflict that needs
             * to be resolved from a content consistency standpoint.
             */
            log.error("savedCompetency: {}", savedCompetency);
            log.error("competencyCheck: {}", competencyCheck);
            throw new MongoException("Insert failed -- unique constraint violation or data propagation failed");
        }

        publishLatestVersion(savedCompetency);
        return savedCompetency;
    }

    private void publishLatestVersion(Competency latestCompetency) {
        List<Competency> competencyList = null;
        // Guarantee Mongo propagation.
        for (int i = 0; i < 10 && (competencyList == null || !competencyList.contains(latestCompetency)); ++i) {
            competencyList = competencyRepository.findByDisciplineAndIdentifierOrderByVersionDesc(
                    latestCompetency.getDiscipline(), latestCompetency.getIdentifier());
            if (competencyList == null || !competencyList.contains(latestCompetency)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // Seriously don't care about this exception.
                }
            }
        }
        Iterator<Competency> competencyIterator = competencyList.iterator();
        Boolean published = true;
        while (competencyIterator.hasNext()) {
            Competency competency = competencyIterator.next();
            if (competency.isPublished() != published) {
                competency.setPublished(published);
                competencyRepository.save(competency);
                log.debug(String.format("Competency published: %s : %s : %s : %s : %s : %s", competency.getId(),
                        competency.getDiscipline(), competency.getIdentifier(), competency.getVersion(),
                        competency.isPublished(), published));
            }
            published = false;
        }
    }

    @Override
    public List<CompetencyDto> create(List<CompetencyDto> competencyDtos) {
        List<Competency> storedCompetencies = new ArrayList<>();

        Map<String,CompetencyDto> identifierVersionMap = new HashMap<>();
        competencyDtos.forEach(c -> identifierVersionMap.put(c.getIdentifier(),c));
        
        for (CompetencyDto competencyDto : competencyDtos) {
            updateCompetencyRefVersion(competencyDto.getChildCompetencyDtoRefs(), identifierVersionMap);
            storedCompetencies.add(create(competencyDto));
        }
        return competencyDtoAssembler.assembleDto(storedCompetencies);
    }

    private void updateCompetencyRefVersion(List<CompetencyRefDto> refs, Map<String,CompetencyDto> identifierVersionMap) {
        if(CollectionUtils.isNotEmpty(refs)) {
            refs.forEach(r -> r.setVersion(getCurrentVersion(r,identifierVersionMap)));
        }
    }

    private Integer getCurrentVersion(CompetencyRefDto ref, Map<String,CompetencyDto> identifierVersionMap) {
        CompetencyDto foundCompetencyDto = identifierVersionMap.get(ref.getCompetencyIdentifier());
        if (foundCompetencyDto != null) {
            return foundCompetencyDto.getVersion();
        } else {
            Competency foundCompetency =  competencyRepository.findTopByDisciplineAndIdentifierAndPublishedOrderByVersionDesc(
                    ref.getDiscipline(),ref.getCompetencyIdentifier(), true);
            if (foundCompetency != null) {
                return foundCompetency.getVersion();
            } else {
                String message = String.format("competency reference %s %s cannot be found.", ref.getDiscipline(), ref.getCompetencyIdentifier());
                throw new PoorlyFormedRequestException(message);
            }
        }
    }

    @Override
    public void delete(String id) {
        competencyRepository.deleteById(id);
    }

    @Override
    public CompetencyDto read(String id) {
        Competency competency = competencyRepository.findById(id).get();
        return competencyDtoAssembler.assembleDto(competency);
    }

    @Override
    public CompetencyDto readLatestPublishedVersion(ScopedIdentifier identifier) {
        List<Competency> competencies = competencyRepository
                .findByDisciplineAndIdentifierAndPublishedOrderByVersionDesc(
                        identifier.getNamespace(), identifier.getIdentifier(), true );
        if (CollectionUtils.isEmpty(competencies)){
            return null;
        }
        return competencyDtoAssembler.assembleDto(competencies.get(0));
    }

    @Override
    public List<CompetencyDto> read(ScopedIdentifier identifier) {
        List<Competency> competencies = competencyRepository
                .findByDisciplineAndIdentifierOrderByVersionDesc(
                        identifier.getNamespace(), identifier.getIdentifier() );
        if (CollectionUtils.isEmpty(competencies)){
            return null;
        }

        return competencyDtoAssembler.assembleDto(competencies);
    }



    @Override
    public List<CompetencyDto> readPublishedUnique() {
        Iterable<Competency> competencies = competencyRepository.findByPublished(true);
        Map<String, Competency> mapOfCompetencies = new HashMap<>();

        for (Competency competency : competencies) {
            if (mapOfCompetencies.containsKey(competency.getIdentifier())) {
                Competency storeCompetency = mapOfCompetencies.get(competency.getIdentifier());
                if (storeCompetency.getVersion() < competency.getVersion()) {
                    mapOfCompetencies.put(competency.getIdentifier(), competency);
                }
            } else {
                mapOfCompetencies.put(competency.getIdentifier(), competency);
            }
        }
        return competencyDtoAssembler.assembleDto(competencies);
    }

    @Override
    public int getNextVersion(String namespace, String identifier) {
        int nextVersion = findMaxVersion(namespace, identifier);
        if (nextVersion == -1 || nextVersion == 0) { // -1 is returned if there
            // is no previous version
            // (upload).
            nextVersion = 1; // We'll start with version 1
        } else {
            ++nextVersion;
        }
        return nextVersion;
    }

    private int findMaxVersion(String discipline, String identifier) {
        List<Competency> competencys = competencyRepository.findByDisciplineAndIdentifierOrderByVersionDesc(discipline,
                identifier);
        if (CollectionUtils.isEmpty(competencys)) {
            return -1;
        }
        Competency competency = competencys.get(0);
        return competency.getVersion();
    }

    @Override
    public Boolean setPublishFlag(String identifier, String discipline, int version, boolean isPublished) {
        Boolean valueAfterSaving = null;
        Competency competency = competencyRepository.findByDisciplineAndIdentifierAndVersion(discipline, identifier,
                version);
        if (competency != null) {
            competency.setPublished(isPublished);
            competencyRepository.save(competency);
            valueAfterSaving = competency.isPublished();
        }
        return valueAfterSaving;
    }

    @Override
    public List<CompetencyDto> read() {
        Iterable<Competency> competencies = competencyRepository.findAll();
        return competencyDtoAssembler.assembleDto(IteratorUtils.toList(competencies.iterator()));
    }

    @Override
    public CompetencyDto readByDisciplineIdentifierAndVersion(String discipline,String identifier, int version) {
        Competency competency = competencyRepository.findByDisciplineAndIdentifierAndVersion(discipline, identifier, version);
        return competencyDtoAssembler.assembleDto(competency);
    }

    public List<CompetencyDto> readAll(List<String> ids) {
        return competencyDtoAssembler.assembleDto(competencyRepository.findAllById(ids));
    }
}
