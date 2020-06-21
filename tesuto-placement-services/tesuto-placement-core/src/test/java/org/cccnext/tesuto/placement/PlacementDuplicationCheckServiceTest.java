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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.model.PlacementComponent;
import org.cccnext.tesuto.placement.service.PlacementComponentAssembler;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementDuplicationCheckService;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class PlacementDuplicationCheckServiceTest {
	@Mock
    PlacementComponentService placementComponentService;
    
    @Mock
    PlacementService placementService;
    
    @InjectMocks
    PlacementDuplicationCheckService duplicationService = new PlacementDuplicationCheckService();
    
    @Autowired 
    PlacementComponentAssembler placementComponentAssembler;
    
    @Test
    public void testInjection() throws IllegalAccessException {
        Assert.assertNotNull(duplicationService);
        Assert.assertNotNull(FieldUtils.readField(duplicationService, "placementService", true));
        Assert.assertNotNull(FieldUtils.readField(duplicationService, "placementComponentService", true));
    }
    
    @Test
    public void testAssignmentIsDuplicate() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        PlacementViewDto assignedPlacement = PlacementGenerator.makePlacementViewDto(misCode, cccid, subjectId, subjectVersion);
        assignedPlacement.setAssigned(true);
        when(placementService.getPlacement(eq(assignedPlacement.getId()))).thenReturn(assignedPlacement);
        
        PlacementViewDto newAssigned = new PlacementViewDto();
        newAssigned.setId(assignedPlacement.getId());
        Assert.assertTrue(duplicationService.isDuplicateAssignedPlacement(newAssigned));
    }

    @Test
    public void testAssignmentIsNotDuplicate() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        PlacementViewDto assignedPlacement = PlacementGenerator.makePlacementViewDto(misCode, cccid, subjectId, subjectVersion);
        assignedPlacement.setAssigned(false);
        when(placementService.getPlacement(eq(assignedPlacement.getId()))).thenReturn(assignedPlacement);
        
        PlacementViewDto newAssigned = new PlacementViewDto();
        newAssigned.setId(assignedPlacement.getId());
        Assert.assertFalse(duplicationService.isDuplicateAssignedPlacement(newAssigned));
    }

    @Test
    public void testNewPlacementIsDuplicate() {
        String cccid = RandomStringUtils.randomAscii(100);
        String misCode = RandomStringUtils.randomAscii(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        List<PlacementViewDto> studentPlacements = PlacementGenerator.makeRandomPlacementsViewDtos(5,misCode, cccid, subjectId, subjectVersion);
        PlacementViewDto newPlacement = studentPlacements.get(RandomUtils.nextInt(0, studentPlacements.size()));
        when(placementService.getPlacementsForStudent(eq(misCode), eq(cccid), eq(subjectId))).thenReturn(studentPlacements);
        Assert.assertTrue(duplicationService.isDuplicateNewPlacement(newPlacement));
    }
    
    @Test
    public void testNewPlacementIsNotDuplicate() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        List<PlacementViewDto> studentPlacements = PlacementGenerator.makeRandomPlacementsViewDtos(5,misCode, cccid, subjectId, subjectVersion);
        PlacementViewDto newPlacement = PlacementGenerator.makePlacementViewDto(misCode, cccid, subjectId, subjectVersion+1);
        when(placementService.getPlacementsForStudent(eq(misCode), eq(cccid), eq(subjectId))).thenReturn(studentPlacements);
        Assert.assertFalse(duplicationService.isDuplicateNewPlacement(newPlacement));
    }
    
    @Test
    public void testNewPlacementIsNotDuplicate2() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        List<PlacementViewDto> studentPlacements = PlacementGenerator.makeRandomPlacementsViewDtos(5,misCode, cccid, subjectId, subjectVersion);
        PlacementViewDto newPlacement = PlacementGenerator.makePlacementViewDto(misCode, cccid, subjectId, subjectVersion);
        PlacementComponent component = PlacementGenerator.randomPlacementComponent("mmap", cccid, misCode, subjectId, subjectVersion);
        newPlacement.getPlacementComponents().add(placementComponentAssembler.assembleDto(component));
        when(placementService.getPlacementsForStudent(eq(misCode), eq(cccid), eq(subjectId))).thenReturn(studentPlacements);
        Assert.assertFalse(duplicationService.isDuplicateNewPlacement(newPlacement));
    }
    
    @Test
    public void testAssessComponentIsDuplicate() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        List<PlacementComponent> studentPlacementComponents = PlacementGenerator.randomPlacementComponent(5, "tesuto", 
                misCode, cccid, subjectId, subjectVersion);
        PlacementComponent placementComponent = studentPlacementComponents.get(RandomUtils.nextInt(0,  studentPlacementComponents.size()));
        when(placementComponentService.getPlacementComponents(eq(misCode), eq(cccid), eq(subjectId)))
                .thenReturn( placementComponentAssembler.assembleDto( studentPlacementComponents));
        Assert.assertTrue(duplicationService.isDuplicateAssessPlacementComponent( (TesutoPlacementComponent)placementComponent));
    }
    
    @Test
    public void testAssessComponentIsNotDuplicate() {
        String cccid = RandomStringUtils.random(100);
        String misCode = RandomStringUtils.random(100);
        Integer subjectId = ThreadLocalRandom.current().nextInt();
        Integer subjectVersion = ThreadLocalRandom.current().nextInt();

        Collection<PlacementComponentViewDto> studentPlacementComponents = placementComponentAssembler.assembleDto(
                PlacementGenerator.randomPlacementComponent(5, "tesuto", misCode, cccid, subjectId, subjectVersion));
        PlacementComponent newPlacementComponent = PlacementGenerator.randomPlacementComponent("tesuto",misCode, cccid, subjectId, subjectVersion+1);
        when(placementComponentService.getPlacementComponents(eq(misCode), eq(cccid), eq(subjectId))).thenReturn(studentPlacementComponents);
        
        Assert.assertFalse(duplicationService.isDuplicateAssessPlacementComponent((TesutoPlacementComponent)newPlacementComponent));
    }
    
}
