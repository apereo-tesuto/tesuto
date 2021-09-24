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
package org.cccnext.tesuto.content.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetencyMapOrderServiceImplTest {

    @Autowired
    CompetencyMapOrderService competencyMapOrderService;

    @Autowired
    CompetencyService competencyService;
    
    @Autowired
    CompetencyMapService competencyMapService;

    @Autowired
    AssessmentItemService assessmentItemService;

    private List<AssessmentItemDto> tobeDeletedAssessmentItems = new ArrayList<AssessmentItemDto>();
    private List<CompetencyDto> tobeDeletedCompetencies = new ArrayList<CompetencyDto>();
    private List<CompetencyMapDto> tobeDeletedCompetencyMaps = new ArrayList<CompetencyMapDto>();
    private List<String> tobeDeletedCompetencMapOrderids = new ArrayList<String>();

    private String createOrderedMapWithCompetencyMapDto(CompetencyMapDto competencyMapDto) {
        
        String orderMapId = competencyMapOrderService.create(competencyMapDto);
        if (orderMapId != null) {
            tobeDeletedCompetencMapOrderids.add(orderMapId);
        }
        return orderMapId;
    }

    private Map<String,String> createOrderedMapWithAssessmentUpload(Set<String> disciplines) {
        Map<String, String> orderMapIds = competencyMapOrderService.createForDisciplines(disciplines);
        if (orderMapIds != null && CollectionUtils.isNotEmpty(orderMapIds.values())) {
            tobeDeletedCompetencMapOrderids.addAll(orderMapIds.values());
        }
        return orderMapIds;
    }

    private CompetencyRefDto createCompetencyRefDto(String competencyIdentifier, List<CompetencyRefDto> children) {
        CompetencyRefDto competencyRefDto = new CompetencyRefDto();
        competencyRefDto.setDiscipline("Discipline");
        competencyRefDto.setVersion(1);
        competencyRefDto.setCompetencyIdentifier(competencyIdentifier);
        createCompetency(competencyIdentifier, children);
        return competencyRefDto;
    }

    private void createCompetency(String competencyIdentifier) {
        createCompetency(competencyIdentifier, null);
    }

    private void createCompetency(String competencyIdentifier, List<CompetencyRefDto> children) {
        CompetencyDto competency = new CompetencyDto();
        competency.setDiscipline("Discipline");
        competency.setVersion(1);
        competency.setPublished(true);
        competency.setIdentifier(competencyIdentifier);
        competency.setId(TesutoUtils.newId());
        competency.setChildCompetencyDtoRefs(children);
        tobeDeletedCompetencies.addAll(competencyService.create(Arrays.asList(new CompetencyDto[]{competency})));
    }
    
    private CompetencyMapDto createCompetencyMap(String competencyIdentifierPrefix, int numberToCreate) {
        List<CompetencyRefDto> competencyRefs = new ArrayList<CompetencyRefDto>();
        CompetencyMapDto competencyMapDto = new CompetencyMapDto();
        competencyMapDto.setDiscipline("Discipline");
        competencyMapDto.setIdentifier("Identifier");
        competencyMapDto.setTitle("Title");
        competencyMapDto.setVersion(1);
        for(int i=1;i <= numberToCreate; i++) {
            String competencyIdentifier = competencyIdentifierPrefix + i;
            competencyRefs.add(createCompetencyRefDto(competencyIdentifier, null));
        }
        competencyMapDto.setCompetencyRefs(competencyRefs);
        CompetencyMapDto createdMap = competencyMapService.create(competencyMapDto);
        tobeDeletedCompetencyMaps.add(createdMap);
        return  createdMap;
    }

    private CompetencyMapDto createComplexCompetencyMap(String competencyIdentifierPrefix) {
        List<CompetencyRefDto> competencyRefs = new ArrayList<CompetencyRefDto>();
        CompetencyMapDto competencyMapDto = new CompetencyMapDto();
        competencyMapDto.setDiscipline("Discipline");
        competencyMapDto.setIdentifier("Identifier");
        competencyMapDto.setTitle("Title");
        competencyMapDto.setVersion(1);
        
        List<CompetencyRefDto> rootChildren = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            String competencyIdentifier = competencyIdentifierPrefix + i;
            rootChildren.add(createCompetencyRefDto(competencyIdentifier, null));
            if (i % 2 == 0) {
                List<CompetencyRefDto> midChildren = new ArrayList<>();
                for (int j = 1; j <= 4; j++) {
                    String childCompetencyIdentifier = competencyIdentifier + j;
                    if (j % 3 == 0) {
                        for (int k = 1; k <= 3; k++) {
                            String leafCompetencyIdentifier = childCompetencyIdentifier + k;
                            midChildren.add(createCompetencyRefDto(leafCompetencyIdentifier, null));
                        }
                        rootChildren.add(createCompetencyRefDto(childCompetencyIdentifier, midChildren));
                    } else {
                        rootChildren.add(createCompetencyRefDto(childCompetencyIdentifier, null));
                    }
                }
            }
        }
        competencyRefs.add(createCompetencyRefDto(competencyIdentifierPrefix + "Root", rootChildren));
        competencyMapDto.setCompetencyRefs(competencyRefs);
        CompetencyMapDto createdMap = competencyMapService.create(competencyMapDto);
        tobeDeletedCompetencyMaps.add(createdMap);
        return createdMap;
    }

    private void createAssessmentItem(String competencyIdentifier, Double calibratedDifficulty,
            ItemBankStatusType itemBankStatusType) {
        createAssessmentItem(competencyIdentifier, calibratedDifficulty, itemBankStatusType,
                "Identifier" + competencyIdentifier);
    }

    private void createAssessmentItem(String competencyIdentifier, Double calibratedDifficulty,
            ItemBankStatusType itemBankStatusType, String itemIdentifier) {
        AssessmentItemDto assessmentItemDto = new AssessmentItemDto();
        ItemMetadataDto itemMetadataDto = new ItemMetadataDto();
        CompetenciesItemMetadataDto competenciesItemMetadataDto = new CompetenciesItemMetadataDto();

        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtos = new ArrayList<>();
        CompetencyRefItemMetadataDto competencyRefItemMetadataDto = new CompetencyRefItemMetadataDto();
        competencyRefItemMetadataDto.setCompetencyId(TesutoUtils.newId());
        competencyRefItemMetadataDto.setCompetencyMapDiscipline("Discipline");
        competencyRefItemMetadataDto.setCompetencyRefId(competencyIdentifier);

        competencyRefItemMetadataDtos.add(competencyRefItemMetadataDto);
        competenciesItemMetadataDto.setCompetencyRef(competencyRefItemMetadataDtos);
        itemMetadataDto.setCompetencies(competenciesItemMetadataDto);
        itemMetadataDto.setCalibratedDifficulty(calibratedDifficulty);
        itemMetadataDto.setItemBankStatusType(itemBankStatusType);
        assessmentItemDto.setItemMetadata(itemMetadataDto);

        assessmentItemDto.setNamespace("namespace");
        assessmentItemDto.setIdentifier(itemIdentifier);
        assessmentItemDto.setTitle("Title");
        assessmentItemDto.setId(TesutoUtils.newId());
        assessmentItemDto.setVersion(1);
        assessmentItemDto.setPublished(true);
        tobeDeletedAssessmentItems.add(assessmentItemService.create(assessmentItemDto));
    }

    @Test
    public void testCreate() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createCompetencyMap(id, 2);
        createAssessmentItem(id + "1", 1.0, ItemBankStatusType.AVAILABLE);
        createAssessmentItem(id + "2", 3.0, ItemBankStatusType.AVAILABLE);

        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);
        assertNotNull(competencyMapOrderId);
        List<CompetencyDifficultyDto> orderedCompetencies = competencyMapOrderService
                .getOrderedCompetencies(competencyMapOrderId);

        assertTrue(orderedCompetencies.size() == 2);
    }

    @Test
    public void testAveragingAssessmentItems() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createCompetencyMap(id, 1);

        createAssessmentItem(id+"1", 2.2, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"1", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"1", 6.6, ItemBankStatusType.AVAILABLE, "ID3");

        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);
        List<CompetencyDifficultyDto> orderedCompetencies = competencyMapOrderService
                .getOrderedCompetencies(competencyMapOrderId);

        assertTrue(orderedCompetencies.size() == 1);
        double difficulty = Math.round((Double) (orderedCompetencies.get(0)).getDifficulty() * 10.0);
        assertTrue(String.format("Competency difficulties are in correctly averaged %s", difficulty),
                difficulty == 44.0);
    }

    @Test
    public void testAveragingAssessmentItemsOrderedCorrectly() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createCompetencyMap(id, 3);

        createAssessmentItem(id+"1", 2.2, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"1", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"1", 6.6, ItemBankStatusType.AVAILABLE, "ID3");

        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem(id+"2", 6.6, ItemBankStatusType.AVAILABLE, "ID5");

        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);
        List<CompetencyDifficultyDto> orderedCompetencies = competencyMapOrderService
                .getOrderedCompetencies(competencyMapOrderId);

        assertTrue(orderedCompetencies.size() == 3);

        assertOrderIdDifficulty(1, id+"1", 4.4, orderedCompetencies, 100);
        assertOrderIdDifficulty(2, id+"3", 2.2, orderedCompetencies, 100);
        assertOrderIdDifficulty(0, id+"2", 6.6, orderedCompetencies, 100);
    }

    @Test
    public void testAveragingAssessmentItemsOrderedCorrectlyOrderedOnAssessmentUpload() {
        String id = TesutoUtils.newId();
        createCompetencyMap(id, 3);

        createAssessmentItem(id+"1", 2.2, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"1", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"1", 6.6, ItemBankStatusType.AVAILABLE, "ID3");

        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem(id+"2", 6.6, ItemBankStatusType.AVAILABLE, "ID5");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        Map<String, String> competencyMapOrderIds = createOrderedMapWithAssessmentUpload(disciplines);
        for (String competencyMapOrderId : competencyMapOrderIds.values()) {
            List<CompetencyDifficultyDto> orderedCompetencies = competencyMapOrderService
                    .getOrderedCompetencies(competencyMapOrderId);

            assertTrue(orderedCompetencies.size() == 3);

            assertOrderIdDifficulty(1, id+"1", 4.4, orderedCompetencies, 100);
            assertOrderIdDifficulty(2, id+"3", 2.2, orderedCompetencies, 100);
            assertOrderIdDifficulty(0, id+"2", 6.6, orderedCompetencies, 100);
        }
    }
    
    @Test
    public void tesRemoveCompetencyWithNoAvailableAssessmentItem() {
        String id = TesutoUtils.newId();
        createCompetencyMap(id, 3);

        createAssessmentItem(id+"1", 2.2, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"1", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"1", 6.6, ItemBankStatusType.AVAILABLE, "ID3");

        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.DO_NOT_USE, "ID4");
        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.REVIEW, "ID6");
        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.GET_MORE_DATA, "ID7");
        createAssessmentItem(id+"2", 6.6, ItemBankStatusType.AVAILABLE, "ID5");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        Map<String, String>  competencyMapOrderIds = createOrderedMapWithAssessmentUpload(disciplines);
        for (String competencyMapOrderId : competencyMapOrderIds.values()) {
            List<CompetencyDifficultyDto> orderedCompetencies = competencyMapOrderService
                    .getOrderedCompetencies(competencyMapOrderId);

            assertTrue(orderedCompetencies.size() == 2);

            assertOrderIdDifficulty(1, id+"1", 4.4, orderedCompetencies, 100);
            assertOrderIdDifficulty(0, id+"2", 6.6, orderedCompetencies, 100);
        }
    }
    
    @Test
    public void testReturnsNullNoQualifingCompetencies() {
        String id = TesutoUtils.newId();
        createCompetencyMap(id, 3);
        createAssessmentItem(id+"1", 2.2, ItemBankStatusType.DO_NOT_USE, "ID1");
        createAssessmentItem(id+"1", 4.4, ItemBankStatusType.DO_NOT_USE, "ID2");
        createAssessmentItem(id+"1", 6.6, ItemBankStatusType.DO_NOT_USE, "ID3");

        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.DO_NOT_USE, "ID4");
        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.REVIEW, "ID6");
        createAssessmentItem(id+"3", 2.2, ItemBankStatusType.GET_MORE_DATA, "ID7");
        createAssessmentItem(id+"2", 6.6, ItemBankStatusType.GET_MORE_DATA, "ID5");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        Map<String, String>  competencyMapOrderIds = createOrderedMapWithAssessmentUpload(disciplines);
        assertTrue(competencyMapOrderIds == null || CollectionUtils.isEmpty(competencyMapOrderIds.values()));
    }
    
    @Test
    public void testReturnsCurrectBasedOnStudentAbilityPositionCompetencies() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createCompetencyMap(id, 7);

        createAssessmentItem(id+"1", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"4", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem(id+"5", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"2", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"6", 4.4, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"7", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"3", 7.7, ItemBankStatusType.AVAILABLE, "ID3");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);
        
        Integer position = competencyMapOrderService.findPositionByAbility(competencyMapOrderId, 7.8);
        assertTrue(String.format("Position incorrect for %s, position should be %s is %s", 7.8, 0, position),position == 0);
        
        position = competencyMapOrderService.findPositionByAbility(competencyMapOrderId, 7.7);
        assertTrue(String.format("Position incorrect for %s, position should be %s is %s", 7.7, 1, position), position == 1);
        
        position = competencyMapOrderService.findPositionByAbility(competencyMapOrderId, 4.4);
        assertTrue(String.format("Position incorrect for %s, position should be %s is %s",4.4,4,position), position == 4);
        
        position = competencyMapOrderService.findPositionByAbility(competencyMapOrderId, 1.0);
        assertTrue(String.format("Position incorrect for %s, position should be %s is %s",1.0, 7, position), position == 7);
        
    }

    @Test
    public void selectOrganizeByAbilityReturnsCorrectResponseForBasicInput() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem("Competency22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 8.0, 0, 3);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 3);
        assertTrue(orderedCompetencies.getTolearn().size() == 3);
        CompetencyDto competencyMastered = orderedCompetencies.getMastered().get(0).getCompetency();
        CompetencyDto competencyToLearn = orderedCompetencies.getTolearn().get(0).getCompetency();
        
        assertTrue(competencyMastered.getDiscipline().equals("Discipline"));
        assertTrue(competencyMastered.getIdentifier().equals(id+"24"));
        assertTrue(orderedCompetencies.getMastered().get(0).getDifficulty() == 7.15);
        assertTrue(competencyToLearn.getIdentifier().equals(id+"233"));
        assertTrue(orderedCompetencies.getTolearn().get(0).getDifficulty() == 10.1);
    }

    @Test
    public void selectOrganizeByAbilityReturnsProperlyForDifficultyHigherThanAllCompetencies() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem("Competency22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 12.0, 0, 3);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 3);
        assertTrue(orderedCompetencies.getTolearn().size() == 0);
        CompetencyDto competencyMastered = orderedCompetencies.getMastered().get(0).getCompetency();
        assertTrue(competencyMastered.getDiscipline().equals("Discipline"));
        assertTrue(competencyMastered.getIdentifier().equals(id+"233"));
        assertTrue(orderedCompetencies.getMastered().get(0).getDifficulty() == 10.1);
    }

    @Test
    public void selectOrganizeByAbilityReturnsProperlyForDifficultyLowerThanAllCompetencies() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem(id+"22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 1.0, 0, 3);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 0);
        assertTrue(orderedCompetencies.getTolearn().size() == 3);
        CompetencyDto competencyToLearn = orderedCompetencies.getTolearn().get(0).getCompetency();
        assertTrue(competencyToLearn.getDiscipline().equals("Discipline"));
        assertTrue(competencyToLearn.getIdentifier().equals(id+"23"));
        assertTrue(orderedCompetencies.getTolearn().get(0).getDifficulty() == 4.95);
    }

    @Test
    public void selectOrganizeByAbilityReturnsProperlyForNonZeroParentLevel() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem("Competency22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 7.0, 1, 3);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 0);
        assertTrue(orderedCompetencies.getTolearn().size() == 3);
        CompetencyDto competencyToLearn = orderedCompetencies.getTolearn().get(0).getCompetency();
        assertTrue(competencyToLearn.getDiscipline().equals("Discipline"));
        assertTrue(competencyToLearn.getIdentifier().equals(id+"233"));
        assertTrue(orderedCompetencies.getTolearn().get(0).getDifficulty() == 10.1);
    }

    @Test
    public void selectOrganizeByAbilityHandlesParentLevelOutOfRange() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem("Competency22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 7.0, 3, 3);

        assertTrue(result.getCompetenciesByTopic().size() == 0);
        CompetencyDto competencyMastered = result.getCompetenciesForMap().getMastered().get(0).getCompetency();
        CompetencyDto competencyToLearn = result.getCompetenciesForMap().getTolearn().get(0).getCompetency();
        assertTrue(competencyMastered.getIdentifier().equals(id+"23"));
        assertTrue(result.getCompetenciesForMap().getMastered().get(0).getDifficulty() == 4.95);
        assertTrue(competencyToLearn.getIdentifier().equals(id+"232"));
        assertTrue(result.getCompetenciesForMap().getTolearn().get(0).getDifficulty() == 9.9);
    }

    @Test
    public void selectOrganizeByAbilityHandlesCompetencyRangeOfZero() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem("Competency22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 7.0, 0, 0);

        assertTrue(result.getCompetenciesForMap().getMastered().size() == 0);
        assertTrue(result.getCompetenciesForMap().getTolearn().size() == 0);
        assertTrue(result.getCompetenciesByTopic().size() == 1);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 0);
        assertTrue(orderedCompetencies.getTolearn().size() == 0);
    }

    @Test
    public void selectOrganizeByAbilityHandlesCompetencyRangeLargerThanMap() {
        String id = TesutoUtils.newId();
        CompetencyMapDto competencyMap = createComplexCompetencyMap(id);

        createAssessmentItem(id+"21", 1.1, org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType.AVAILABLE, "ID1");
        createAssessmentItem(id+"21", 2.2, ItemBankStatusType.AVAILABLE, "ID4");
        createAssessmentItem(id+"22", 3.3, ItemBankStatusType.AVAILABLE, "ID5");
        createAssessmentItem(id+"23", 4.4, ItemBankStatusType.AVAILABLE, "ID2");
        createAssessmentItem(id+"23", 5.5, ItemBankStatusType.AVAILABLE, "ID6");
        createAssessmentItem(id+"24", 6.6, ItemBankStatusType.AVAILABLE, "ID7");
        createAssessmentItem(id+"24", 7.7, ItemBankStatusType.AVAILABLE, "ID3");
        createAssessmentItem(id+"231", 8.8, ItemBankStatusType.AVAILABLE, "ID9");
        createAssessmentItem(id+"232", 9.9, ItemBankStatusType.AVAILABLE, "ID8");
        createAssessmentItem(id+"233", 10.1, ItemBankStatusType.AVAILABLE, "ID10");

        Set<String> disciplines = new HashSet<String>();
        disciplines.add("Discipline");
        String competencyMapOrderId = createOrderedMapWithCompetencyMapDto(competencyMap);

        OrderedCompetencySet result =
                competencyMapOrderService.selectOrganizeByAbility(competencyMapOrderId, 7.0, 0, 15);

        assertTrue(result.getCompetenciesForMap().getMastered().size() == 3);
        assertTrue(result.getCompetenciesForMap().getTolearn().size() == 4);
        assertTrue(result.getCompetenciesByTopic().size() == 1);

        SelectedOrderedCompetencies orderedCompetencies =
                (SelectedOrderedCompetencies) result.getCompetenciesByTopic().values().toArray()[0];

        assertTrue(orderedCompetencies.getMastered().size() == 3);
        assertTrue(orderedCompetencies.getTolearn().size() == 4);
    }

    @After
    public void tearDown() {
        tobeDeletedAssessmentItems.forEach(ai -> assessmentItemService.delete(ai.getId()));
        tobeDeletedCompetencies.forEach(c -> competencyService.delete(c.getId()));
        tobeDeletedCompetencMapOrderids.forEach(cid -> competencyMapOrderService.delete(cid));
        tobeDeletedCompetencyMaps.forEach(cm -> competencyMapService.delete(cm.getId()));

        tobeDeletedAssessmentItems.clear();
        tobeDeletedCompetencies.clear();
        tobeDeletedCompetencMapOrderids.clear();
        tobeDeletedCompetencyMaps.clear();
    }

    private void assertOrderIdDifficulty(int order, String expectedCompetencyId, double expectedDifficulty,
            List<CompetencyDifficultyDto> orderedCompetencies, int rounding) {
        CompetencyDifficultyDto cmp = orderedCompetencies.get(order);

        double roundedDifficulty = Math.round(cmp.getDifficulty() * rounding);
        double roundedExpectedDifficulty = Math.round(expectedDifficulty * rounding);
        assertTrue(
                String.format(
                        "Competency difficulties are in correctly averaged and ordered. order:%s; id-expected:%s, id-actual:%s, difficulty-expected:%s, difficuty-actual:%s",
                        order, expectedCompetencyId, cmp.getCompetency().getIdentifier(), expectedDifficulty,
                        cmp.getDifficulty()),
                roundedExpectedDifficulty == roundedDifficulty
                        && expectedCompetencyId.equals(cmp.getCompetency().getIdentifier()));
    }

}
