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
package org.cccnext.tesuto.placement.service;

import java.io.Serializable;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.strategy.ValidityAuditStrategy;


import org.hibernate.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PlacementValidityAuditStrategy extends ValidityAuditStrategy {

        @Override
        public void perform(Session session, String entityName, EnversService auditCfg, Serializable id, Object data, Object revision) {
            try {
                super.perform(session, entityName, auditCfg, id, data, revision);
            } catch (RuntimeException re) {
                if (log.isDebugEnabled()) {
                    log.debug("IGNORE RuntimeException: Cannot update previous revision for entity.", re);
                }
            }
        }
}
