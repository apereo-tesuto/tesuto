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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.assembler.assessment.AssessmentDtoAssembler;
import org.cccnext.tesuto.content.assembler.view.assessment.AssessmentViewDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.Assessment;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.repository.mongo.AssessmentRepository;
import org.cccnext.tesuto.content.service.AssessmentAccessService;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.util.TesutoUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "assessmentService")
public class AssessmentServiceImpl implements AssessmentService {

	@Autowired(required=false)//not required for preview(cache backed) services
	MongoOperations mongoOperations;
	
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private AssessmentDtoAssembler assessmentDtoAssembler;
    @Autowired
    private AssessmentItemService assessmentItemService; // Setter injection
    @Autowired
    private AssessmentAccessService assessmentAccessService;
    @Autowired
    private AssessmentViewDtoAssembler assessmentViewDtoAssembler;

    public void setAssessmentItemService(AssessmentItemService assessmentItemService) {
        this.assessmentItemService = assessmentItemService;
    }

    public AssessmentItemService getAssessmentItemService() {
        return assessmentItemService;
    }

    public void setAssessmentRepository(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public AssessmentRepository getAssessmentRepository() {
        return assessmentRepository;
    }

    public void setAssessmentAccessService(AssessmentAccessService assessmentAccessService) {
        this.assessmentAccessService = assessmentAccessService;
    }

    public AssessmentViewDtoAssembler getAssessmentViewDtoAssembler() {
        return assessmentViewDtoAssembler;
    }

    public void setAssessmentViewDtoAssembler(AssessmentViewDtoAssembler assessmentViewDtoAssembler) {
        this.assessmentViewDtoAssembler = assessmentViewDtoAssembler;
    }

    public AssessmentAccessService getAssessmentAccessService() {
        return assessmentAccessService;
    }

    @Override
    public AssessmentDto create(AssessmentDto assessmentDto) {
        if (StringUtils.isBlank(assessmentDto.getNamespace())) {
            throw new PoorlyFormedRequestException("To create an assessment, a namespace is required");
        }
        Assessment assessment = assessmentDtoAssembler.disassembleDto(assessmentDto);
        if (StringUtils.isBlank(assessment.getId())) {
            assessment.setId(TesutoUtils.newId());
        }

        Assessment savedAssessment = assessmentRepository.save(assessment);
        // Let's detect a possible race condition of 2 assessments being
        // uploaded at the same time.
        Assessment assessmentCheck = assessmentRepository.findByNamespaceAndIdentifierAndVersion(
                savedAssessment.getNamespace(), savedAssessment.getIdentifier(), savedAssessment.getVersion());

		 /*
         *  The assessmentCheck is null, if Mongo is still saving the assessment and the node that is used for the read
         *  does not have the record. We assume that there will be no conflict in that case.
         */
        if (assessmentCheck != null) {
            if (!savedAssessment.getId().equals(assessmentCheck.getId())) { // Note we are checking the Mongo GUID here.
                /*
                 * A Mongo insert silently fails if there is already a document with
                 * the same (namespace, identifier, version), because of a unique
                 * index 2 Assessment were uploaded with the same author namespace,
                 * identifier, and version. The first upload is kept. The second one
                 * causes this error because the version is the same. Retrying this
                 * upload will cause a version bump and update the conflicting
                 * content. This is likely a content authoring conflict that needs
                 * to be resolved from a content consistency standpoint.
                 */
                throw new MongoException("Insert failed -- unique constraint violation");
            }
        }

        publishLatestVersion(savedAssessment.getNamespace(), savedAssessment.getIdentifier());

        return assessmentDtoAssembler.assembleDto(savedAssessment);
    }

    // TODO: find usages of this.
    private void publishLatestVersion(String namespace, String identifier) {
        List<Assessment> assessments = assessmentRepository.findByNamespaceAndIdentifierOrderByVersionDesc(namespace,
                identifier);
        Iterator<Assessment> assessmentIterator = assessments.iterator();
        Boolean published = true;
        while (assessmentIterator.hasNext()) {
            Assessment assessment = assessmentIterator.next();
            if (assessment.isPublished() != published) {
                assessment.setPublished(published);
                assessmentRepository.save(assessment);
                log.debug(String.format("Assessment Item published: %s : %s : %s : %s : %s", assessment.getId(),
                        assessment.getNamespace(), assessment.getIdentifier(), assessment.getVersion(),
                        assessment.isPublished()));
            }
            published = false;
        }
    }

    @Override
    public List<AssessmentDto> create(List<AssessmentDto> assessmentDtos) {
        List<AssessmentDto> storedAssessments = new ArrayList<AssessmentDto>();
        for (AssessmentDto assessmentDto : assessmentDtos) {
            storedAssessments.add(create(assessmentDto));
        }
        return storedAssessments;
    }

    @Override
    public void delete(String id) {
        assessmentRepository.deleteById(id);
    }

    @Override
    public AssessmentDto read(String id) {
        Assessment assessmentDto = assessmentRepository.findById(id).get();
        return assessmentDtoAssembler.assembleDto(assessmentDto);
    }

    @Override
    public List<AssessmentDto> read() {
        Iterable<Assessment> assessments = assessmentRepository.findAll();
        return assessmentDtoAssembler.assembleDto(IteratorUtils.toList(assessments.iterator()));
    }

    @Override
    public List<AssessmentDto> readPublishedUnique() {
        Iterable<Assessment> assessments = assessmentRepository.findByPublished(true);
        Map<String, Assessment> assessmentsMap = new HashMap<String, Assessment>();

        for (Assessment assessment : assessments) {
            if (assessmentsMap.containsKey(assessment.getIdentifier())) {
                Assessment storedAssessment = assessmentsMap.get(assessment.getIdentifier());
                if (storedAssessment.getVersion() < assessment.getVersion()) {
                    assessmentsMap.put(assessment.getIdentifier(), assessment);
                }
            } else {
                assessmentsMap.put(assessment.getIdentifier(), assessment);
            }
        }
        return assessmentDtoAssembler.assembleDto(IteratorUtils.toList(assessments.iterator()));
    }

    @Override
    public List<AssessmentDto> readPublishedUniqueForUserAndLocation(String userId, String locationId) {
        Set<ScopedIdentifier> allowedAsssessments = assessmentAccessService.getAllowedAssessments(userId, locationId);
        List<AssessmentDto> assessments = new ArrayList<AssessmentDto>();

        for (ScopedIdentifier allowedAsssessment : allowedAsssessments) {
            AssessmentDto assessmentDto = readLatestPublishedVersion(allowedAsssessment);
            if (assessmentDto != null) {
                assessments.add(assessmentDto);
            }
        }

        return assessments;
    }

    @Override
    public AssessmentMetadataDto readLatestPublishedVersionMetadata(ScopedIdentifierDto identifier) {
        AssessmentDto assessmentDto = readLatestPublishedVersion(identifier);
        if (assessmentDto == null || assessmentDto.getAssessmentMetadata() == null) {
            return null;
        }
        return assessmentDto.getAssessmentMetadata();
    }
    
    @Override
    public AssessmentDto readLatestPublishedVersion(ScopedIdentifier identifier) {
        Assessment assessment = assessmentRepository.findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(identifier.getNamespace(), identifier.getIdentifier(), true);
        return assessmentDtoAssembler.assembleDto(assessment);
    }

    @Override
    public AssessmentDto readLatestPublishedVersion(ScopedIdentifierDto identifier) {
        Assessment assessment = assessmentRepository.findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(identifier.getNamespace(), identifier.getIdentifier(), true);
        return assessmentDtoAssembler.assembleDto(assessment);
    }

    @Override
    public List<AssessmentDto> readAllRevisions(String namespace, String identifier) {
        List<Assessment> assessmentList = assessmentRepository.findByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);
        List<AssessmentDto> assessmentDtoList;
        if (CollectionUtils.isEmpty(assessmentList)) {
            assessmentDtoList = new LinkedList<>();
        } else {
            assessmentDtoList = assessmentDtoAssembler.assembleDto(assessmentList);
        }
        return assessmentDtoList;
    }

    @Override
    public List<AssessmentDto> read(ScopedIdentifier identifier) {
        List<Assessment> assessments = assessmentRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc(identifier.getNamespace(), identifier.getIdentifier());
        if (CollectionUtils.isEmpty(assessments)) {
            return null;
        }
        return assessmentDtoAssembler.assembleDto(assessments);
    }

    @Override
    public AssessmentDto readVersion(ScopedIdentifier identifier, int version) {
        Assessment assessment =
                assessmentRepository.findByNamespaceAndIdentifierAndVersion(identifier.getNamespace(), identifier.getIdentifier(), version);
        return assessmentDtoAssembler.assembleDto(assessment);
    }

    @Override
    public AssessmentDto generateLinearAssessmentFromAssessmentItems(List<AssessmentItemDto> assessmentItems,
            String namespace) {
        AssessmentDto assessment = new AssessmentDto();

        assessment.setId(TesutoUtils.newId());
        assessment.setIdentifier("linear-assessment:" + assessment.getId());
        assessment.setNamespace(namespace);
        assessment.setTitle("Basic Linear Assesment From Assessment Items:" + assessment.getId());
        assessment.setVersion(1);
        assessment.setDuration(1000000.0);
        AssessmentPartDto assessmentPart = new AssessmentPartDto();

        assessmentPart.setAssessmentPartNavigationMode(AssessmentPartNavigationMode.LINEAR);
        AssessmentItemSessionControlDto assessmentItemSessionControlDto = new AssessmentItemSessionControlDto();
        assessmentItemSessionControlDto.setAllowSkipping(true);
        assessmentItemSessionControlDto.setValidateResponses(false);
        assessmentPart.setItemSessionControl(assessmentItemSessionControlDto);

        AssessmentSectionDto assessmentSection = new AssessmentSectionDto();
        assessmentSection.setId("Section");

        List<AssessmentItemRefDto> assessmentItemRefs = new ArrayList<AssessmentItemRefDto>();
        List<AssessmentComponentDto> assessmentComponents = new ArrayList<AssessmentComponentDto>();
        List<AssessmentSectionDto> assessmentSections = Collections.emptyList();
        for (AssessmentItemDto assessmentItem : assessmentItems) {
            AssessmentItemRefDto assessmentItemRefDto = new AssessmentItemRefDto();
            assessmentItemRefDto.setItemIdentifier(assessmentItem.getIdentifier());
            assessmentItemRefDto.setIdentifier(assessmentItem.getId());
            assessmentItemRefDto.setIsFixed(true);
            assessmentItemRefDto.setIsRequired(true);
            assessmentItemRefs.add(assessmentItemRefDto);
            assessmentComponents.add((AssessmentComponentDto) assessmentItemRefDto);
        }

        assessmentSection.setAssessmentItemRefs(assessmentItemRefs);
        assessmentSection.setAssessmentComponents(assessmentComponents);
        assessmentSection.setAssessmentSections(assessmentSections);
        assessmentPart.setAssessmentSections(Arrays.asList(assessmentSection));
        assessment.setAssessmentParts(Arrays.asList(assessmentPart));
        return assessment;
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

    // TODO: Optimize this by a more targeted fetch of just the version field.
    private int findMaxVersion(String namespace, String identifier) {
        Assessment assessment = assessmentRepository.findTopByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);
        if (assessment == null) {
            return -1;
        }
        return assessment.getVersion();
    }

    @Override
    public Boolean setPublishFlag(String namespace, String identifier, int version, boolean isPublished) {
        Boolean valueAfterSaving = null;
        Assessment assessment = assessmentRepository.findByNamespaceAndIdentifierAndVersion(namespace, identifier,
                version);
        if (assessment != null) {
            assessment.setPublished(isPublished);
            assessmentRepository.save(assessment);
            valueAfterSaving = assessment.isPublished();
        }
        return valueAfterSaving;
    }

    @Override
    public AssessmentViewDto readViewDto(String namespace, String identifier) {
        AssessmentDto assessmentDto = readLatestPublishedVersion(namespace, identifier);
        return assessmentViewDtoAssembler.assembleViewDto(assessmentDto);
    }

    @Override
    public List<Integer> readVersions(ScopedIdentifier identifier) {
        List<Integer> versions = new ArrayList<>();
        List<Assessment> assessments = assessmentRepository.findVersionsByNamespaceAndIdentifier(identifier.getNamespace(), identifier.getIdentifier());
        for (Assessment assessment : assessments) {
            versions.add(assessment.getVersion());
        }
        return versions;
    }
    

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline) {
		if (StringUtils.isBlank(competencyMapDiscipline)) {
			return new ArrayList<>(0);
		}
		Criteria criteria = null;

		criteria = where("assessmentMetadata.competencyMapDisciplines").is(competencyMapDiscipline);

		if (criteria == null) {
			return new ArrayList<>();
		}
		Query query = query(criteria);

		return assessmentDtoAssembler.assembleDto(mongoOperations.find(query, Assessment.class));
	}

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String identiferRegExpression) {
		if (StringUtils.isBlank(competencyMapDiscipline)) {
			return new ArrayList<>(0);
		}
		Criteria criteria = null;

		criteria = where("assessmentMetadata.competencyMapDisciplines").regex(competencyMapDiscipline, "i");
		
		if (criteria == null) {
			return new ArrayList<>();
		}
		Query query = query(criteria);
		List<Assessment> assessments = mongoOperations.find(query, Assessment.class);

		Criteria criteriaIdentifer = where("identifier").regex(identiferRegExpression, "i");

		Query query2 = query(criteriaIdentifer);

		List<Assessment> assessments2 = mongoOperations.find(query2, Assessment.class);

		List<Assessment> allAssesments = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(assessments)) {
			allAssesments.addAll(assessments);
		}
		if (CollectionUtils.isNotEmpty(assessments2)) {
			allAssesments.addAll(assessments2);
		}
		Map<String, Assessment> uniqueAssessments = new HashMap<>();
		for (Assessment assesment : allAssesments) {
			if (!uniqueAssessments.containsKey(assesment.getId()))
				uniqueAssessments.put(assesment.getId(), assesment);

		}
		log.info(String.format("criteria tested for", identiferRegExpression));
		return assessmentDtoAssembler.assembleDto(uniqueAssessments.values());
	}
	

}
