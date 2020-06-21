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
package org.cccnext.tesuto.importer.service.upload;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;

import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 12/30/16.
 */
public class PackageResults extends Results {

    private List<AssessmentDto> assessmentDtos;
    private List<AssessmentItemDto> assessmentItemDtos;

    public List<AssessmentDto> getAssessmentDtos() {
        return assessmentDtos;
    }

    public void setAssessmentDtos(List<AssessmentDto> assessmentDtos) {
        this.assessmentDtos = assessmentDtos;
    }

    public List<AssessmentItemDto> getAssessmentItemDtos() {
        return assessmentItemDtos;
    }

    public void setAssessmentItemDtos(List<AssessmentItemDto> assessmentItemDtos) {
        this.assessmentItemDtos = assessmentItemDtos;
    }

}
