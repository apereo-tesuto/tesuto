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
package org.cccnext.tesuto.reports.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.reports.assembler.ResponseRecordAssembler;
import org.cccnext.tesuto.reports.model.ResponseRecord;
import org.cccnext.tesuto.reports.model.inner.JpaResponseRecord;
import org.cccnext.tesuto.reports.repository.jpa.ResponseRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponseRecordService {

    @Autowired
    ResponseRecordRepository repository;

    @Autowired
    ResponseRecordAssembler assembler;

    @Transactional(readOnly = true)
    public List<ResponseRecord> getAttemptRecordForAttemptId(String attemptId) {
        return assembler.assembleDto(repository.findByResponseAttemptId(attemptId));
    }

    public List<ResponseRecord> getResponseRecordsForAttempt(String attemptId) {
        return getAttemptRecordForAttemptId(attemptId);
    }

    @Transactional
    public void deleteByAttemptId(String attemptId) {
        repository.findByResponseAttemptId(attemptId).stream().forEach(rr -> repository.delete(rr));
    }

    @Transactional
    ResponseRecord save(ResponseRecord entity) {
        JpaResponseRecord jpa = assembler.disassembleDto(entity);
        Date currentTimeStamp = new Date(System.currentTimeMillis());
        jpa.setCreatedOnDate(currentTimeStamp);
        jpa.setLastUpdatedDate(currentTimeStamp);
        JpaResponseRecord savedEntity = repository.save(jpa);
        return assembler.assembleDto(savedEntity);
    }

    @Transactional
    List<ResponseRecord> save(Collection<ResponseRecord> entities) {
        List<JpaResponseRecord> jpas = assembler.disassembleDto(entities);
        Date currentTimeStamp = new Date(System.currentTimeMillis());
        for (JpaResponseRecord jpa : jpas) {
            jpa.setCreatedOnDate(currentTimeStamp);
            jpa.setLastUpdatedDate(currentTimeStamp);
        }
        List<JpaResponseRecord> savedEntity = repository.saveAll(jpas);
        return assembler.assembleDto(savedEntity);
    }
}
