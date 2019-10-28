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

import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.springboot.web.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class PlacementCollegeAuthorizerService extends AuthorizerService {
    
    @Autowired
    private SubjectAreaServiceAdapter service;
    
    public SubjectAreaServiceAdapter getService() {
        return service;
    }

    public void authorizeForDiscipline(int disciplineId, UserAccountDto user) {
        DisciplineViewDto discipline = service.getDiscipline(disciplineId);
        if (discipline == null) {
            throw new NotFoundException("Cannot find discipline with id " + disciplineId);
        }
        authorize(discipline.getCollegeId(), user);
    }

    public void authorizeForCourse(int courseId, UserAccountDto user) {
        Set<String> collegeIds = service.getCollegeIdsForCourse(courseId);
        if ( !user.getCollegeIds().containsAll(collegeIds)) {
            throw new AccessDeniedException("Insufficient privileges on colleges for course " + courseId);
        }
    }
}
