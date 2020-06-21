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
package org.cccnext.tesuto.delivery.view;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.metadata.ToolsItemMetadataDto;

import java.util.List;

/**
 * Created by bruce on 11/19/15.
 */
public class AssessmentItemViewDto {

    private List<ResponseVarViewDto> responseVars;
    private String stylesheets;
    private String itemBody;

    // All interaction objects are pulled in from the content model
    private List<AssessmentInteractionDto> interactions;

    private ToolsItemMetadataDto tools;

    public List<ResponseVarViewDto> getResponseVars() {

        return responseVars;
    }

    public void setResponseVars(List<ResponseVarViewDto> responseVars) {
        this.responseVars = responseVars;
    }

    public String getStylesheets() {
        return stylesheets;
    }

    public void setStylesheets(String stylesheets) {
        this.stylesheets = stylesheets;
    }

    public String getItemBody() {
        return itemBody;
    }

    public void setItemBody(String itemBody) {
        this.itemBody = itemBody;
    }

    public List<AssessmentInteractionDto> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<AssessmentInteractionDto> interactions) {
        this.interactions = interactions;
    }

    public ToolsItemMetadataDto getTools() {
        return tools;
    }

    public void setTools(ToolsItemMetadataDto tools) {
        this.tools = tools;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AssessmentItemViewDto))
            return false;

        AssessmentItemViewDto that = (AssessmentItemViewDto) o;

        if (getResponseVars() != null ? !getResponseVars().equals(that.getResponseVars())
                : that.getResponseVars() != null)
            return false;
        if (getStylesheets() != null ? !getStylesheets().equals(that.getStylesheets()) : that.getStylesheets() != null)
            return false;
        if (getItemBody() != null ? !getItemBody().equals(that.getItemBody()) : that.getItemBody() != null)
            return false;
        return !(getInteractions() != null ? !getInteractions().equals(that.getInteractions())
                : that.getInteractions() != null);

    }

    @Override
    public int hashCode() {
        int result = getResponseVars() != null ? getResponseVars().hashCode() : 0;
        result = 31 * result + (getStylesheets() != null ? getStylesheets().hashCode() : 0);
        result = 31 * result + (getItemBody() != null ? getItemBody().hashCode() : 0);
        result = 31 * result + (getInteractions() != null ? getInteractions().hashCode() : 0);
        result = 31 * result + (getTools() != null ? getTools().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssessmentItemViewDto{" + "responseVars=" + responseVars + ", stylesheets='" + stylesheets + '\''
                + ", itemBody='" + itemBody + '\'' + ", interactions=" + interactions + ", tools=" + tools + '}';
    }
}
