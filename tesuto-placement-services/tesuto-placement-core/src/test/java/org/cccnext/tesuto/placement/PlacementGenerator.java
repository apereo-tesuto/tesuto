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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.model.MmapPlacementComponent;
import org.cccnext.tesuto.placement.model.PlacementComponent;
import org.cccnext.tesuto.placement.view.MmapDataSourceType;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

public class PlacementGenerator {

    
    static public List<PlacementActionResult> makePlacementActionResults(int size) {
        List<PlacementActionResult> list = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            list.add( makePlacementActionResult());
        }
        return list;
    }


    static public PlacementActionResult makePlacementActionResult() {
        PlacementActionResult result = new PlacementActionResult();
        result.setPlacementId( RandomStringUtils.random(100));
        result.getPlacementComponentIds().add( RandomStringUtils.random(100));
        result.getPlacementComponentIds().add( RandomStringUtils.random(100));
        result.setTrackingId( RandomStringUtils.random(100));
        result.setCccid( RandomStringUtils.random(100));
        result.setCollegeId( RandomStringUtils.random(100));
        result.setSubjectAreaId(RandomUtils.nextInt());
        result.setSubjectAreaVersion( RandomUtils.nextInt());
        result.setRuleSetId( RandomStringUtils.random(100));
        result.setRuleSetRowId( RandomStringUtils.random(100));
        return result;
    }
    
    static public List<PlacementViewDto> makeRandomPlacementsViewDtos( int size, String misCode, String cccid, Integer subjectId, Integer subjectVersion) {
        Set<PlacementViewDto> set = new HashSet<>(size);
        for (int i = 1; i <= size; i++) {
            set.add(makePlacementViewDto(misCode, cccid, subjectId, subjectVersion));
        }
        return new ArrayList<>(set);
    }
    
    static public PlacementViewDto makePlacementViewDto( String misCode, String cccid, Integer subjectAreaId, Integer subjectVersion) {
        PlacementViewDto dto = new PlacementViewDto();
        dto.setCccid(cccid);
        dto.setCollegeId(misCode);
        dto.setDisciplineId(subjectAreaId);
        dto.setSubjectAreaName( RandomStringUtils.random(20));
        dto.setSubjectAreaVersion(subjectVersion);
        dto.setAssigned( RandomUtils.nextBoolean());
        dto.setAssignedDate( DateUtils.addDays(Calendar.getInstance().getTime(), -1 * RandomUtils.nextInt(4, 60)));
        dto.setCreateRuleSetId( RandomStringUtils.random(100));
        dto.setAssignedRuleSetId( RandomStringUtils.random(100));
        dto.setCreatedOn( DateUtils.addDays(Calendar.getInstance().getTime(), -1 * RandomUtils.nextInt(4, 60)));
        
        dto.setPlacementComponents(new HashSet<>());
        dto.getPlacementComponents().add(randomPlacementComponentViewDto("mmap", cccid, misCode, subjectAreaId,  subjectVersion));
        dto.getPlacementComponents().add(randomPlacementComponentViewDto("tesuto", cccid, misCode, subjectAreaId,  subjectVersion));

        
        VersionedSubjectAreaViewDto sDto = new VersionedSubjectAreaViewDto();
        sDto.setDisciplineId(subjectAreaId);
        sDto.setTitle(dto.getSubjectAreaName());
        sDto.setVersion(subjectVersion);
        dto.setVersionedSubjectAreaViewDto(sDto);
        return dto;
    }
    
    static public List<PlacementComponentViewDto> randomPlacementComponentDto(int size, String componentType, String cccid, String misCode,
            Integer subjectAreaId, Integer subjectAreaVersion) {
        List<PlacementComponentViewDto> set = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            set.add(randomPlacementComponentViewDto(componentType, misCode, cccid, subjectAreaId, subjectAreaVersion));
        }
        return  set;
    }
    
    static public PlacementComponentViewDto randomPlacementComponentViewDto(String componentType, String cccid, String misCode,
            Integer subjectAreaId, Integer subjectAreaVersion) {
        PlacementComponent component = randomPlacementComponent(componentType, misCode, cccid, subjectAreaId, subjectAreaVersion);
        PlacementComponentViewDto dto = new PlacementComponentViewDto();
        dto.setCreatedOn(component.getCreatedOn());
        dto.setCb21Code(component.getCb21Code());
        dto.setCollegeId(component.getCollegeId());
        dto.setCccid(component.getCccid());
        dto.setSubjectAreaId(component.getSubjectAreaId());
        dto.setSubjectAreaVersion(component.getSubjectAreaVersion());
        dto.setId(component.getId());
        dto.setTrackingId(component.getTrackingId());
        return dto;
    }

    static public List<PlacementComponent> randomPlacementComponent(int size, String componentType, String cccid, String misCode,
            Integer subjectAreaId, Integer subjectAreaVersion) {
        List<PlacementComponent> set = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            set.add(randomPlacementComponent(componentType, misCode, cccid, subjectAreaId, subjectAreaVersion));
        }
        return set;
    }

    static public PlacementComponent randomPlacementComponent(String componentType, String cccid, String misCode,
            Integer subjectAreaId, Integer subjectAreaVersion) {
        
        PlacementComponent placementComponent = null;
        if (componentType == "tesuto") {
            TesutoPlacementComponent tesuto = new TesutoPlacementComponent();
            tesuto.setAssessmentSessionId(RandomStringUtils.random(100));
            tesuto.setAssessmentTitle(RandomStringUtils.random(256));
            tesuto.setAssessmentDate( DateUtils.addDays(Calendar.getInstance().getTime(), -1 * RandomUtils.nextInt(4, 60)));
            tesuto.setStudentAbility( Float.valueOf(RandomUtils.nextFloat(0.0f, 5.0f)).doubleValue());
            placementComponent = tesuto;
        } else {
            MmapPlacementComponent mmap = new MmapPlacementComponent();
            mmap.setDataSource( RandomStringUtils.random(100));
            mmap.setDataSourceDate( DateUtils.addDays(Calendar.getInstance().getTime(), -1 * RandomUtils.nextInt(4, 60)));
            mmap.setDataSourceType( MmapDataSourceType.SELF_REPORTED);
            mmap.setMmapCourseCategories( IntStream.range(1, 5).mapToObj( i -> RandomStringUtils.random(100)).collect( Collectors.toList()));
            mmap.setMmapVariableSetId( RandomStringUtils.random(19));
            mmap.setMmapRuleId( RandomStringUtils.random(19));
            mmap.setMmapRuleSetId( RandomStringUtils.random(19));
            mmap.setMmapRuleSetRowId( RandomStringUtils.random(19));
            mmap.setMmapRowNumber( RandomUtils.nextInt(0,100));
            mmap.setStandalonePlacement( RandomUtils.nextBoolean());
            placementComponent = mmap;
        }
            
        placementComponent.setId( RandomStringUtils.randomAlphanumeric(100));
        placementComponent.setCccid( cccid);
        placementComponent.setCb21Code( RandomStringUtils.random(1, "YABCDEFGH").charAt(0));
        placementComponent.setCourseGroup( RandomUtils.nextInt(1, 5));
        placementComponent.setCollegeId( misCode);
        placementComponent.setTriggerData( RandomStringUtils.random(300));
        placementComponent.setSubjectAreaId( subjectAreaId);
        placementComponent.setSubjectAreaVersion( subjectAreaVersion);
        placementComponent.setCreatedOn( DateUtils.addDays(Calendar.getInstance().getTime(), -1 * RandomUtils.nextInt(4, 60)));
        return placementComponent;
    }
}
