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
package org.cccnext.tesuto.web.service;

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.util.TesutoUtils;
import org.cccnext.tesuto.web.repository.StudentCollegeAffiliationRepository;
import org.cccnext.tesuto.web.service.StudentCollegeAffiliationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-test.xml" })
@Transactional
public class StudentCollegeAffiliationServiceTest {
    @Autowired
    StudentCollegeAffiliationRepository repository;

    @Resource(name = "studentCollegeAffiliationService")
    StudentCollegeAffiliationService service;

    private StudentCollegeAffiliationDto createAffiliation() {
        String eppn = TesutoUtils.newId();
        String cccId = TesutoUtils.newId();
        String misCode = TesutoUtils.newId();
        String authSource = TesutoUtils.newId();
        service.createIfNotExists(eppn, cccId, misCode, authSource);
        StudentCollegeAffiliationDto dto = new StudentCollegeAffiliationDto();
        dto.setEppn(eppn);
        dto.setStudentCccId(cccId);
        dto.setMisCode(misCode);
        dto.setAuthSource(authSource);
        return dto;
    }

    @Test
    public void testCreate() throws Exception {
        StudentCollegeAffiliationDto dto = createAffiliation();
        StudentCollegeAffiliationDto found = service.find(dto.getEppn(), dto.getStudentCccId(), dto.getMisCode());
        Assert.assertNotNull(found);
        Assert.assertEquals(dto, found);
    }

    @Test
    public void testDelete() throws Exception {
        StudentCollegeAffiliationDto dto = createAffiliation();
        service.delete(dto.getEppn(), dto.getStudentCccId(), dto.getMisCode());
        StudentCollegeAffiliationDto found = service.find(dto.getEppn(), dto.getStudentCccId(), dto.getMisCode());
        Assert.assertNull(found);
    }

    @Test
    public void testfindByCccIdAndMisCode() throws Exception {
        StudentCollegeAffiliationDto dto = createAffiliation();
        StudentCollegeAffiliationDto found = service.findByCccIdAndMisCode(dto.getStudentCccId(), dto.getMisCode());
        Assert.assertEquals(dto, found);
    }

    @Test(expected = NotFoundException.class)
    public void testfindByCccIdAndMisCodeIfStudentNotFound() throws Exception {
        String expectedCccId = TesutoUtils.newId();
        String expectedMisCode = TesutoUtils.newId();
        StudentCollegeAffiliationDto found = service.findByCccIdAndMisCode(expectedCccId, expectedMisCode);
    }

    @Test
    public void testfindByCccIdAndMisCodeReturnsMostRecentEppn() throws Exception {
        service.createIfNotExists(TesutoUtils.newId(), TesutoUtils.newId(), TesutoUtils.newId(), "whatever");
        service.createIfNotExists(TesutoUtils.newId(), TesutoUtils.newId(), TesutoUtils.newId(), "whatever");
        Thread.sleep(1000l);
        StudentCollegeAffiliationDto dto = createAffiliation();
        StudentCollegeAffiliationDto found = service.findByCccIdAndMisCode(dto.getStudentCccId(), dto.getMisCode());
        Assert.assertEquals(dto, found);
    }
}
