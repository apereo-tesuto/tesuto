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
package org.cccnext.tesuto.placement.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;

import org.cccnext.tesuto.admin.client.UserAccountClient;
import org.cccnext.tesuto.admin.dto.UserAccountDto;

import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserContextService;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class VersionedSubjectAreaControllerTest {
    private static String COLLEGE_DISCIPLINE_URI = "classpath:sample-subjectarea.json";

    private MockMvc mockMvc;
    
    @Mock
    UserContextService userContextService;
    
    @Mock
    UserAccountClient userAccountService;
    
    @Mock
    RequestForwardService requestForwardService;
    
    @Mock
    StudentReader studentService;


    @Mock
    private PlacementCollegeAuthorizerService authorizationService;

    @Mock SubjectAreaServiceAdapter service;

    @InjectMocks
    SubjectAreaController subjectAreaController;

    Discipline sampleSubjectArea;
    VersionedSubjectAreaViewDto sampleDto;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subjectAreaController).build();
        doNothing().when(authorizationService).authorizeForDiscipline(anyInt(), any(UserAccountDto.class));
        sampleDto = createVersionedSubjectArea();
        FieldUtils.writeField(subjectAreaController, "service", service, true);
        FieldUtils.writeField(service, "mapper", new DozerBeanMapper(), true);
    }

    @Test
    public void publishedSubjectArea() throws Exception {
      when(service.createVersionedSubjectArea(sampleSubjectArea.getDisciplineId())).thenReturn(sampleDto);
          MvcResult result = this.mockMvc.perform(put("/service/v1/subject-areas/" + sampleDto.getDisciplineId() + "/publish")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        VersionedSubjectAreaViewDto dto = (new ObjectMapper()).readValue(result.getResponse().getContentAsString(), VersionedSubjectAreaViewDto.class);
        Assert.assertEquals(sampleDto.getCollegeId(), dto.getCollegeId());
        Assert.assertEquals(sampleDto.getDisciplineId(), dto.getDisciplineId());
        Assert.assertEquals( sampleDto.getVersion().intValue(), dto.getVersion().intValue());
    }

    @Test
    public void unpublishedSubjectArea() throws Exception {
      doNothing().when(service).unpublishSubjectArea(eq(sampleSubjectArea.getDisciplineId()));
      this.mockMvc.perform(put("/service/v1/subject-areas/" + sampleDto.getDisciplineId() + "/unpublish")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());


    }

    @Test
    public void getPublishedSubjectArea() throws Exception {
      when(service.getVersionedSubjectAreaDto(eq(sampleDto.getDisciplineId()), eq(sampleDto.getVersion()))).thenReturn(sampleDto);
        MvcResult result = this.mockMvc.perform(get("/service/v1/subject-areas/" + sampleDto.getDisciplineId() + "/version/" + sampleDto.getVersion())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

      VersionedSubjectAreaViewDto dto = (new ObjectMapper()).readValue(result.getResponse().getContentAsString(), VersionedSubjectAreaViewDto.class);
      Assert.assertEquals(sampleDto, dto);
    }

    @Test
    public void getPublishedSubjectAreaNoFound() throws Exception {
      when(service.getVersionedSubjectAreaDto(2, 4)).thenReturn((VersionedSubjectAreaViewDto)null);
      this.mockMvc.perform(get("/service/v1/subject-areas/2/version/4")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    private VersionedSubjectAreaViewDto createVersionedSubjectArea() throws Exception {
        File src = ResourceUtils.getFile(COLLEGE_DISCIPLINE_URI);
        ObjectMapper objectMapper = new ObjectMapper();
        sampleSubjectArea = objectMapper.readValue(src, Discipline.class);
        sampleSubjectArea.setCompetencyAttributes(CompetencyAttributes.createInstance(sampleSubjectArea.getCompetencyMapDiscipline()));
        Mapper mapper = new DozerBeanMapper();
        VersionedSubjectAreaViewDto dto = mapper.map(sampleSubjectArea, VersionedSubjectAreaViewDto.class);
        dto.setPublished(true);
        dto.setPublishedDate(Calendar.getInstance().getTime());
        dto.setVersion(2);
        return dto;
    }

}
