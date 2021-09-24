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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.stub.CompetencyAttributesServiceStub;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.cccnext.tesuto.test.util.SeedDataUtil;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The current test implementation test only the exceptions for the messages, to verify that the correct
 * exceptions, null, or placementViewDto is returned.
 */
@RunWith(SpringRunner.class)
public class MultipleMeasurePlacementTest {
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
            FieldUtils.writeField(assembler, "attributesService", competencyAttributesService(), true);
            FieldUtils.writeField(assembler, "attributeAssembler", competencyAttributesAssembler(), true);
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
        	CompetencyAttributesService service =  new CompetencyAttributesServiceStub();
        	FieldUtils.writeField(service, "mapper", mapper(), true);
            return service;
        }
    }


    @Mock
    PlacementEventLogService placementEventLogService;

    @Mock
    SubjectAreaServiceAdapter subjectAreaService;

    @Mock
    RuleSetReader client;

    @InjectMocks
    private MultipleMeasurePlacementComponentService mmPlacementService = new MultipleMeasurePlacementComponentService();;

    @Autowired
    DisciplineAssembler disciplineAssembler;

    @Autowired
    CompetencyAttributesService competencyAttributesService;
    
    @Mock
    CompetencyAttributesAssembler attributesAssembler;
    
    @Autowired
    CompetencyAttributesAssembler attribAssembler;
    
    @Autowired
    Mapper mapper;

    private Discipline sampleSubjectArea;
    
    private DisciplineViewDto disciplineViewDto;
    
    private String engineId = "mmppPlacementEngine";

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException, ReflectiveOperationException {
        MockitoAnnotations.initMocks(this);
        List<Object> disciplines = SeedDataUtil.readSeedData(COLLEGE_DISCIPLINE_URI, Discipline.class);
        sampleSubjectArea = (Discipline) disciplines.get(0);
        CompetencyAttributes competencyAttributes = CompetencyAttributes.createInstance(sampleSubjectArea.getCompetencyMapDiscipline());
        sampleSubjectArea.setCompetencyAttributes(competencyAttributes);
        FieldUtils.writeField(mmPlacementService, "engineId", engineId, true);
        competencyAttributesService.upsert(sampleSubjectArea.getCompetencyAttributes());
        disciplineViewDto = disciplineAssembler.assembleDto(sampleSubjectArea);
        
    }

    @Test
    public void testNotOptInMultiMeasure() throws Exception {
        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(false);

        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_OPT_OUT), anyString());
    }

    @Test
    public void testInvalidSubjectArea() throws Exception {
        
        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);

        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_FAIL), anyString());
    }

    @Test
    public void testMissingCompetencyAttributes() throws Exception {
        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);
        versionedSubjectArea.setCompetencyAttributes(null);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_FAIL), anyString());
    }

    @Test
    public void testRuleValidationFailed() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions("FAILED_ON_VALIDATE"));
        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_FAILED_ON_VALIDATE), anyString());
    }

    @Test(expected=RetryableMessageException.class)
    public void testRuleVariableSetFailed() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.VARIABLE_SET_NOT_FOUND));
        mmPlacementService.createPlacementComponent( disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());
    }

    @Test(expected=RetryableMessageException.class)
    public void testRuleStillLoading() throws Exception {
        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(
                createFailedActions(PlacementRulesActionCode.RULE_ARE_LOADING + " for college id " + versionedSubjectArea.getCollegeId()));
        
        mmPlacementService.createPlacementComponent( disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());
    }
    @Test(expected=RetryableMessageException.class)
    public void testRulePlacementServiceDown() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN));
        mmPlacementService.createPlacementComponent( disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

    }

    @Test
    public void testRuleNoActions() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions(PlacementRulesActionCode.NO_ACTIONS_FOUND));
        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE_NO_PLACEMENT_GENERATED), anyString());
    }

    @Test(expected=StoreMessageException.class)
    public void testPlacementComponentFailed() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createFailedActions("Some other failure message"));
        mmPlacementService.createPlacementComponent( disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

    }

    @Test
    public void testPlacementComponentCompleted() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenReturn(createSuccessActions());
        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE), anyString());
    }

    @Test(expected=RetryableMessageException.class)
    public void testRuleServicePoorUrl() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenThrow( new PoorlyFormedRequestException("some url"));
        mmPlacementService.createPlacementComponent( disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

    }

    @Test
    public void testRuleServiceOtherException() throws Exception {

        disciplineViewDto.getCompetencyAttributes().setOptInMultiMeasure(true);
        VersionedSubjectAreaViewDto versionedSubjectArea = versionedSubjectArea(sampleSubjectArea);

        when( subjectAreaService.getPublishedVersionForSubjectArea( eq(disciplineViewDto.getDisciplineId()))).thenReturn(versionedSubjectArea);
        when(client.executeRulesEngine(eq("mmppPlacementEngine"), anyMapOf(String.class, Class.class))).thenThrow( new HttpClientErrorException(HttpStatus.BAD_REQUEST, "cannot recover"));
        PlacementComponentViewDto placementComponentViewDto = mmPlacementService.createPlacementComponent(
                disciplineViewDto.getCollegeId(), disciplineViewDto, new PlacementEventInputDto());

        Assert.assertNull(placementComponentViewDto);
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_START), anyString());
        verify( placementEventLogService).log(anyString(), anyString(), eq(disciplineViewDto.getDisciplineId()),
                eq(Integer.valueOf(0)), eq(disciplineViewDto.getCollegeId()),
                eq(PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_FAIL), anyString());
    }

    private List<ActionResult> createFailedActions(String failureMessage) {
        ActionResult actionResult = new ActionResult( failureMessage, false);
        return new LinkedList<>(Collections.singleton(actionResult));
    }

    private List<ActionResult> createSuccessActions() {
        ActionResult actionResult = new ActionResult( "CREATE_PLACEMENT_COMPONENT", true);
        return new LinkedList<>(Collections.singleton(actionResult));
    }

    private VersionedSubjectAreaViewDto versionedSubjectArea(Discipline discipline)  {
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = mapper.map(discipline, VersionedSubjectAreaViewDto.class);
        // versionedSubjectAreaViewDto.setDisciplineSequences(disciplineSequenceAssembler.assembleDto(discipline.getDisciplineSequences()));
        versionedSubjectAreaViewDto.setCompetencyAttributes(attribAssembler.assembleDto(discipline.getCompetencyAttributes()));
        versionedSubjectAreaViewDto.setVersion(1);
        versionedSubjectAreaViewDto.setPublished(false);
        versionedSubjectAreaViewDto.setPublishedVersion(null);
        versionedSubjectAreaViewDto.setPublishedDate(null);
        versionedSubjectAreaViewDto.setPublishedTitle(null);
        versionedSubjectAreaViewDto.setDirty(true);
        versionedSubjectAreaViewDto.setLastEditedDate(discipline.getLastUpdatedDate());
        versionedSubjectAreaViewDto.setArchived(false);
        return versionedSubjectAreaViewDto;
    }
}
