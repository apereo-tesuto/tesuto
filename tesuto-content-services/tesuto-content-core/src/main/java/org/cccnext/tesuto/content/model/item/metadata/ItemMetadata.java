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
package org.cccnext.tesuto.content.model.item.metadata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;
import org.cccnext.tesuto.content.model.AbstractAssessment;

import java.util.List;

public class ItemMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 2l;

    private String type;
    private String identifier;
    private ItemBankStatusType itemBankStatusType;
    private String authoringTool;
    private String authoringToolVersion;
    private String authoringToolAnswerType;
    private String author;
    private String difficulty;
    private Double calibratedDifficulty;
    private String weightedCategory;
    private String contextual;
    private String theme;
    private List<String> commonCoreRef;
    private String lexile;
    private String passage;
    private String passageType;
    private String includes;
    private String readingLevel;
    private CompetenciesItemMetadata competencies;
    private ToolsItemMetadata tools;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ToolsItemMetadata getTools() {
        return tools;
    }

    public void setTools(ToolsItemMetadata tools) {
        this.tools = tools;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAuthoringTool() {
        return authoringTool;
    }

    public void setAuthoringTool(String authoringTool) {
        this.authoringTool = authoringTool;
    }

    public String getAuthoringToolVersion() {
        return authoringToolVersion;
    }

    public void setAuthoringToolVersion(String authoringToolVersion) {
        this.authoringToolVersion = authoringToolVersion;
    }

    public String getAuthoringToolAnswerType() {
        return authoringToolAnswerType;
    }

    public void setAuthoringToolAnswerType(String authoringToolAnswerType) {
        this.authoringToolAnswerType = authoringToolAnswerType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getWeightedCategory() {
        return weightedCategory;
    }

    public void setWeightedCategory(String weightedCategory) {
        this.weightedCategory = weightedCategory;
    }

    public String getContextual() {
        return contextual;
    }

    public void setContextual(String contextual) {
        this.contextual = contextual;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<String> getCommonCoreRef() {
        return commonCoreRef;
    }

    public void setCommonCoreRef(List<String> commonCoreRef) {
        this.commonCoreRef = commonCoreRef;
    }

    public String getLexile() {
        return lexile;
    }

    public void setLexile(String lexile) {
        this.lexile = lexile;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public String getPassageType() {
        return passageType;
    }

    public void setPassageType(String passageType) {
        this.passageType = passageType;
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getReadingLevel() {
        return readingLevel;
    }

    public void setReadingLevel(String readingLevel) {
        this.readingLevel = readingLevel;
    }

    public CompetenciesItemMetadata getCompetencies() {
        return competencies;
    }

    public void setCompetencies(CompetenciesItemMetadata competencies) {
        this.competencies = competencies;
    }

    public ItemBankStatusType getItemBankStatusType() {
        return itemBankStatusType;
    }

    public void setItemBankStatusType(ItemBankStatusType itemBankStatusType) {
        this.itemBankStatusType = itemBankStatusType;
    }

    public Double getCalibratedDifficulty() {
        return calibratedDifficulty;
    }

    public void setCalibratedDifficulty(Double calibratedDifficulty) {
        this.calibratedDifficulty = calibratedDifficulty;
    }

    @Override
    public boolean equals(Object o) {
        return new EqualsBuilder().reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder().reflectionHashCode(this);

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
