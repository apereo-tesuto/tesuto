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
package org.ccctc.common.droolsengine.utils;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class FactsUtils {
    public static final String ACTIONS_FIELD = "actions";

    public static final String CCCID_FIELD = "cccid";

    public static final String COOKIES_FIELD = "cookies";

    public static final String EMAIL_FIELD = "email";

    public static final String FIRST_NAME_FIELD = "firstName";

    public static final String LAST_NAME_FIELD = "lastName";

    public static final String MAIN_PHONE_AUTH_FIELD = "mainPhoneAuth";

    public static final String MAIN_PHONE_FIELD = "mainPhone";

    public static final String MIDDLE_NAME_FIELD = "middleName";

    public static final String MISCODE_FIELD = "cccMisCode";

    public static final String OAUTH_TOKEN_FIELD = "oauthToken";

    public static final String SECONDARY_PHONE_AUTH_FIELD = "secondPhoneAuth";
    
    public static final String SECONDARY_PHONE_FIELD = "secondPhone";
    
    public static final String STUDENT_PROFILE_FIELD = "studentProfile";

    /**
     * Builds an HttpHeaders object based on available facts. This utility function is typically used as part of actions that use 
     * RestTemplates to call external systems.
     * <p>
     * Headers built include:
     * <ul>
     * <li>Authorization< with oauth bearer token/li>
     * <li>ContentType of application/json</li>
     * </ul>
     * 
     * @param facts
     * @return HttpHeaders object
     */
    public HttpHeaders buildHeaders(Map<String, Object> facts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String cookies = getCookies(facts);
        if (!StringUtils.isBlank(cookies)) {
            headers.set("Cookie", cookies);
        }
        return headers;
    }

    public Boolean getBooleanField(Map<String, Object> facts, String fieldId) {
        if (!(facts.containsKey(fieldId))) {
            return null;
        }
        Boolean field = (Boolean) facts.get(fieldId);
        return field;
    }

    public String getCccId(Map<String, Object> facts) {
        return getStringField(facts, FactsUtils.CCCID_FIELD);
    }

    public String getFamily(Map<String, Object> facts) {
        return getStringField(facts, FactsUtils.MISCODE_FIELD);
    }

    public String getCookies(Map<String, Object> facts) {
        return getStringField(facts, FactsUtils.COOKIES_FIELD);
    }

    public String getField(Map<String, Object> facts, String fieldId) {
        return getStringField(facts, fieldId);
    }

    public String getOauthToken(Map<String, Object> facts) {
        return getStringField(facts, FactsUtils.OAUTH_TOKEN_FIELD);
    }

    public String getStringField(Map<String, Object> facts, String fieldId) {
        String field = (String) facts.get(fieldId);
        if (StringUtils.isEmpty(field)) {
            return "";
        }
        return field;
    }
}
