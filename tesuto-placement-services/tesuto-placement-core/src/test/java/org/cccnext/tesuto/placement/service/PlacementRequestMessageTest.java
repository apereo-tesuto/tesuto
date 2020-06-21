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

import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.placement.PlacementGenerator;
import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.stub.CompetencyAttributesServiceStub;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.cccnext.tesuto.test.util.SeedDataUtil;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RunWith(SpringRunner.class)
public class PlacementRequestMessageTest {
    private static String COLLEGE_DISCIPLINE_URI = "src/test/resources/seed_data/college_disciplines.json";

    @Configuration
    public static class MyTestConfiguration {
        @Bean
        public Mapper mapper() {
            List<String> mappingsList = new LinkedList<>();
            mappingsList.add( "subjectarea-mapping.xml");
            DozerBeanMapper beanMapper = new DozerBeanMapper( mappingsList);
            return beanMapper;
        }

        @Bean
        public DisciplineAssembler disciplineAssembler() throws IllegalAccessException {
            DisciplineAssembler assembler = new DisciplineAssembler();
            FieldUtils.writeField(assembler, "mapper", mapper(), true);
            return assembler;
        }

        @Bean
        public CompetencyAttributesAssembler competencyAttributesAssembler() throws IllegalAccessException {
            CompetencyAttributesAssembler assembler =  new CompetencyAttributesAssembler();
            FieldUtils.writeField(assembler, "mapper", mapper(), true);
            return assembler;
        }
        
        @Bean
        public CompetencyAttributesService competencyAttributesService() throws IllegalAccessException {
        	CompetencyAttributesService assembler =  new CompetencyAttributesServiceStub();
            FieldUtils.writeField(assembler, "mapper", mapper(), true);
            return assembler;
        }
    }

    @Mock
    PlacementEventLogService placementEventLogService;

    @Mock
    PlacementNotificationService placementNotificationService;

    @Mock
    SubjectAreaServiceAdapter subjectAreaService;

    @Mock
    RuleSetReader client;

    @InjectMocks
    private PlacementEventServiceImpl placementEventService = new PlacementEventServiceImpl();;

    @InjectMocks
    DisciplineAssembler disciplineAssembler;
    
    @Mock
    CompetencyAttributesServiceImpl competencyAttributesService;

    @Autowired
    CompetencyAttributesAssembler attributesAssembler;

    @Autowired
    Mapper mapper;

    private Discipline sampleSubjectArea;
    
    private String engineId = "mmppPlacementEngine";

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException, ReflectiveOperationException {
        MockitoAnnotations.initMocks(this);
        List<Object> disciplines = SeedDataUtil.readSeedData(COLLEGE_DISCIPLINE_URI, Discipline.class);
        sampleSubjectArea = (Discipline) disciplines.get(0);
        CompetencyAttributes competencyAttributes = CompetencyAttributes.createInstance(sampleSubjectArea.getCompetencyMapDiscipline());
        sampleSubjectArea.setCompetencyAttributes(competencyAttributes);
        FieldUtils.writeField(placementEventService, "engineId", engineId, true);
    }

    @Test
    public void testRuleValidationFailed() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.FAILED_ON_VALIDATE));

        placementEventService.requestPlacementForActionResult(placementActionResult);

        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL), anyString());
    }

    @Test(expected=RetryableMessageException.class)
    public void testRulePlacementServiceDown() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN));

        placementEventService.requestPlacementForActionResult(placementActionResult);

    }

    @Test(expected=RetryableMessageException.class)
    public void testRulesStillLoading() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(
                createFailedActions(PlacementRulesActionCode.RULE_ARE_LOADING + "for college id " + placementActionResult.getCollegeId()));

        placementEventService.requestPlacementForActionResult(placementActionResult);

    }

    @Test
    public void testRuleNoActions() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.NO_ACTIONS_FOUND));

        placementEventService.requestPlacementForActionResult(placementActionResult);

        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL), anyString());
    }

    @Test
    public void testPlacementRequestCompleted() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createSuccessActions());

        placementEventService.requestPlacementForActionResult(placementActionResult);

        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_COMPLETE), anyString());
    }

    @Test(expected=RetryableMessageException.class)
    public void testRuleServicePoorUrl() throws Exception {

        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        doThrow( new PoorlyFormedRequestException("some url")).when(client).executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class));

        placementEventService.requestPlacementForActionResult(placementActionResult);

    }

    @Test
    public void testRuleServiceOtherException() throws Exception {
        PlacementActionResult placementActionResult = PlacementGenerator.makePlacementActionResult();
        VersionedSubjectAreaViewDto versionedSubjectArea = minimalVersionedSubjectAreaViewDto(
                placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());

        when( subjectAreaService.getVersionedSubjectAreaDto( eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()))).thenReturn(versionedSubjectArea);
        doThrow( new HttpClientErrorException(HttpStatus.BAD_REQUEST, "cannot recover")).when(client).executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class));

        placementEventService.requestPlacementForActionResult(placementActionResult);

        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(placementActionResult.getSubjectAreaId()),
                eq(placementActionResult.getSubjectAreaVersion()), eq(placementActionResult.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL), anyString());
    }

    private List<ActionResult> createFailedActions(String failureMessage) {
        ActionResult actionResult = new ActionResult( failureMessage, false);
        return new LinkedList<>(Collections.singleton(actionResult));
    }

    private List<ActionResult> createSuccessActions() {
        ActionResult actionResult = new ActionResult( "CREATE_PLACEMENT", true);
        return new LinkedList<>(Collections.singleton(actionResult));
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
