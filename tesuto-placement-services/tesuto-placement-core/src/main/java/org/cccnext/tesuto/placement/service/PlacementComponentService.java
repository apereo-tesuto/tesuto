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

import static org.cccnext.tesuto.placement.model.PlacementEventLog.EventType.PLACEMENT_COMPONENT_SAVE_COMPLETE;
import static org.cccnext.tesuto.placement.model.PlacementEventLog.EventType.PLACEMENT_COMPONENT_SAVE_FAILURE;
import static org.cccnext.tesuto.placement.model.PlacementEventLog.EventType.PLACEMENT_COMPONENT_SAVE_START;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.model.PlacementComponent;
import org.cccnext.tesuto.placement.repository.jpa.PlacementComponentRepository;
import org.cccnext.tesuto.placement.view.TesutoPlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class PlacementComponentService {
    public static final String RULE_SET_SERVICE_ENDPOINT = "/service/v1/ruleset/";

    @Autowired
    @Qualifier(value = "entityManagerFactoryPlacement")
    protected EntityManagerFactory emf;

    @Autowired
    PlacementComponentRepository placementComponentRepository;

    @Autowired
    PlacementComponentAssembler placementComponentAssembler;

    @Autowired
    TesutoPlacementComponentViewDtoAssembler tesutoPlacementComponentViewDtoAssembler;

    @Autowired
    SubjectAreaServiceAdapter subjectAreaService;

    @Autowired
    private CollegeReader collegeRestClient;

    @Autowired
    private PlacementEventLogService eventLogService;

    @Autowired
    private RuleServiceWebServiceQueries ruleServiceWebServiceQueries;

    @Transactional(readOnly=true)
    public Collection<PlacementComponentViewDto> getPlacementComponentsByTrackingId(String trackingId) {
        Collection<PlacementComponentViewDto> dtos = placementComponentAssembler.assembleDto( placementComponentRepository.findByTrackingId(trackingId));
        dtos.stream().forEach(it -> updateComponent(it));
        return dtos;
    }



    @Transactional(readOnly=true)
    public Collection<PlacementComponentViewDto> getPlacementComponents(String misCode, String cccid) {
        Collection<PlacementComponentViewDto> dtos = placementComponentAssembler.assembleDto( placementComponentRepository.findByCollegeIdAndCccid(misCode, cccid));
        dtos.stream().forEach(it -> updateComponent(it));
        return dtos;
    }

    public Collection<PlacementComponentViewDto> getPlacementComponents(String misCode, String cccid, Integer subjectAreaId) {
        Collection<PlacementComponentViewDto> dtos = placementComponentAssembler.assembleDto( placementComponentRepository.findByCollegeIdAndCccidAndSubjectAreaId(misCode, cccid, subjectAreaId));
        dtos.stream().forEach(it -> updateComponent(it));
        return dtos;
    }


    @Transactional(readOnly=true)
    public Collection<PlacementComponentViewDto> getPlacementComponents(Placement placement) {
        Collection<PlacementComponentViewDto> dtos = placementComponentAssembler.assembleDto( placementComponentRepository.findByPlacements(placement));
        dtos.stream().forEach(it -> updateComponent(it));
        return dtos;
    }


    public PlacementComponent createPlacementComponent(String misCode, String cccid, PlacementComponentViewDto viewDto) {
        eventLogService.log(viewDto.getTrackingId(), cccid, viewDto.getSubjectAreaId(), viewDto.getSubjectAreaVersion(),
                misCode, PLACEMENT_COMPONENT_SAVE_START, "" );
        try {
            if (viewDto.getMmapRuleSetId() != null && viewDto.getMmapRuleSetTitle() == null) {
                // set the title value
                RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(viewDto.getMmapRuleSetId());
                viewDto.setMmapRuleSetTitle(ruleSetDto.getTitle());
            }
            PlacementComponent component = placementComponentRepository.save(placementComponentAssembler.disassembleDto(viewDto));
            eventLogService.log(viewDto.getTrackingId(), cccid, viewDto.getSubjectAreaId(), viewDto.getSubjectAreaVersion(),
                    misCode, PLACEMENT_COMPONENT_SAVE_COMPLETE, "" );
            return component;
        } catch (Exception e) {
            eventLogService.log(viewDto.getTrackingId(), cccid, viewDto.getSubjectAreaId(), viewDto.getSubjectAreaVersion(),
                    misCode, PLACEMENT_COMPONENT_SAVE_FAILURE, e.getMessage());
            throw e;
        }
    }

    public int deleteAllPlacementComponentsForCollegeId(String misCode) {
        return placementComponentRepository.deleteByCollegeId(misCode);
    }

    PlacementComponentViewDto updateComponent(PlacementComponentViewDto dto) {
        dto.setCb21(subjectAreaService.getCb21(dto.getCb21Code()));
        dto.setVersionedSubjectAreaViewDto(subjectAreaService.getVersionedSubjectAreaDto(dto.getSubjectAreaId(), dto.getSubjectAreaVersion()));
        if (dto.getVersionedSubjectAreaViewDto() != null) {
            dto.setCourses(subjectAreaService.getCoursesFromVersionedSubjectArea(dto.getVersionedSubjectAreaViewDto(), dto.getCb21Code(), dto.getCourseGroup()));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public TesutoPlacementComponentViewDto getTesutoPlacementComponent(String collegeMisCode, String placementId) {
    	EntityManager entityManager = emf.createEntityManager();
        List<TesutoPlacementComponent> tesutoPlacementComponentList = (List<TesutoPlacementComponent>) entityManager
                .createNamedQuery("TesutoPlacementComponent.findByPlacementId")
                .setParameter("placementId", placementId)
                .setHint("org.hibernate.cacheable", true)
                .setMaxResults(1)
                .getResultList();
        TesutoPlacementComponentViewDto tesutoPlacementComponentViewDto = null;
        if (tesutoPlacementComponentList.size() >= 1) {
            tesutoPlacementComponentViewDto = tesutoPlacementComponentViewDtoAssembler.doAssemble(tesutoPlacementComponentList.get(0));
        }
        if (tesutoPlacementComponentViewDto != null) {
            String collegeName = collegeRestClient.getCollegeByMisCode(collegeMisCode).getName();
            tesutoPlacementComponentViewDto.setCollegeName(collegeName);
        }
        return tesutoPlacementComponentViewDto;
    }

    public PlacementComponentViewDto getPlacementComponent(String placementComponentId) {
        return placementComponentAssembler.assembleDto(placementComponentRepository.getOne(placementComponentId));
    }

    public List<PlacementComponentViewDto> getPlacementComponents(Set<String> placementComponentId) {
        return placementComponentAssembler.assembleDto(placementComponentRepository.findAllById(placementComponentId));
    }

    public Collection<PlacementComponentViewDto> updatePlacementComponent(Collection<PlacementComponentViewDto> placementComponentViewDtos) {
        placementComponentViewDtos.forEach(placementComponentViewDto -> {
            PlacementComponent placementComponent = placementComponentAssembler.doDisassemble(placementComponentViewDto);
            // TODO: Add RuleSet Title, see PlacementComponentController
            placementComponentRepository.save(placementComponent);
        });
        return placementComponentViewDtos;
    }
}
