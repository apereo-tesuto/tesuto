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
package org.cccnext.tesuto.content.model.metadata;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.content.model.AbstractAssessment;

/*
 * @author jbrown@unicon.net
 */
public class AssessmentMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 3l;

    private String type;
    private String identifier;
    private String authoringTool;
    private String authoringToolVersion;
    private String author;
    private String available;
    private String displayInHistory;
    private String displayGeneralInstructions;
    private String displayGeneralClosing;
    private String autoActivate;
    private String requirePasscode;
    private PrerequisiteMetadata preRequisite;
    private Double scaleAdditiveTerm;
    private Double scaleMultiplicativeTerm;
    private List<String> competencyMapDisciplines;
    private List<SectionMetadata> section;
    private OverallPerformanceMetadata overallPerformanceMetadata;
    private CompetencyPerformanceMetadata competencyPerformanceMetadata;
    private String instructions;
    private DeliveryTypeMetadata deliveryType;
    private String generateAssessmentPlacement;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getDisplayInHistory() {
        return displayInHistory;
    }

    public void setDisplayInHistory(String displayInHistory) {
        this.displayInHistory = displayInHistory;
    }

    public String getDisplayGeneralInstructions() {
        return displayGeneralInstructions;
    }

    public void setDisplayGeneralInstructions(String displayGeneralInstructions) {
        this.displayGeneralInstructions = displayGeneralInstructions;
    }

    public String getDisplayGeneralClosing() {
        return displayGeneralClosing;
    }

    public void setDisplayGeneralClosing(String displayGeneralClosing) {
        this.displayGeneralClosing = displayGeneralClosing;
    }

    public String getAutoActivate() {
        return autoActivate;
    }

    public void setAutoActivate(String autoActivate) {
        this.autoActivate = autoActivate;
    }

    public String getRequirePasscode() {
        return requirePasscode;
    }

    public void setRequirePasscode(String requirePasscode) {
        this.requirePasscode = requirePasscode;
    }

    public PrerequisiteMetadata getPreRequisite() {
        return preRequisite;
    }

    public void setPreRequisite(PrerequisiteMetadata preRequisite) {
        this.preRequisite = preRequisite;
    }

    public Double getScaleAdditiveTerm() {
        return scaleAdditiveTerm;
    }

    public void setScaleAdditiveTerm(Double scaleAdditiveTerm) {
        this.scaleAdditiveTerm = scaleAdditiveTerm;
    }

    public Double getScaleMultiplicativeTerm() {
        return scaleMultiplicativeTerm;
    }

    public void setScaleMultiplicativeTerm(Double scaleMultiplicativeTerm) {
        this.scaleMultiplicativeTerm = scaleMultiplicativeTerm;
    }

    public List<String> getCompetencyMapDisciplines() {
        return competencyMapDisciplines;
    }

    public void setCompetencyMapDisciplines(List<String> competencyMapDisciplines) {
        this.competencyMapDisciplines = competencyMapDisciplines;
    }

    public List<SectionMetadata> getSection() {
        return section;
    }

    public void setSection(List<SectionMetadata> section) {
        this.section = section;
    }

    public OverallPerformanceMetadata getOverallPerformanceMetadata() {
        return overallPerformanceMetadata;
    }

    public void setOverallPerformanceMetadata(OverallPerformanceMetadata overallPerformanceMetadata) {
        this.overallPerformanceMetadata = overallPerformanceMetadata;
    }

    public CompetencyPerformanceMetadata getCompetencyPerformanceMetadata() {
        return competencyPerformanceMetadata;
    }

    public void setCompetencyPerformanceMetadata(CompetencyPerformanceMetadata competencyPerformanceMetadata) {
        this.competencyPerformanceMetadata = competencyPerformanceMetadata;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public DeliveryTypeMetadata getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryTypeMetadata deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getGenerateAssessmentPlacement() {
        return generateAssessmentPlacement;
    }

    public void setGenerateAssessmentPlacement(String generateAssessmentPlacement) {
        this.generateAssessmentPlacement = generateAssessmentPlacement;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this);

    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
