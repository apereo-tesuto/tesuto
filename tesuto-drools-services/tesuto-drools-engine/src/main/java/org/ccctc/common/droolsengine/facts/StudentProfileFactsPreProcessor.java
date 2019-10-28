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
import org.ccctc.common.droolsengine.utils.SecurityUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * StudentProfileFactsPreProcessor will make a call to the profile service and add
 * the results to the list of available facts. Currently, the profile service
 * returns very little usable data to the validator. In the future, we would
 * expect that more information is available.
 * <p>
 * This validator may not be required and can be disabled if desired.
 * </p>
 */
@Slf4j
@Service
public class StudentProfileFactsPreProcessor extends AbstractFactsPreProcessor {
    private final static String NAME = "STUDENT_PROFILE";

    public StudentProfileFactsPreProcessor() {
        name = NAME;
    }

    @Autowired
    private FactsUtils factsUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private StudentProfileFacade studentProfileFacade;

    String getCccid(Map<String, Object> facts) {
        String cccid = (String) facts.get(FactsUtils.CCCID_FIELD);
        if (StringUtils.isBlank(cccid)) {
            return "";
        }
        return cccid;
    }

    String getFamily(Map<String, Object> facts) {
        String cccMisCode = (String) facts.get(FactsUtils.MISCODE_FIELD);
        if (StringUtils.isBlank(cccMisCode)) {
            cccMisCode = securityUtils.getFamily();
        }
        if (!StringUtils.isBlank(cccMisCode)) {
            facts.put(FactsUtils.MISCODE_FIELD, cccMisCode);
        }
        return cccMisCode;
    }

    @Override
    public boolean isEnabled(Map<String, Object> facts) {
        // enabled in the following circumstances
        // 1) not explicitly disabled in config
        // 2) no _VALIDATOR_MISCODEs defined in config for this validator
        // 3) ALL is one of the defined _VALIDATOR_MISCODEs defined in config for this validator
        // 4) Facts MISCODE is in the list of _VALIDATOR_MISCODEs defined in config for this validator
        if (!enabled) {
            return false;
        }
        List<String> misCodes = config.getValidatorMisCodes(getName());
        if (misCodes.size() == 0) {
            return true;
        }
        if (misCodes.contains("ALL")) {
            return true;
        }
        return misCodes.contains(factsUtils.getFamily(facts));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> processFacts(Map<String, Object> facts) {
        log.debug("Validating facts with StudentProfileFactsPreProcessor");
        List<String> errors = new ArrayList<String>();
        Map<String, Object> studentProfile = (Map<String, Object>) facts.get(FactsUtils.STUDENT_PROFILE_FIELD);
        String cccid = getCccid(facts);
        if (StringUtils.isBlank(cccid)) {
            log.error("Cannot retrieve studentProfile with a blank cccid");
            errors.add("Cannot retrieve studentProfile with a blank cccid");
            return errors;
        }

        if (studentProfile == null) {
            String cccMisCode = getFamily(facts);
            if (StringUtils.isBlank(cccMisCode)) {
                log.error("Cannot retrieve studentProfile with a blank cccMisCode");
                errors.add("Cannot retrieve studentProfile with a blank cccMisCode");
                return errors;
            }
            studentProfile = studentProfileFacade.getStudentProfile(cccid);
        }

        if (studentProfile == null) {
            log.error("Cannot execute RulesEngine without a Student Profile");
            errors.add("Cannot execute RulesEngine without a Student Profile");
            return errors;
        }
        log.debug("StudentProfile:[" + studentProfile + "]");
        studentProfile.forEach((key, value) -> facts.put(key, value));
        return errors;
    }
}
