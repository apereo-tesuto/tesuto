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
package org.cccnext.tesuto.content.viewdto;

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;

public class CompetencyRestrictedViewDto {

    private String discipline;
    private String identifier;
    private String description;
    private String studentDescription;
    
    public CompetencyRestrictedViewDto(CompetencyDto competencyDto)  {
        discipline = competencyDto.getDiscipline();
        identifier = competencyDto.getIdentifier();
        description = competencyDto.getDescription();
        studentDescription = competencyDto.getStudentDescription();
    }
    public String getDiscipline() {
        return discipline;
    }
    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
	public String getStudentDescription() {
		return studentDescription;
	}
	public void setStudentDescription(String studentDescription) {
		this.studentDescription = studentDescription;
	}
}
