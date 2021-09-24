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
package org.cccnext.tesuto.web.repository;

import java.time.LocalDateTime;

import org.cccnext.tesuto.web.model.TesutoAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TesutoAuditEventJpaRepository extends TesutoAuditEventRepository, JpaRepository<TesutoAuditEvent, Long> {

    Iterable<TesutoAuditEvent> findByAuditEventDateAfter(LocalDateTime from);

    Iterable<TesutoAuditEvent> findByUsernameAndAuditEventDateAfter(String principal, LocalDateTime from);

    Iterable<TesutoAuditEvent> findByUsernameAndAuditEventDateAfterAndAuditEventType(String principal, LocalDateTime from, String type);
}
