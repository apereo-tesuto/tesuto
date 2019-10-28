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
package org.cccnext.tesuto.importer.qti.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.cccnext.tesuto.content.repository.mongo.AssessmentItemRepository;


import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class AssessmentItemRepositoryStub implements AssessmentItemRepository {

    Cache assessmentItemCache;

    public AssessmentItemRepositoryStub(Cache assessmentItemCache) {
        this.assessmentItemCache = assessmentItemCache;
    }

    @Override
    public long count() {
        return 0;
    }

    public void delete(String assessmentItemId) {
        delete(getOne(assessmentItemId));
    }

    @Override
    public void delete(AssessmentItem assessmentItem) {
        assessmentItemCache.evict(assessmentItem.getId());
        assessmentItemCache.evict(getKey(assessmentItem));
        assessmentItemCache.evict(getKeyVersion(assessmentItem));
    }

    public void delete(Iterable<? extends AssessmentItem> assessmentItems) {
        for (AssessmentItem assessmentItem : assessmentItems) {
            delete(assessmentItem);
        }
    }

    @Override
    public void deleteAll() {
        assessmentItemCache.clear();
    }

    public boolean exists(String assessmentItemId) {
        if (assessmentItemCache.get(assessmentItemId) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Iterable<AssessmentItem> findAll() {
        throw new UnsupportedOperationException("Caches do not support find all objects");
    }

    public Iterable<AssessmentItem> findAll(Iterable<String> assessmentItemIds) {
        List<AssessmentItem> assessmentItemList = new ArrayList<AssessmentItem>();
        for (String assessmentItemId : assessmentItemIds) {
            assessmentItemList.add((AssessmentItem) assessmentItemCache.get(assessmentItemId).get());
        }
        return assessmentItemList;
    }

    public AssessmentItem getOne(String assessmentItemId) {
        return (AssessmentItem) assessmentItemCache.get(assessmentItemId).get();
    }

    @Override
    public <S extends AssessmentItem> S save(S assessmentItem) {
        assessmentItemCache.put(assessmentItem.getId(), assessmentItem);
        assessmentItemCache.put(getKeyVersion(assessmentItem), assessmentItem);
        ValueWrapper storedItemWrapper = assessmentItemCache.get(getKey(assessmentItem));
        if (storedItemWrapper != null) {
            AssessmentItem storedItem = (AssessmentItem) storedItemWrapper.get();
            if (storedItem.getVersion() > assessmentItem.getVersion()) {
                return (S) storedItem;
            }
        }
        assessmentItemCache.put(getKey(assessmentItem), assessmentItem);

        return assessmentItem;
    }

    public <S extends AssessmentItem> Iterable<S> save(Iterable<S> assessmentItems) {
        List<AssessmentItem> assessmentItemList = new ArrayList<AssessmentItem>();
        for (AssessmentItem assessmentItem : assessmentItems) {
            assessmentItemList.add(save(assessmentItem));
        }
        return (Iterable<S>) assessmentItemList;
    }

    @Override
    public AssessmentItem findByNamespaceAndIdentifierAndVersion(String namespace, String identifier, int version) {
        ValueWrapper wrappedItem = assessmentItemCache.get(getKey(namespace, identifier, version));
        if (wrappedItem == null) {
            return null;
        }
        return (AssessmentItem) wrappedItem.get();
    }

    @Override
    public List<AssessmentItem> findByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier) {
        List<AssessmentItem> foundAssessmentItems = new ArrayList<AssessmentItem>();
        ValueWrapper wrappedItem = assessmentItemCache.get(getKey(namespace, identifier));
        if (wrappedItem != null) {
            foundAssessmentItems.add((AssessmentItem) wrappedItem.get());
        }
        return foundAssessmentItems;
    }

    @Override
    public AssessmentItem findTopByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier) {
        Cache.ValueWrapper item = assessmentItemCache.get(getKey(namespace, identifier));
        if (item == null)
            return null;
        else
            return (AssessmentItem) item.get();
    }

    @Override
    public List<AssessmentItem> findByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String itemId, Boolean isPublished) {
        // TODO: We should not ignore the published flag in the future.  Right now it does not matter for preview because
        // the latest is always published.
        return findByNamespaceAndIdentifierOrderByVersionDesc(namespace, itemId);
    }

    @Override
    public AssessmentItem findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String itemId, Boolean isPublished) {
        return (AssessmentItem) assessmentItemCache.get(getKey(namespace, itemId)).get();
    }

    private String getKey(AssessmentItem assessmentItem) {
        String namespace = assessmentItem.getNamespace();
        String identifier = assessmentItem.getIdentifier();
        boolean published = assessmentItem.isPublished();
        return String.format("%s:%s", namespace, identifier, published);
    }

    private String getKeyVersion(AssessmentItem assessmentItem) {
        String namespace = assessmentItem.getNamespace();
        String identifier = assessmentItem.getIdentifier();
        int version = assessmentItem.getVersion();
        return String.format("%s:%s:%s", namespace, identifier, version);
    }

    private String getKey(String namespace, String identifier) {
        return String.format("%s:%s", namespace, identifier);
    }

    private String getKey(String namespace, String identifier, int version) {
        return String.format("%s:%s:%s", namespace, identifier, version);
    }

    @Override
    public List<AssessmentItem> findByCompetency(String mapDiscipline, String competencyIdentifier) {
        throw new UnsupportedOperationException("Caches do not support find all objects");
    }

	public void deleteAll(Iterable<? extends AssessmentItem> assessmentItems) {
		this.delete(assessmentItems);
	}

	public void deleteById(String assessmentItemId) {
		this.delete(assessmentItemId);
		
	}

	public boolean existsById(String assessmentItemId) {
		return this.exists(assessmentItemId);
	}

	public Iterable<AssessmentItem> findAllById(Iterable<String> assessmentItemIds) {
		return this.findAll(assessmentItemIds);
	}

	public Optional<AssessmentItem> findById(String assessmentItemId) {
		return Optional.of(getOne(assessmentItemId));
	}
	
	public <S extends AssessmentItem> Iterable<S> saveAll(Iterable<S> AssessmentItems) {
		return this.save(AssessmentItems);
	}

	@Override
	public AssessmentItem findOne(String arg0) {
		return getOne(arg0);
	}

}
