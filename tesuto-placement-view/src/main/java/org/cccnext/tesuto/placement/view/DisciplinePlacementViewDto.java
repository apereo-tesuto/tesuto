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
package org.cccnext.tesuto.placement.view;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class DisciplinePlacementViewDto implements Serializable {
    
    private static final long serialVersionUID = 3L;

    DisciplineViewDto disciplineViewDto;
    
    SequenceInfoViewDto sequenceInfo;
    
    List<CourseViewDto> courseViewDtos;

    public DisciplineViewDto getDisciplineViewDto() {
        return disciplineViewDto;
    }

    public void setDisciplineViewDto(DisciplineViewDto disciplineViewDto) {
        this.disciplineViewDto = disciplineViewDto;
    }

    public List<CourseViewDto> getCourseViewDtos() {
        return courseViewDtos;
    }

    public void setCourseViewDtos(List<CourseViewDto> courseViewDtos) {
        this.courseViewDtos = courseViewDtos;
    }

    public SequenceInfoViewDto getSequenceInfo() {
        return sequenceInfo;
    }

    public void setSequenceInfo(SequenceInfoViewDto sequenceInfo) {
        this.sequenceInfo = sequenceInfo;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
