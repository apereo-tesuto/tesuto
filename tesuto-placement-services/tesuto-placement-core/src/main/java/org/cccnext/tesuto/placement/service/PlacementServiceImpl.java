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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.repository.jpa.PlacementRepository;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.placement.view.student.PlacementStudentViewDto;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.cccnext.tesuto.util.TesutoUtils;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("placementService")
public class PlacementServiceImpl implements PlacementService {
	private static final ExampleMatcher COLLEGE_ID_MATCHER = ExampleMatcher.matching().withMatcher("collegeId", GenericPropertyMatchers.ignoreCase());

	@Autowired
	PlacementRepository placementRepository;

	@Autowired
	PlacementNotificationService placementNotificationService;

	@Autowired
	SubjectAreaServiceAdapter subjectAreaService;

	@Autowired
	PlacementAssembler placementAssembler;

	@Autowired
	PlacementHelperService placementHelperService;

	@Autowired
	AssessmentPlacementComponentService assessmentPlacementComponentService;

	@Autowired
	MultipleMeasurePlacementComponentService multipleMeasurePlacementComponentService;

	@Autowired
	PlacementComponentService placementComponentService;

	@Autowired
	RuleSetReader ruleRestClient;

	@Autowired
	Mapper mapper;

	@Autowired
	PlacementStudentViewAssembler placementStudentViewAssembler;

  @Autowired
  private RuleServiceWebServiceQueries ruleServiceWebServiceQueries;

	@Override
	@Transactional
	public Placement addPlacement(PlacementViewDto placementDto) {
		if (StringUtils.isBlank(placementDto.getId())) {
			placementDto.setId(TesutoUtils.newId());
		}
		Placement placement = placementAssembler.disassembleDto(placementDto);
		placement.setCreatedOn(Calendar.getInstance().getTime());

		if (CollectionUtils.isNotEmpty(placement.getPlacementComponents())) {
        // TODO: Verify this is necessary.  Right now I'm keeping it here for safety. -scott smith
        if (placement.getCreateRuleSetId() != null && placement.getCreateRuleSetTitle() == null) {
            // set the title value
            RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(placement.getCreateRuleSetId());
            placement.setCreateRuleSetTitle(ruleSetDto.getTitle());
        }
        if (placement.getAssignedRuleSetId() != null && placement.getAssignedRuleSetTitle() == null) {
            // set the title value
            RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(placement.getAssignedRuleSetId());
            placement.setAssignedRuleSetTitle(ruleSetDto.getTitle());
        }
		    placement = placementRepository.save(placement);
		}
		return placement;
	}

	protected Placement initPlacement(String cccId, String collegeId)  throws Exception {
		Placement placement = new Placement();
		placement.setCollegeId(collegeId);
		placement.setCccid(cccId);
		placement.setId(TesutoUtils.newId());
		return placement;
	}

	private boolean doesPlacementExists(String collegeMisCode, String cccid, DisciplineViewDto subjectArea) {
		return CollectionUtils.isEmpty( placementRepository.findByCollegeIdAndCccidAndDisciplineId(collegeMisCode, cccid, subjectArea.getDisciplineId()));
	}

	@Override
	public void updateAssignedPlacements(PlacementViewDto placementDto) {
		//The next two methods need to execute in different transactions to avoid deadlock (not sure why)
		//So this method must NOT be transactional
		doUpdateAssignedPlacements(placementDto);
		placementNotificationService.placementDecisionComplete(placementDto.getId());
	}

	@Transactional
	public void doUpdateAssignedPlacements(PlacementViewDto placementDto) {
		Collection<Placement> placements = placementRepository.findByCollegeIdAndCccidAndDisciplineId(placementDto.getCollegeId(), placementDto.getCccid(), placementDto.getDisciplineId());
		if(CollectionUtils.isNotEmpty(placements)) {
			Collection<Placement> assignedPlacments = placements.stream().filter(pl -> pl.isAssigned() == true && !pl.getId().equals(placementDto.getId()))
					.collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(assignedPlacments)) {
				assignedPlacments.forEach(pl -> {
					pl.setAssigned(false);
					// TODO: Verify this is necessary.  Right now I'm keeping it here for safety. -scott smith
					if (pl.getCreateRuleSetId() != null && pl.getCreateRuleSetTitle() == null) {
						// set the title value
						RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(pl.getCreateRuleSetId());
						pl.setCreateRuleSetTitle(ruleSetDto.getTitle());
					}
					if (pl.getAssignedRuleSetId() != null && pl.getAssignedRuleSetTitle() == null) {
						// set the title value
						RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(pl.getAssignedRuleSetId());
						pl.setAssignedRuleSetTitle(ruleSetDto.getTitle());
					}
					placementRepository.save(pl);
				});
			}
		}
		Placement placement = placementRepository.getOne(placementDto.getId());
		placement.setAssignedRuleSetId(placementDto.getAssignedRuleSetId());
		placement.setAssignedDate(Calendar.getInstance().getTime());
		placement.setAssigned(true);
		// TODO: Verify this is necessary.  Right now I'm keeping it here for safety. -scott smith
		if (placement.getCreateRuleSetId() != null && placement.getCreateRuleSetTitle() == null) {
			// set the title value
			RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(placement.getCreateRuleSetId());
			placement.setCreateRuleSetTitle(ruleSetDto.getTitle());
		}
		if (placement.getAssignedRuleSetId() != null && placement.getAssignedRuleSetTitle() == null) {
			// set the title value
			RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(placement.getAssignedRuleSetId());
			placement.setAssignedRuleSetTitle(ruleSetDto.getTitle());
		}
		placementRepository.save(placement);
	}




	@Override
	@Transactional(readOnly=true)
	public Collection<PlacementViewDto> getPlacements(String misCode) {
		return updatePlacements(placementAssembler.assembleDto( placementRepository.findByCollegeId(misCode)));
	}

	@Override
	@Transactional(readOnly=true)
	public Collection<PlacementViewDto> getPlacementsForStudent(String misCode, String cccid) {
		Collection<Placement> placements =  placementRepository.findByCollegeIdAndCccid(misCode, cccid);
		Collection<PlacementViewDto> placementViews =  placementAssembler.assembleDto(placements);

		return updatePlacements(placementViews);
	}

	@Override
	@Transactional(readOnly=true)
	public Collection<PlacementStudentViewDto> getStudentViewPlacementsForStudent(String collegeMisCode, String userAccountId) {
		Collection<Placement> placements =  placementRepository.findByCollegeIdAndCccid(collegeMisCode, userAccountId);
		return placements.stream().filter(Placement::isAssigned).map(p -> placementStudentViewAssembler.assembleDto(p)).collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly=true)
	public Collection<PlacementViewDto> getPlacementsForStudent(String misCode, String cccid, Integer subjectAreaId) {
		Collection<Placement> placements =  placementRepository.findByCollegeIdAndCccidAndDisciplineId(misCode, cccid, subjectAreaId);
		addPlacementComponents( placements);
		return updatePlacements(placementAssembler.assembleDto(placements));
	}

	@Override
	@Transactional(readOnly=true)
	public PlacementViewDto findNewPlacementForCccid(String cccid) {
		return placementAssembler.assembleDto(placementRepository.findByCccidAndCreatedOnIsNull(cccid));
	}

	@Override
	@Transactional
	public int deleteAllPlacementsForCollegeId(String misCode) {
		return placementRepository.deleteByCollegeId(misCode);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasPlacementsForCollegeId(String misCollegeCode) {
		return placementRepository.existsByCollegeId(misCollegeCode);
	}

	@Override
	@Transactional(readOnly = true)
	// This is temporary logic to create placements for seed data. Actual logic will be via rules engine
	public Collection<PlacementViewDto> createPlacementsForStudent(String misCode, String cccid) {
		Collection<Placement> placements = new ArrayList<>();
		Collection<PlacementComponentViewDto> components = placementComponentService.getPlacementComponents(misCode, cccid);
		Map<VersionedSubjectAreaViewDto, List<PlacementComponentViewDto>> pcBySubjectArea =
				components.stream().filter(w -> w.getVersionedSubjectAreaViewDto() != null).collect(
						Collectors.groupingBy(w -> w.getVersionedSubjectAreaViewDto())
        );
		// This code creates a placement per group of versionedSubjectArea. This is not accurate logic for actual placements.
		for (Entry<VersionedSubjectAreaViewDto, List<PlacementComponentViewDto>> componentGroup: pcBySubjectArea.entrySet()) {
			PlacementViewDto placementDto = new PlacementViewDto();
			placementDto.setCollegeId(misCode);
			placementDto.setCccid(cccid);
			placementDto.setDisciplineId(componentGroup.getKey().getDisciplineId());
			placementDto.setSubjectAreaVersion(componentGroup.getKey().getVersion());
			placementDto.setSubjectAreaName(componentGroup.getKey().getTitle());
			addPlacementComponent(placementDto);
			PlacementComponentViewDto highestPlacementComponent = getHighestComponent(componentGroup.getValue());
			if (highestPlacementComponent != null) {
				placementDto.setCb21Code(highestPlacementComponent.getCb21Code());
				placementDto.setCourseGroup(highestPlacementComponent.getCourseGroup());
			}
			placements.add(addPlacement(placementDto));
		}
		return placementAssembler.assembleDto( placements);
	}

	@Override
	@Transactional(readOnly=true)
	public PlacementViewDto getPlacement(String placementId) {
		return placementAssembler.assembleDto( placementRepository.getOne(placementId));
	}


	private PlacementComponentViewDto getHighestComponent(List<PlacementComponentViewDto> components) {
		Character cb21Code = detectCb21FromComponents(components);
		Optional<PlacementComponentViewDto> pcDto =
				components.stream().filter(f -> cb21Code.equals(f.getCb21Code())).max(Comparator.comparing(PlacementComponentViewDto::getCourseGroup));
		return pcDto.get();
	}

	private Character detectCb21FromComponents(List<PlacementComponentViewDto> components) {
		// TODO need to be replaced with the rules engine call
		Optional<CB21ViewDto> minCB21 = components.stream().map(it -> it.getCb21()).min( new Comparator<CB21ViewDto>() {
			@Override
			public int compare(CB21ViewDto o1, CB21ViewDto o2) {
				return Integer.compare(o1.getLevel(), o2.getLevel());
			}
		});

		return minCB21.get().getCb21Code();
	}

	@Transactional(readOnly = true)
	public Collection<PlacementViewDto> updatePlacements(Collection<PlacementViewDto> placements) {
		return placements.stream().map(it -> updatePlacement(it)).collect(Collectors.toList());
	}

	PlacementViewDto updatePlacement(PlacementViewDto dto) {
		if (dto.getCb21Code() != null) {
			dto.setCb21(subjectAreaService.getCb21(dto.getCb21Code()));
		}
		dto.setVersionedSubjectAreaViewDto(subjectAreaService.getVersionedSubjectAreaDto(dto.getDisciplineId(), dto.getSubjectAreaVersion()));
		if (dto.getVersionedSubjectAreaViewDto() != null) {
			dto.setCourses(subjectAreaService.getCoursesFromVersionedSubjectArea(dto.getVersionedSubjectAreaViewDto(), dto.getCb21Code(), dto.getCourseGroup()));
		}
		return dto;
	}

	void addPlacementComponents(Collection<Placement> placements) {
	    Collection<PlacementViewDto> placementViews = placementAssembler.assembleDto(placements);
	    for (PlacementViewDto placementView : placementViews) {
	        addPlacementComponent( placementView);
	    }
	}

	void addPlacementComponent(PlacementViewDto placementView) {
	    Collection<PlacementComponentViewDto> components = placementComponentService.getPlacementComponents(placementAssembler.disassembleDto(placementView));
           if( CollectionUtils.isNotEmpty(components))  {
               placementView.setPlacementComponents(new HashSet<PlacementComponentViewDto>(components));
           }
    }

}
