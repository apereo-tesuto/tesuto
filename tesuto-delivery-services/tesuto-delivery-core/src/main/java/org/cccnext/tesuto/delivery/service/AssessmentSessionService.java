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
package org.cccnext.tesuto.delivery.service;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.delivery.view.AssessmentSessionRestrictedViewDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;


import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class AssessmentSessionService implements AssessmentSessionReader {


    private AssessmentSessionDao dao;
    

    public AssessmentSessionDao getDao() {
        return dao;
    }

    public void setDao(AssessmentSessionDao dao) {
        this.dao = dao;
    }


	@Override
	public Object findAssessmentSession(String assessmentSessionId)
			throws AssessmentSessionNotFoundException {
		return assembleAssessmentSession(dao.find(assessmentSessionId));
	}
	
    public AssessmentSessionRestrictedViewDto assembleAssessmentSession(AssessmentSession session) {
        if (session == null) {
            return null;
        }
        AssessmentSessionRestrictedViewDto view = new AssessmentSessionRestrictedViewDto();
        view.setAssessmentSessionId(session.getAssessmentSessionId());
        AssessmentDto assessment = session.getAssessment();
        view.setTitle(assessment.getTitle());
        view.setLanguage(assessment.getLanguage());
        view.setAssessmentSettings(session.getAssessmentSettings());
        view.setDeadline(session.getDeadline());
        view.setStylesheets(assessment.getStylesheets());
        view.setUserId(session.getUserId());
        view.setAssessmentVersion(session.getAssessment().getVersion());
        view.setAssessmentContentId(session.getContentId());
        return view;
    }

	@Override
	public Object createContentAssessmentSession(String namespace,
			String identifier) {
		throw new NotImplementedException();
	}

	@Override
	public String createUserAssessmentSession(String userId, ScopedIdentifier scopedIdentifer,
			DeliveryType deliveryType, Map<String, String> settings) {
		throw new NotImplementedException();	}

	@Override
	public String createUserAssessmentSession(String userId, ScopedIdentifier assessmentId, int assessmentVersion,
			DeliveryType deliveryType, Map<String, String> settings) {
		throw new NotImplementedException("createUserAssessmentSession has not been implemented in AssessmentSessionService");
	}

	@Override
	public Integer getAssessmentVersionForSession(String assessmentSessionId) {
		throw new NotImplementedException("getAssessmentVersionForSession has not been implemented in AssessmentSessionService");
	}

}
