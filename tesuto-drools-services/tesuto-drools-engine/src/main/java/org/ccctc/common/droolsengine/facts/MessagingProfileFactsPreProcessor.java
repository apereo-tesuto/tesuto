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
package org.ccctc.common.droolsengine.facts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsengine.utils.FactsUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MessagingProfileFactsValidator is used to retrieve data from the profile service that is required to send messages
 * to the student. Additionally, the facts are enhanced with student contact information to allow for better message 
 * personalizations. Although technically this information may not be required, this validator will fail a request if it 
 * is unable to query the student from the profile service.
 * <p>If connecting via SMS and/or email is not required, disable this processor.</p>
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class MessagingProfileFactsPreProcessor extends AbstractFactsPreProcessor {
    private final static String NAME = "MESSAGE_PROFILE";

    @Autowired
    private FactsUtils factsUtils;

    @Autowired
    private StudentProfileFacade studentProfileFacade;

    public MessagingProfileFactsPreProcessor() {
        name = NAME;
    }

    private String getCccid(Map<String, Object> facts) {
        String cccid = (String) facts.get(FactsUtils.CCCID_FIELD);
        return StringUtils.isBlank(cccid) ? "" : cccid;
    }

    @Override
    public boolean isEnabled(Map<String, Object> facts) {
        if (!enabled) {
            return false;
        }
        if (this.misCodes.contains("ALL")) {
            return true;
        }
        return this.misCodes.contains(factsUtils.getFamily(facts));
    }

    /**
     * Enhance the facts with profile data. This ensures that contact details in the facts have the current available details
     * needed to send messages.
     * @see org.ccctc.common.droolsengine.facts.IFactsPreProcessor#processFacts(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<String> processFacts(Map<String, Object> facts) {
        List<String> errors = new ArrayList<String>();
        String cccid = getCccid(facts);
        if (StringUtils.isBlank(cccid)) {
            errors.add("MessagingProfileFactsPreProcessor unable to validate with a blank cccid");
            return errors;
        }

        Map<String, Object> profile = studentProfileFacade.getStudentProfile(cccid);
        if (profile == null) {
            errors.add("MessagingProfileFactsPreProcessor unable to get a profile for cccid: " + cccid);
            return errors;
        }

        log.trace("MessagingProfileFactsPreProcessor: enhancing facts with data from profile: [" + profile + "]");
        facts.put(FactsUtils.MAIN_PHONE_AUTH_FIELD, profile.get(FactsUtils.MAIN_PHONE_AUTH_FIELD));
        facts.put(FactsUtils.MAIN_PHONE_FIELD, profile.get(FactsUtils.MAIN_PHONE_FIELD));
        facts.put(FactsUtils.SECONDARY_PHONE_AUTH_FIELD, profile.get(FactsUtils.SECONDARY_PHONE_AUTH_FIELD));
        facts.put(FactsUtils.SECONDARY_PHONE_FIELD, profile.get(FactsUtils.SECONDARY_PHONE_FIELD));
        facts.put(FactsUtils.EMAIL_FIELD, profile.get(FactsUtils.EMAIL_FIELD));
        facts.put("firstname", profile.get(FactsUtils.FIRST_NAME_FIELD));
        facts.put("lastname", profile.get(FactsUtils.LAST_NAME_FIELD));
        facts.put("middlename", profile.get(FactsUtils.MIDDLE_NAME_FIELD));
        Map colleges = (Map) profile.get("collegeAssociations");
        facts.put("colleges", colleges == null ? new ArrayList<>() : colleges.keySet());

        return errors;
    }
}
