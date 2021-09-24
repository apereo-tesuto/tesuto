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

import org.cccnext.tesuto.content.model.Assessment;
import org.cccnext.tesuto.content.repository.mongo.AssessmentRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

public class AssessmentRepositoryStub implements AssessmentRepository {

    Cache assessmentCache;

    public AssessmentRepositoryStub(Cache assessmentCache) {
        this.assessmentCache = assessmentCache;
    }

    @Override
    public long count() {
        return 0;
    }

    public void delete(String assessmentId) {
        Assessment assessment = getOne(assessmentId);
        delete(assessment);

    }

    @Override
    public void delete(Assessment assessment) {
        assessmentCache.evict(assessment.getId());
        assessmentCache.evict(getKey(assessment));
        assessmentCache.evict(getKeyVersion(assessment));
    }

    public void delete(Iterable<? extends Assessment> assessments) {
        for (Assessment assessment : assessments) {
            delete(assessment);
        }
    }

    @Override
    public void deleteAll() {
        assessmentCache.clear();
    }

    public boolean exists(String assessmentId) {
        if (assessmentCache.get(assessmentId) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Iterable<Assessment> findAll() {
        throw new UnsupportedOperationException("Caches do not support find all objects");
    }

    public Iterable<Assessment> findAll(Iterable<String> assessmentIds) {
        List<Assessment> assessmentList = new ArrayList<Assessment>();
        for (String assessmentId : assessmentIds) {
            assessmentList.add((Assessment) assessmentCache.get(assessmentId).get());
        }
        return assessmentList;
    }

    public Assessment getOne(String assessmentId) {
        Cache.ValueWrapper wrapper = assessmentCache.get(assessmentId);
        return wrapper == null ? null : (Assessment) wrapper.get();
    }

    @Override
    public <S extends Assessment> S save(S assessment) {
        assessmentCache.put(assessment.getId(), assessment);
        assessmentCache.put(getKeyVersion(assessment), assessment);
        ValueWrapper storedAssessmentWrapper = assessmentCache.get(getKey(assessment));
        if (storedAssessmentWrapper != null) {
            Assessment storedAssessment = (Assessment) storedAssessmentWrapper.get();
            if (storedAssessment.getVersion() > assessment.getVersion()) {
                return (S) storedAssessment;
            }
        }
        assessmentCache.put(getKey(assessment), assessment);
        return assessment;
    }

    public <S extends Assessment> Iterable<S> save(Iterable<S> assessments) {
        List<Assessment> assessmentList = new ArrayList<Assessment>();
        for (Assessment assessment : assessments) {
            assessmentList.add(save(assessment));
        }
        return (Iterable<S>) assessmentList;
    }

    @Override
    public Assessment findByNamespaceAndIdentifierAndVersion(String namespace, String identifier, int version) {
        ValueWrapper wrappedItem = assessmentCache.get(getKey(namespace, identifier, version));
        if (wrappedItem == null) {
            return null;
        }
        return (Assessment) wrappedItem.get();
    }

    public List<Assessment> findByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace,
            String identifier, Boolean isPublished) {
        List<Assessment> foundAssessments = new ArrayList<Assessment>();
        ValueWrapper wrapper = assessmentCache.get(getKey(namespace, identifier));
        if (wrapper != null) {
            foundAssessments.add((Assessment) wrapper.get());
        }
        return foundAssessments;
    }

    @Override
    public Assessment findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String identifier, Boolean isPublished) {
        Cache.ValueWrapper wrapper = assessmentCache.get(getKey(namespace, identifier));
        return wrapper == null ? null : (Assessment) wrapper.get();
    }

    @Override
    public List<Assessment> findByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier) {
        return findByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(namespace, identifier, null);
    }

    @Override
    public Assessment findTopByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier) {
        Cache.ValueWrapper wrapper = assessmentCache.get(getKey(namespace, identifier));
        return wrapper == null ? null :(Assessment) wrapper.get();
    }

    private String getKey(Assessment assessment) {
        String namespace = assessment.getNamespace();
        String identifier = assessment.getIdentifier();
        return String.format("%s:%s", namespace, identifier);
    }

    private String getKeyVersion(Assessment assessment) {
        String namespace = assessment.getNamespace();
        String identifier = assessment.getIdentifier();
        int version = assessment.getVersion();
        return String.format("%s:%s:%s", namespace, identifier, version);
    }

    private String getKey(String namespace, String identifier) {
        return String.format("%s:%s", namespace, identifier);
    }

    private String getKey(String namespace, String identifier, int version) {
        return String.format("%s:%s:%s", namespace, identifier, version);
    }

    public List<Assessment> findByPublished(Boolean isPublished) {
        throw new UnsupportedOperationException("Caches do not support find all objects");
    }

    public List<Assessment> findVersionsByNamespaceAndIdentifier(String namespace, String identifier) {
        throw new UnsupportedOperationException("Caches do not support find all objects");
    }

	public void deleteAll(Iterable<? extends Assessment> assessments) {
		this.delete(assessments);
		
	}

	public void deleteById(String assessmentId) {
		this.delete(assessmentId);
		
	}

	public boolean existsById(String assessmentId) {
		return this.exists(assessmentId);
	}

	public Iterable<Assessment> findAllById(Iterable<String> assessmentItemIds) {
		return this.findAllById(assessmentItemIds);
	}

	public Optional<Assessment> findById(String assessmentItemId) {
		return Optional.of(getOne(assessmentItemId));
	}

	public <S extends Assessment> Iterable<S> saveAll(Iterable<S> assessments) {
		return this.save(assessments);
	}

	@Override
	public Assessment findOne(String arg0) {
		return getOne(arg0);
	}
}
