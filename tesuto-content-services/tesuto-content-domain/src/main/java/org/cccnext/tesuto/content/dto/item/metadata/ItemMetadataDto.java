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
package org.cccnext.tesuto.content.dto.item.metadata;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ItemMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 2l;

    private String type;
    private String identifier;
    @JacksonXmlProperty(localName = "itemBankStatus")
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
    @JacksonXmlElementWrapper(localName = "commonCore")
    @JacksonXmlProperty(localName = "commonCoreRefId")
    private List<String> commonCoreRef;
    private String lexile;
    private String passage;
    private String passageType;
    private String includes;
    private String readingLevel;
    private CompetenciesItemMetadataDto competencies;
    private ToolsItemMetadataDto tools;

    public CompetenciesItemMetadataDto getCompetencies() {
        return competencies;
    }

    public void setCompetencies(CompetenciesItemMetadataDto competencies) {
        this.competencies = competencies;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ItemBankStatusType getItemBankStatusType() {
        return itemBankStatusType;
    }

    public void setItemBankStatusType(ItemBankStatusType itemBankStatusType) {
        this.itemBankStatusType = itemBankStatusType;
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

    public Double getCalibratedDifficulty() {
        return calibratedDifficulty;
    }

    public void setCalibratedDifficulty(Double calibratedDifficulty) {
        this.calibratedDifficulty = calibratedDifficulty;
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

    public ToolsItemMetadataDto getTools() {
        return tools;
    }

    public void setTools(ToolsItemMetadataDto tools) {
        this.tools = tools;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
