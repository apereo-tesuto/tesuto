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
package org.cccnext.tesuto.admin.controller;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class TestLocationFormData {
    TestLocationDto testLocationDto;
    Set<ScopedIdentifierDto> assessments;

    public TestLocationDto getTestLocationDto() {
        return testLocationDto;
    }

    public void setTestLocationDto(TestLocationDto testLocationDto) {
        this.testLocationDto = testLocationDto;
    }

    public Set<ScopedIdentifierDto> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<ScopedIdentifierDto> assessments) {
        this.assessments = assessments;
    }

    @Override
    public String toString() {
        return "TestLocationFormData{" +
                "testLocationDto=" + testLocationDto +
                ", assessments=" + assessments +
                '}';
    }
}
