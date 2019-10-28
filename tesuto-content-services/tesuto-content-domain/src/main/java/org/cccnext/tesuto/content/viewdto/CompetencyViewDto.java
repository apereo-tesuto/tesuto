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

import java.util.List;

import org.cccnext.tesuto.domain.dto.Dto;

/**
 * Created by jasonbrown on 6/23/16.
 */
public class CompetencyViewDto implements Dto {

    private static final long serialVersionUID = 1L;
    
    private String discipline;
    private String identifier;
    private int version;
    private boolean published;
    private String description;
    private List<CompetencyViewDto> childCompetencyViewDtos;

    public List<CompetencyViewDto> getChildCompetencyViewDtos() {
        return childCompetencyViewDtos;
    }

    public void setChildCompetencyViewDtos(List<CompetencyViewDto> childCompetencyViewDto) {
        this.childCompetencyViewDtos = childCompetencyViewDto;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
