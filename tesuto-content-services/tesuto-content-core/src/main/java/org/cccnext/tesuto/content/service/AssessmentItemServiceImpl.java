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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.assembler.item.AssessmentItemDtoAssembler;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.cccnext.tesuto.content.repository.mongo.AssessmentItemRepository;
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
@Service
public class AssessmentItemServiceImpl implements AssessmentItemService {

    @Autowired
    private AssessmentItemDtoAssembler assessmentItemDtoAssembler;
    @Autowired
    private AssessmentItemRepository assessmentItemRepository;
    
    @Autowired(required=false)//no required for cached backed service for preview
    MongoOperations mongoOperations;

    public void setAssessmentItemRepository(AssessmentItemRepository assessmentItemRepository) {
        this.assessmentItemRepository = assessmentItemRepository;
    }

    public AssessmentItemRepository getAssessmentItemRepository() {
        return assessmentItemRepository;
    }

    @Override
    public AssessmentItemDto create(AssessmentItemDto assessmentItemDto) {

        AssessmentItem assessmentItem = preProcessForCreate(assessmentItemDto);
        AssessmentItem savedItem = assessmentItemRepository.save(assessmentItem);
        return assessmentItemDtoAssembler.assembleDto(postProcessForCreate(savedItem));
    }

    private AssessmentItem preProcessForCreate(AssessmentItemDto assessmentItemDto) {
        if (StringUtils.isBlank(assessmentItemDto.getNamespace())) {
            throw new PoorlyFormedRequestException("To create an assessment item, a namespace is required");
        }

        AssessmentItem assessmentItem = assessmentItemDtoAssembler.disassembleDto(assessmentItemDto);
        if (StringUtils.isBlank(assessmentItem.getId())) {
            assessmentItem.setId(TesutoUtils.newId());
        }
        return assessmentItem;
    }

    private AssessmentItem postProcessForCreate(AssessmentItem savedItem) {
        // Let's detect a possible race condition of 2 items being uploaded at
        // the same time.
        AssessmentItem assessmentItemCheck = assessmentItemRepository.findByNamespaceAndIdentifierAndVersion(
                savedItem.getNamespace(), savedItem.getIdentifier(), savedItem.getVersion());
        if (!savedItem.getId().equals(assessmentItemCheck.getId())) { // Note we are checking the Mongo GUID here.
            /*
             * A Mongo insert silently fails if there is already a document with
             * the same (namespace, identifier, version), because of a unique
             * index 2 AssessmentItems were uploaded with the same author
             * namespace, identifier, and version. The first upload is kept. The
             * second one causes this error because the version is the same.
             * Retrying this upload will cause a version bump and update the
             * conflicting content. This is likely a content authoring conflict
             * that needs to be resolved from a content consistency standpoint.
             */
            throw new MongoException("Insert failed -- unique constraint violation");
        }

        publishLatestVersion(savedItem.getNamespace(), savedItem.getIdentifier());
        return savedItem;
    }

    private void publishLatestVersion(String namespace, String identifier) {
        List<AssessmentItem> assessmentItems = assessmentItemRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);

        Iterator<AssessmentItem> assessmentIterator = assessmentItems.iterator();

        Boolean published = true;
        if (assessmentIterator.hasNext()) {
            do {
                AssessmentItem assessmentItem = assessmentIterator.next();
                if (published != assessmentItem.isPublished()) {
                    assessmentItem.setPublished(published);
                    log.debug(String.format("Assessment Item published: %s : %s : %s : %s published: %s",
                            assessmentItem.getId(), assessmentItem.getNamespace(), assessmentItem.getIdentifier(),
                            assessmentItem.getVersion(), assessmentItem.isPublished()));
                    assessmentItemRepository.save(assessmentItem);
                }
                published = false;
            } while (assessmentIterator.hasNext());
        }
    }

    @Override
    public List<AssessmentItemDto> create(List<AssessmentItemDto> assessmentItemDtos) {
        List<AssessmentItem> assessmentItems = assessmentItemDtos.stream().map(dto -> preProcessForCreate(dto))
                .collect(Collectors.toList());

        Iterable<AssessmentItem> savedItems = assessmentItemRepository.saveAll(assessmentItems);

        for (AssessmentItem savedItem : savedItems) {
            postProcessForCreate(savedItem);
            publishLatestVersion(savedItem.getNamespace(), savedItem.getIdentifier());
        }

        return assessmentItemDtoAssembler.assembleDto(savedItems);
    }

    @Override
    public AssessmentItemDto read(String id) {
        AssessmentItem assessmentItem = assessmentItemRepository.findById(id).get();
        return assessmentItemDtoAssembler.assembleDto(assessmentItem);
    }

    @Override
    public List<AssessmentItemDto> read() {
        Iterable<AssessmentItem> assessmentItems = assessmentItemRepository.findAll();
        List<AssessmentItemDto> assessmentItemDtos = new LinkedList<AssessmentItemDto>();
        for (AssessmentItem assessmentItem : assessmentItems) {
            AssessmentItemDto assessmentItemDto = assessmentItemDtoAssembler.assembleDto(assessmentItem);
            assessmentItemDtos.add(assessmentItemDto);
        }
        return assessmentItemDtos;
    }

    /**
     * The reason this works is because there is a compound index
     * on the namespace, identifier and version fields.
     *
     * @param namespace
     * @param identifier
     * @return
     */
    @Override
    public AssessmentItemDto readLatestVersion(String namespace, String identifier) {
        AssessmentItem assessmentItem = assessmentItemRepository.findTopByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);
        return assessmentItemDtoAssembler.assembleDto(assessmentItem);
    }

    @Override
    public List<AssessmentItemDto> readAllRevisions(String namespace, String identifier) {
        List<AssessmentItem> assessmentItemList = assessmentItemRepository.findByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);
        List<AssessmentItemDto> assessmentItemDtoList;
        if (CollectionUtils.isEmpty(assessmentItemList)) {
            assessmentItemDtoList = new LinkedList<>();
        } else {
            assessmentItemDtoList = assessmentItemDtoAssembler.assembleDto(assessmentItemList);
        }
        return assessmentItemDtoList;
    }

    @Override
    public AssessmentItemDto readLatestPublishedVersion(String namespace, String itemId) {
        AssessmentItem assessmentItem = assessmentItemRepository.findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(namespace, itemId, true);
        return assessmentItemDtoAssembler.assembleDto(assessmentItem);
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
        AssessmentItem assessmentItem = assessmentItemRepository.findTopByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);
        if (assessmentItem == null) {
            return -1;
        }
        return assessmentItem.getVersion();
    }

    @Override
    public Boolean setPublishFlag(String namespace, String identifier, int version, boolean isPublished) {
        Boolean valueAfterSaving = null;
        AssessmentItem assessmentItem = assessmentItemRepository.findByNamespaceAndIdentifierAndVersion(namespace,
                identifier, version);
        if (assessmentItem != null) {
            assessmentItem.setPublished(isPublished);
            assessmentItemRepository.save(assessmentItem);
            valueAfterSaving = assessmentItem.isPublished();
        }
        return valueAfterSaving;
    }

    @Override
    public void delete(String id) {
        assessmentItemRepository.deleteById(id);
    }


    @Override
    public List<AssessmentItemDto> getAllVersions(String namespace, String identifier) {
        List<AssessmentItem> assessmentItems = assessmentItemRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc(namespace, identifier);

        if (CollectionUtils.isEmpty(assessmentItems)) {
            return null;
        }
        return assessmentItemDtoAssembler.assembleDto(assessmentItems);
    }
    
    @Override
    public  List<AssessmentItemDto> getItemsByCompetency(String mapDiscipline, String competencyIdentifier) {
        List<AssessmentItem> assessmentItems =  assessmentItemRepository.findByCompetency(mapDiscipline, competencyIdentifier);
        return assessmentItemDtoAssembler.assembleDto(assessmentItems);
    }

	@Override
	public List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(String competencyMapDisicpline, List<String> fields) {
			if (StringUtils.isBlank(competencyMapDisicpline)) {
	            return new ArrayList<>(0);
	        }
			Map<String, AssessmentItem> foundItems = new HashMap<>();

	       getItemsByQuery(foundItems, 
	    		   "itemMetadata.competencies.competencyRef.competencyMapDiscipline",
		   			 competencyMapDisicpline, 
		   			fields);       
	       
	       getItemsByQuery(foundItems, 
	   			 "itemMetadata.competencies.competencyRef.mapDiscipline",
	   			 competencyMapDisicpline, 
	   			fields);
	        
	        return assessmentItemDtoAssembler.assembleDto(foundItems.values());
	}
	
	private  void getItemsByQuery(Map<String, AssessmentItem> foundItems, 
			String queryString,
			String competencyMapDisicpline, 
			List<String> fields) {
			
			Criteria criteria = where(queryString).regex(competencyMapDisicpline, "i");			
			final Query query  = query(criteria);
	       
	       if(CollectionUtils.isNotEmpty(fields)) {
	    	   fields.forEach(f -> query.fields().include(f));
	       }
	       //query.fields().include("itemMetadata.competencies.competencyRef.mapDiscipline");
	        List<AssessmentItem> items = mongoOperations.find(query, AssessmentItem.class);
	        
	        if(!CollectionUtils.isNotEmpty(items)) {
	        	for(AssessmentItem item:items) {
	        		if(!foundItems.containsKey(item.getId())){
	        			foundItems.put(item.getId(), item);
	        		}
	        	}
	        }
	}
}
