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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.reports.assembler.AttemptRecordAssembler;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.model.inner.JpaAttemptRecord;
import org.cccnext.tesuto.reports.repository.jpa.AttemptRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttemptRecordService {

    @Autowired
    AttemptRecordRepository repository;

    @Autowired
    AttemptRecordAssembler assembler;

    @Transactional(readOnly = true)
    public AttemptRecord getAttemptRecordForAttemptId(String attemptId) {
        return assembler.assembleDto(repository.findByAttemptId(attemptId));
    }

    @Transactional(readOnly = true)
    public Pair<List<String>, Map<String,AttemptRecord>> getAttemptRecords(List<String> attemptIds) {
        List<AttemptRecord> processedAttempts = assembler.assembleDto(repository.findByAttemptIdIn(attemptIds));
        List<String> foundAttemptIds = processedAttempts.stream()
                .map(at -> at.getAttemptId()).collect(Collectors.toList());
        Map<String, AttemptRecord> mappedProcessedAttempts =  processedAttempts.stream()
                .collect(Collectors.toMap(AttemptRecord::getAttemptId, Function.identity()));
        return Pair.of(attemptIds.stream()
                .filter(a -> !foundAttemptIds.contains(a))
                .collect(Collectors.toList()), mappedProcessedAttempts);
    }

    @Transactional(readOnly = true)
    public List<AttemptRecord> findByCccid(String cccid) {
        return assembler.assembleDto(repository.findByCccid(cccid));
    }

    @Transactional
    public void deleteByAttemptId(String attemptId) {
        repository.deleteById(attemptId);
    }

    @Transactional
    AttemptRecord save(AttemptRecord entity) {
        JpaAttemptRecord jpa = assembler.disassembleDto(entity);
        Date currentTimeStamp = new Date(System.currentTimeMillis());
        if (repository.getOne(jpa.getAttemptId()) == null) {
            jpa.setCreatedOnDate(currentTimeStamp);
        }
        jpa.setLastUpdatedDate(currentTimeStamp);
        jpa.setTotalDurationFormatted(DurationFormatUtils.formatDuration(entity.getTotalDuration(), "H:mm:ss", true));
        JpaAttemptRecord savedEntity = repository.save(jpa);
        return assembler.assembleDto(savedEntity);
    }

}
