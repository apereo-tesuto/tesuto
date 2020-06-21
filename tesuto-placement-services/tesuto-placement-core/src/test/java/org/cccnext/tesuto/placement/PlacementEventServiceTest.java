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
package org.cccnext.tesuto.placement;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.placement.service.PlacementComponentAssembler;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementDuplicationCheckService;
import org.cccnext.tesuto.placement.service.PlacementEventLogService;
import org.cccnext.tesuto.placement.service.PlacementEventServiceImpl;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class PlacementEventServiceTest {

    @Mock
    RuleSetReader ruleRestClient;

    @Mock
    PlacementEventLogService placementEventLogService;

    @Mock
    PlacementService placementService;

    @Mock
    PlacementComponentService placementComponentService;

    @Mock
    PlacementDuplicationCheckService placementDuplicationCheckService;

    @Mock
    SubjectAreaServiceAdapter subjectAreaService;

    @InjectMocks
    PlacementEventServiceImpl placementEventService;

    PlacementEventServiceImpl placementEventServiceSpy;

    @Autowired
    PlacementComponentAssembler placementComponentAssembler;
    
    private String engineId = "mmppPlacementEngine";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        FieldUtils.writeField(placementEventService, "mapper", new DozerBeanMapper(), true);
        FieldUtils.writeField(placementEventService, "engineId", engineId, true);
        placementEventServiceSpy = spy(placementEventService);
    }

    @Test
    public void verifyProcessCreatePlacement() {
        ArgumentCaptor<PlacementViewDto> placementCapture = ArgumentCaptor.forClass(PlacementViewDto.class);
        List<PlacementActionResult> placementActionResults = PlacementGenerator.makePlacementActionResults(3);
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = new VersionedSubjectAreaViewDto();
        CompetencyAttributesViewDto competencyAttributesViewDto = new CompetencyAttributesViewDto();
        competencyAttributesViewDto.setMmAssignedPlacementLogic("mmAssignedPlacementLogic");
        versionedSubjectAreaViewDto.setCompetencyAttributes(competencyAttributesViewDto);
        when(subjectAreaService.getVersionedSubjectAreaDto(anyInt(), anyInt())).thenReturn(versionedSubjectAreaViewDto);
        when(placementDuplicationCheckService.isDuplicateNewPlacement(any(PlacementViewDto.class))).thenReturn(false);

        placementEventService.addPlacement(placementActionResults);

        verify(placementService, times(3)).addPlacement(placementCapture.capture());
        List<PlacementViewDto> values = placementCapture.getAllValues();
        for (PlacementViewDto placementViewDto : values) {
            Assert.assertFalse(placementViewDto.isAssigned());
            Assert.assertNull(placementViewDto.getAssignedDate());
        }
    }

    @Test
    public void verifyDuplicatePlacementNotCreated() {
        ArgumentCaptor<PlacementViewDto> placementCapture = ArgumentCaptor.forClass(PlacementViewDto.class);
        ArgumentCaptor<PlacementActionResult> actionResultCapture = ArgumentCaptor.forClass(PlacementActionResult.class);
        List<PlacementActionResult> placementActionResults = PlacementGenerator.makePlacementActionResults(3);

        VersionedSubjectAreaViewDto subjectArea = new VersionedSubjectAreaViewDto();
        subjectArea.setCompetencyAttributes(new CompetencyAttributesViewDto());
        when(subjectAreaService.getVersionedSubjectAreaDto(anyInt(), anyInt())).thenReturn(subjectArea);
        when(placementDuplicationCheckService.isDuplicateNewPlacement(any(PlacementViewDto.class))).thenReturn(true);

        placementEventServiceSpy.addPlacement(placementActionResults);

        verify(placementService, times(0)).addPlacement(placementCapture.capture());
        verify(placementEventServiceSpy, times(1)).requestAssignedPlacement(actionResultCapture.capture());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void verifyRuleRestCallAfterAssignPlacement() {
        ArgumentCaptor<HashMap> factsCapture = ArgumentCaptor.forClass(HashMap.class);
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        when(ruleRestClient.executeRulesEngine(eq("mmppPlacementEngine"), anyMap())).thenReturn(new ArrayList<>());
        when(subjectAreaService.getVersionedSubjectAreaDto(eq(placementActionResult.getSubjectAreaId()), eq(placementActionResult.getSubjectAreaVersion())))
            .thenReturn(minimalVersionedSubjectAreaViewDto(placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion()));
        placementEventService.requestAssignedPlacement(placementActionResult);

        verify(ruleRestClient).executeRulesEngine(eq("mmppPlacementEngine"), factsCapture.capture());
        HashMap<String,Object> facts = factsCapture.getValue();
        Assert.assertEquals(facts.get(PlacementMapKeys.CCCID_KEY), placementActionResult.getCccid());
        Assert.assertEquals(facts.get(PlacementMapKeys.MISCODE_KEY), placementActionResult.getCollegeId());
        Assert.assertEquals(facts.get(PlacementMapKeys.TRACKING_ID_KEY), placementActionResult.getTrackingId());
        Assert.assertEquals(facts.get(PlacementMapKeys.SUBJECT_AREA_KEY), String.valueOf(placementActionResult.getSubjectAreaId()));
        Assert.assertEquals(facts.get(PlacementMapKeys.SUBJECT_AREA_VERSION_KEY), String.valueOf(placementActionResult.getSubjectAreaVersion()));
    }

    @Test
    public void verifyUpdateAssignPlacement() {
        ArgumentCaptor<PlacementViewDto> dtoCapture = ArgumentCaptor.forClass(PlacementViewDto.class);
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        when(placementDuplicationCheckService.isDuplicateAssignedPlacement(any(PlacementViewDto.class))).thenReturn(false);

        placementEventService.assignPlacement(placementActionResult);
        verify(placementService).updateAssignedPlacements( dtoCapture.capture());
        Assert.assertEquals( placementActionResult.getPlacementId(), dtoCapture.getValue().getId());
        Assert.assertEquals( placementActionResult.getRuleSetId(), dtoCapture.getValue().getAssignedRuleSetId());
        Assert.assertEquals( true, dtoCapture.getValue().isAssigned());
    }

    @Test
    public void verifyDuplicatePlacementNotAssigned() {
        ArgumentCaptor<PlacementViewDto> dtoCapture = ArgumentCaptor.forClass(PlacementViewDto.class);
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        when(placementDuplicationCheckService.isDuplicateAssignedPlacement(any(PlacementViewDto.class))).thenReturn(true);

        placementEventService.assignPlacement(placementActionResult);
        verify(placementService,times(0)).updateAssignedPlacements( dtoCapture.capture());
    }


    private VersionedSubjectAreaViewDto minimalVersionedSubjectAreaViewDto(Integer subjectAreaId, Integer version) {
        VersionedSubjectAreaViewDto dto = new VersionedSubjectAreaViewDto();
        dto.setDisciplineId(subjectAreaId);
        dto.setVersion(version);
        dto.setCompetencyAttributes(new CompetencyAttributesViewDto());
        dto.getCompetencyAttributes().setMmAssignedPlacementLogic("mmapAssignLogic");
        return dto;
    }
}
