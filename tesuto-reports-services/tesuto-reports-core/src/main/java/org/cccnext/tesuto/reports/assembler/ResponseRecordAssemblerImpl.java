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

import org.cccnext.tesuto.reports.model.ResponseRecord;
import org.cccnext.tesuto.reports.model.inner.JpaResponseRecord;
import org.springframework.stereotype.Component;

@Component(value = "responseRecordAssembler")
public class ResponseRecordAssemblerImpl implements ResponseRecordAssembler {

    @Override
    public ResponseRecord assembleDto(JpaResponseRecord jpaResponseRecord) {

        if (jpaResponseRecord == null) {
            return null;
        }
        ResponseRecord responseRecord = new ResponseRecord();

        responseRecord.setAttemptId(jpaResponseRecord.getResponseAttemptId());

        responseRecord.setAttemptIndex(jpaResponseRecord.getAttemptIndex());

        responseRecord.setDuration(jpaResponseRecord.getDuration());

        responseRecord.setItemRefIdentifier(jpaResponseRecord.getItemRefIdentifier());

        responseRecord.setOutcomeScore(jpaResponseRecord.getOutcomeScore());

        responseRecord.setResponses(jpaResponseRecord.getResponses());

        responseRecord.setSingleCharacterResponse(jpaResponseRecord.getSingleCharacterResponse());

        responseRecord.setResponseIdentifiers(jpaResponseRecord.getResponseIdentifiers());
        
        responseRecord.setItemId(jpaResponseRecord.getItemId());

        return responseRecord;

    }

    @Override
    public JpaResponseRecord disassembleDto(ResponseRecord responseRecord) {
        if (responseRecord == null) {
            return null;
        }
        JpaResponseRecord jpaResponseRecord = new JpaResponseRecord();

        jpaResponseRecord.setResponseAttemptId(responseRecord.getAttemptId());

        jpaResponseRecord.setAttemptIndex(responseRecord.getAttemptIndex());

        jpaResponseRecord.setDuration(responseRecord.getDuration());

        jpaResponseRecord.setItemRefIdentifier(responseRecord.getItemRefIdentifier());

        jpaResponseRecord.setOutcomeScore(responseRecord.getOutcomeScore());

        jpaResponseRecord.setResponses(responseRecord.getResponses());

        jpaResponseRecord.setSingleCharacterResponse(responseRecord.getSingleCharacterResponse());

        jpaResponseRecord.setResponseIdentifiers(responseRecord.getResponseIdentifiers());
                
        jpaResponseRecord.setItemId(responseRecord.getItemId());

        return jpaResponseRecord;

    }

}
