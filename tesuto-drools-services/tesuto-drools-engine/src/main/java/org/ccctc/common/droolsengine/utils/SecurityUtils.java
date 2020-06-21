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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class SecurityUtils {

    @Autowired
    private RestTemplate restTemplate = null;

    public String getFamily() {
        String cccMisCode = "";
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.error("RequestContextHolder does not have request attributes");
            return cccMisCode;
        }

        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            log.error("requestAttributes are not ServletRequestAttributes");
            return cccMisCode;
        }

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttribute.getRequest();

        String getUserMisCodeUrl = request.getScheme() + "://" + request.getServerName() + "/uPortal/api/cccUserMISCode/";
        log.debug("misCodeUrl:[" + getUserMisCodeUrl + "]");

        HttpHeaders h = new HttpHeaders();
        h.set(HttpHeaders.COOKIE, request.getHeader(HttpHeaders.COOKIE));

        // Pass headers along in mis code request
        HttpEntity<String> get = new HttpEntity<String>(h);
        String misCode = restTemplate.exchange(getUserMisCodeUrl, HttpMethod.GET, get, String.class).getBody();

        misCode = (StringUtils.isEmpty(misCode) || "000".equals(misCode)) ? "000" : misCode;
        log.debug("cccMisCode:[" + misCode + "]");
        return misCode.replaceAll("\"", "");
    }

    public HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.error("RequestContextHolder does not have request attributes");
            return null;
        }

        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            log.error("requestAttributes are not ServletRequestAttributes");
            return null;
        }

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttribute.getRequest();
        return request;
    }
}
