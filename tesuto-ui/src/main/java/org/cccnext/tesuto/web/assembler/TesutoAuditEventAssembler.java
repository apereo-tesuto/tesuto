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
package org.cccnext.tesuto.web.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;

import org.cccnext.tesuto.springboot.audit.TesutoAuthenticationDetails;
import org.cccnext.tesuto.web.model.TesutoAuditEvent;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class TesutoAuditEventAssembler {

    public TesutoAuditEvent convert(AuditEvent event) {
        TesutoAuditEvent auditEvent = new TesutoAuditEvent();
        auditEvent.setUsername( event.getPrincipal());
        auditEvent.setAuditEventDate( new Date(event.getTimestamp().toEpochMilli()));
        auditEvent.setAuditEventType( event.getType());
        Object details = event.getData().get("details");
        if (details != null && WebAuthenticationDetails.class.isAssignableFrom(details.getClass())) {
            WebAuthenticationDetails authDetails = (WebAuthenticationDetails) details;
            auditEvent.setRemoteAddress(authDetails.getRemoteAddress());
        }
        if (details != null && TesutoAuthenticationDetails.class.isAssignableFrom(details.getClass())) {
            TesutoAuthenticationDetails authDetails = (TesutoAuthenticationDetails) details;
            auditEvent.setUserAgent(authDetails.getUserAgent());
            auditEvent.setPermissions (authDetails.getRoles());
            auditEvent.setColleges (authDetails.getColleges());
            auditEvent.setCccid(authDetails.getCccid());
        }
        return auditEvent;
    }

    public List<AuditEvent> convert(Iterable<TesutoAuditEvent> cccAuditEventList) {
        return Lists.newArrayList(cccAuditEventList).stream().map( ev -> convert(ev)).collect(Collectors.toList());
    }

    public AuditEvent convert(TesutoAuditEvent cccAuditEvent) {
        Map<String, Object> data = new HashMap<>();

        AuditEvent auditEvent = new AuditEvent(cccAuditEvent.getUserAgent(), cccAuditEvent.getAuditEventType(), data);
        return auditEvent;
    }

}
