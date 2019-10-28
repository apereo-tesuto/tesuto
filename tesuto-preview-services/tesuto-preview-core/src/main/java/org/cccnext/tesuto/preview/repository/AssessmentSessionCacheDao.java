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
package org.cccnext.tesuto.preview.repository;

import java.util.List;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.DeliverySearchParameters;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 11/10/15.
 */

@Service("assessmentSessionCacheDao")
public class AssessmentSessionCacheDao implements AssessmentSessionDao, InitializingBean {

	private Cache cache;
	@Value("${assessmentSessionCacheName}")
	private String cacheName;
	
	@Autowired
	private CacheManager cacheManager;

	@Override
	public void afterPropertiesSet() {
		cache = cacheManager.getCache(cacheName);
	}

	public Cache sessionCache() {
		return cache;
	}

	/**
	 * @param session
	 * @return the id of the new AssessmentSession
	 */
	@Override
	public String create(AssessmentSession session) {
		if (session.getAssessmentSessionId() == null) {
			session.setAssessmentSessionId(TesutoUtils.newId());
		}
		cache.put(session.getAssessmentSessionId(), session);
		return session.getAssessmentSessionId();
	}

	/**
	 * Delete an assessment session from storage
	 *
	 * @param id
	 * @return boolean indicating whether the assessment session actually existed or
	 *         not.
	 */
	@Override
	public boolean delete(String id) {
		AssessmentSession session = cache.get(id, AssessmentSession.class);
		if (session != null) {
			cache.evict(id);
		}
		return session != null;
	}

	/**
	 * @param id
	 * @return assessment session with the desired id
	 * @throws AssessmentSessionNotFoundException if no assessment session found
	 */
	@Override
	public AssessmentSession find(String id) {
		AssessmentSession session = cache.get(id, AssessmentSession.class);
		if (session == null) {
			throw new AssessmentSessionNotFoundException(id);
		}
		return session;
	}

	/**
	 * Update (or create) the assessment session
	 *
	 * @param session
	 * @return the updated AssessmentSession
	 */
	@Override
	public AssessmentSession save(AssessmentSession session) {
		String id = create(session);
		return find(id);
	}

	@Override
	public List<AssessmentSession> search(DeliverySearchParameters search) {
		throw new UnsupportedOperationException("Caches do not support serach on criteria for all objects");
	}
}
