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
package org.cccnext.tesuto.web.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.web.model.TesutoAuditEvent;
import org.cccnext.tesuto.web.repository.TesutoAuditEventRepository;
import org.cccnext.tesuto.web.assembler.TesutoAuditEventAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TesutoAuditEventService implements AuditEventRepository {
    @Autowired
    private TesutoAuditEventRepository tesutoAuditEventRepository;

    @Autowired
    private TesutoAuditEventAssembler auditEventConverter;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {
        tesutoAuditEventRepository.save(auditEventConverter.convert(event));
    }

    public List<AuditEvent> find(Date after) {
        if (after == null) {
            return auditEventConverter.convert(tesutoAuditEventRepository.findAll());
        } else {
            Iterable<TesutoAuditEvent> tesutoAuditEvents =
                    tesutoAuditEventRepository.findByAuditEventDateAfter(LocalDateTime.from(after.toInstant()));
            return auditEventConverter.convert(tesutoAuditEvents);
        }
    }

    public List<AuditEvent> find(String principal, Date after) {
        if (StringUtils.isEmpty(principal)) {
            return find(after);
        } else {
            Iterable<TesutoAuditEvent> tesutoAuditEvents =
                    tesutoAuditEventRepository.findByUsernameAndAuditEventDateAfter(principal, LocalDateTime.from(after.toInstant()));
            return auditEventConverter.convert(tesutoAuditEvents);
        }
    }

    public List<AuditEvent> find(String principal, Date after, String type) {
        if (StringUtils.isEmpty(type)) {
            return find(principal, after);
        } else {
            Iterable<TesutoAuditEvent> tesutoAuditEvents =
                    tesutoAuditEventRepository.findByUsernameAndAuditEventDateAfterAndAuditEventType(principal, LocalDateTime.from(after.toInstant()), type);
            return auditEventConverter.convert(tesutoAuditEvents);
        }
    }

    //TODO add functionality
	@Override
	public List<AuditEvent> find(String principal, Instant after, String type) {
		return null;
	}

}
