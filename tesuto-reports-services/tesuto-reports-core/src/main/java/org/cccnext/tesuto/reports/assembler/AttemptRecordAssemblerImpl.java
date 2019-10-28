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

import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.model.inner.JpaAttemptRecord;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "attemptRecordAssembler")
public class AttemptRecordAssemblerImpl implements AttemptRecordAssembler {

    
    @Autowired
    Mapper mapper;
    
    @Override
    public AttemptRecord assembleDto(JpaAttemptRecord jpaAttemptRecord) {

        if (jpaAttemptRecord == null) {
            return null;
        }
        
        AttemptRecord attemptRecord =  mapper.map(jpaAttemptRecord,AttemptRecord.class);
       
        return attemptRecord;
    }

    @Override
    public JpaAttemptRecord disassembleDto(AttemptRecord attemptRecord) {
        if (attemptRecord == null) {
            return null;
        }
        JpaAttemptRecord jpaAttemptRecord =  mapper.map(attemptRecord,JpaAttemptRecord.class);
        return jpaAttemptRecord;
    }

}
