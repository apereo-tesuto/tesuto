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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.reports.assembler.AssessmentReportStructureAssembler;
import org.cccnext.tesuto.reports.model.AssessmentReportStructure;
import org.cccnext.tesuto.reports.model.inner.JpaAssessmentReportStructure;
import org.cccnext.tesuto.reports.repository.jpa.AssessmentReportStructureRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class AssessmentStructureService {

    @Autowired
    AssessmentReportStructureRepository repository;

    @Autowired
    AssessmentReportStructureAssembler assembler;

    @Transactional(readOnly = true)
    List<AssessmentReportStructure> getReportStructureForAssessment(String assessmentId) {
        return assembler.assembleDto(repository.findByAssessmentId(assessmentId));
    }

    @Transactional
    void deleteByAssessmentId(String assessmentId) {
        repository.findByAssessmentId(assessmentId).forEach(ras -> repository.delete(ras));
    }

    @Transactional
    AssessmentReportStructure save(AssessmentReportStructure entity) {
        if(StringUtils.isEmpty(entity.getItemRefIdentifier())) {
            log.error(String.format("Unable to find item_ref_identifier for assessmentItem: %s from assessment: %s", entity.getItemId(), entity.getAssessmentId()));
            return null;
        }
        JpaAssessmentReportStructure jpa = assembler.disassembleDto(entity);
        Date currentTimeStamp = new Date(System.currentTimeMillis());
        jpa.setCreatedOnDate(currentTimeStamp);
        jpa.setLastUpdatedDate(currentTimeStamp);
        JpaAssessmentReportStructure savedEntity = repository.save(jpa);
        return assembler.assembleDto(savedEntity);
    }

    @Transactional
    List<AssessmentReportStructure> save(List<AssessmentReportStructure> entity) {
        List<AssessmentReportStructure> bad = entity.stream().filter(e -> StringUtils.isBlank(e.getItemRefIdentifier())).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(bad)) {
            entity.removeAll(bad);
            for(AssessmentReportStructure b:bad) {
                log.error(String.format("Unable to find item_ref_identifier for assessmentItems: %s from assessment: %s", b.getItemId(), b.getAssessmentId()));
            }
        }
        List<JpaAssessmentReportStructure> jpas = assembler.disassembleDto(entity);
       
        Date currentTimeStamp = new Date();
        for (JpaAssessmentReportStructure jpa : jpas) {
            jpa.setCreatedOnDate(currentTimeStamp);
            jpa.setLastUpdatedDate(currentTimeStamp);
        }

        List<JpaAssessmentReportStructure> savedEntities = repository.saveAll(jpas);
        return assembler.assembleDto(savedEntities);
    }
}
