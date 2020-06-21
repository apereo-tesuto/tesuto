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

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.placement.service.PlacementRequester;
import org.cccnext.tesuto.web.assembler.StudentCollegeAffiliationDtoAssembler;
import org.cccnext.tesuto.web.model.StudentCollegeAffiliation;
import org.cccnext.tesuto.web.model.StudentCollegeAffiliationId;
import org.cccnext.tesuto.web.repository.StudentCollegeAffiliationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("studentCollegeAffiliationService")
public class StudentCollegeAffiliationServiceImpl implements StudentCollegeAffiliationService {

    @Autowired
    StudentCollegeAffiliationRepository repository;

    @Autowired
    StudentCollegeAffiliationDtoAssembler assembler;

    @Autowired
    PlacementRequester placementRequestRestClient;

    @Override
    @Transactional
    public void createIfNotExists(String eppn, String studentCccId, String misCode, String authSource) {
        StudentCollegeAffiliationId id = new StudentCollegeAffiliationId(eppn, studentCccId, misCode);
        StudentCollegeAffiliation affiliation = repository.findById(id).get();
        if (affiliation == null) {
            StudentCollegeAffiliation newAffiliation = new StudentCollegeAffiliation();
            newAffiliation.setEppn(eppn);
            newAffiliation.setStudentCccId(studentCccId);
            newAffiliation.setMisCode(misCode);
            newAffiliation.setLoggedDate(new Date());
            newAffiliation.setAuthSource(authSource);
            repository.save(newAffiliation);

            try {
                placementRequestRestClient.requestPlacements(misCode, studentCccId);
            } catch (Exception ex) {
                String message = String.format("Failed to request placements for student %s in college %s", studentCccId, misCode);
                log.error(message, ex);
            }
        }
    }

    @Override
    @Transactional
    public void delete(String eppn, String studentCccId, String misCode) {
        StudentCollegeAffiliationId id = new StudentCollegeAffiliationId(eppn, studentCccId, misCode);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(StudentCollegeAffiliationDto affiliationDto) {
        delete(affiliationDto.getEppn(), affiliationDto.getStudentCccId(), affiliationDto.getMisCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCollegeAffiliationDto> findAll() {
        return assembler.assembleDto(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentCollegeAffiliationDto find(String eppn, String studentCccId, String misCode) {
        StudentCollegeAffiliationId id = new StudentCollegeAffiliationId(eppn, studentCccId, misCode);
        return assembler.assembleDto(repository.findById(id).get());
    }

    @Override
	@Transactional(readOnly = true)
	public StudentCollegeAffiliationDto findByCccIdAndMisCode(String studentCccId, String misCode) {
		List<StudentCollegeAffiliation> affiliations = repository.findByStudentCccIdAndMisCode(studentCccId, misCode);
		Comparator<StudentCollegeAffiliation> comparator = new Comparator<StudentCollegeAffiliation>() {
				public int compare(StudentCollegeAffiliation aff1, StudentCollegeAffiliation aff2) {
					return aff1.getLoggedDate().compareTo(aff2.getLoggedDate());
				}
			};
		Optional<StudentCollegeAffiliationDto> affiliation =
			affiliations.stream().max(comparator).map(aff -> assembler.assembleDto(aff));
		if (affiliation.isPresent()) {
			return affiliation.get();
		} else {
			throw new NotFoundException("Cannot find student affiliation for "
										+ studentCccId + "," + misCode);
		}
	}

    @Override
    @Transactional(readOnly = true)
    public List<StudentCollegeAffiliationDto> findByStudentCccId(String studentCccId) {
        List<StudentCollegeAffiliation> studentCollegeAffiliationList = repository.findByStudentCccId(studentCccId);
        List<StudentCollegeAffiliationDto> studentCollegeAffiliationDtoList = assembler.assembleDto(studentCollegeAffiliationList);
        return studentCollegeAffiliationDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCollegeAffiliationDto> findTenMostRecent() {
        return assembler.assembleDto(repository.findMostRecentLogins(new PageRequest(0, 10)));
    }
}

