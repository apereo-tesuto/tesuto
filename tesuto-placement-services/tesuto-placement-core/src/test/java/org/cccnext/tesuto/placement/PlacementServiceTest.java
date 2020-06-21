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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.repository.jpa.PlacementRepository;
import org.cccnext.tesuto.placement.service.PlacementNotificationService;
import org.cccnext.tesuto.placement.service.PlacementServiceImpl;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PlacementServiceTest {
    
    @Mock
    PlacementRepository placementRepository;
    
    @Mock
    PlacementNotificationService placementNotificationService;
    
    @InjectMocks
    PlacementServiceImpl placementService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void verifyAssignNoAssignedPlacements() {
        PlacementViewDto placementViewDto =  setupPlacementReturn();
        ArgumentCaptor<Placement> placementCapture = ArgumentCaptor.forClass(Placement.class);
        when(placementRepository.findByCollegeIdAndCccidAndDisciplineId(anyString(), anyString(), anyInt())).thenReturn(new HashSet<Placement>());
        placementService.updateAssignedPlacements(placementViewDto);
        
        verify(placementRepository).save(placementCapture.capture());
        
        Placement placement = placementCapture.getValue();
        
        Assert.assertNotNull(placement.getAssignedDate());
        Assert.assertEquals("id", placement.getId());
        Assert.assertTrue(placement.isAssigned());
    }
    
    @Test
    public void verifyAssignAssignedPlacements() {
        PlacementViewDto placementViewDto =  setupPlacementReturn();
        
        Placement wasAssigned = new Placement();
        wasAssigned.setId("wasAssigned");
        wasAssigned.setAssigned(true);
       List<Placement> storedPlacements = new ArrayList<Placement>();
       storedPlacements.add(wasAssigned);
       
        ArgumentCaptor<Placement> placementCapture = ArgumentCaptor.forClass(Placement.class);
        
        when(placementRepository.findByCollegeIdAndCccidAndDisciplineId(anyString(), anyString(), anyInt())).thenReturn(storedPlacements);
        placementService.updateAssignedPlacements(placementViewDto);
        
        verify(placementRepository, times(2)).save(placementCapture.capture());
        
        List<Placement> placements = placementCapture.getAllValues();
        
        Assert.assertNull(placements.get(0).getAssignedDate());
        Assert.assertEquals("wasAssigned", placements.get(0).getId());
        Assert.assertFalse(placements.get(0).isAssigned());
        
        Assert.assertNotNull(placements.get(1).getAssignedDate());
        Assert.assertEquals("id", placements.get(1).getId());
        Assert.assertTrue(placements.get(1).isAssigned());
    }
    
    @Test
    public void verifyAssignWithPreviouslyAssignedPlacements() {
        PlacementViewDto placementViewDto =  setupPlacementReturn();
        
        Placement wasAssgned = new Placement();
        wasAssgned.setId("wasAssgned");
        wasAssgned.setAssigned(true);
        
        Placement storedId = new Placement();
        storedId.setId("id");
        storedId.setAssigned(false);
       List<Placement> storedPlacements = new ArrayList<Placement>();
       storedPlacements.add(storedId);
       storedPlacements.add(wasAssgned);
       
        ArgumentCaptor<Placement> placementCapture = ArgumentCaptor.forClass(Placement.class);
        
        when(placementRepository.findByCollegeIdAndCccidAndDisciplineId(anyString(), anyString(), anyInt())).thenReturn(storedPlacements);
       
        placementService.updateAssignedPlacements(placementViewDto);
        
        verify(placementRepository, times(2)).save(placementCapture.capture());
        
        List<Placement> placements = placementCapture.getAllValues();
        
        Assert.assertNull(placements.get(0).getAssignedDate());
        Assert.assertEquals("wasAssgned", placements.get(0).getId());
        Assert.assertFalse(placements.get(0).isAssigned());
        
        Assert.assertNotNull(placements.get(1).getAssignedDate());
        Assert.assertEquals("id", placements.get(1).getId());
        Assert.assertTrue(placements.get(1).isAssigned());
    }
    
    private PlacementViewDto setupPlacementReturn() {
        PlacementViewDto placementViewDto = new PlacementViewDto();
        placementViewDto.setId("id");
        placementViewDto.setCccid("cccid");
        placementViewDto.setCollegeId("collegeId");
        placementViewDto.setDisciplineId(1);
        
        Placement placement = new Placement();
        placement.setId(placementViewDto.getId());
        placement.setAssigned(false);
        when(placementRepository.getOne(placementViewDto.getId())).thenReturn(placement);
        return placementViewDto;
    }
}
