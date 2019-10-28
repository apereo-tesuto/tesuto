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
package org.cccnext.tesuto.reports.assembler;

import org.cccnext.tesuto.domain.assembler.DtoAssembler;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.model.inner.JpaAttemptRecord;

public interface AttemptRecordAssembler extends DtoAssembler<AttemptRecord, JpaAttemptRecord> {

    @Override
    AttemptRecord assembleDto(JpaAttemptRecord jpaAttemptRecord);

    @Override
    JpaAttemptRecord disassembleDto(AttemptRecord attemptRecord);
}
