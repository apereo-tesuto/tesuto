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
package org.cccnext.tesuto.springboot.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestForwardService {
    @Value("${http.originating.protocol}")
    String httpOriginatingProtocol;

    @Value("${http.originating.client.port}")
    String httpOriginatingClientPort;

    public String getForwardUrl(HttpServletRequest request, String path) {
        // Check the header value for validity
        String requestHeaderHost = request.getHeader("host");
        if (StringUtils.isNotBlank(requestHeaderHost)) {
            requestHeaderHost = requestHeaderHost.split(",")[0];
            requestHeaderHost = requestHeaderHost.split(":")[0];
        }

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(request.getHeader(httpOriginatingProtocol))) {
            sb = sb.append(request.getHeader(httpOriginatingProtocol)).append("://")
                    .append(requestHeaderHost);
            if (!("80".equals(request.getHeader(httpOriginatingClientPort))
                    || "443".equals(request.getHeader(httpOriginatingClientPort)))) {
                sb.append(":").append(request.getHeader(httpOriginatingClientPort));
            }
            sb.append(request.getContextPath())
                    .append(path);
        } else {
            sb.append(getBaseURL(request)).append(path);
        }
        return sb.toString();
    }
    
    private String getBaseURL(HttpServletRequest request) {

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }
}
