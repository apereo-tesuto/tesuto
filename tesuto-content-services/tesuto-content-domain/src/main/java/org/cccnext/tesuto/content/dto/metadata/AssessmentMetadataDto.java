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
package org.cccnext.tesuto.content.dto.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.enums.MetadataType;

import java.util.HashMap;
import java.util.List;

/*
 * @author jbrown@unicon.net
 */
public class AssessmentMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 4l;

    // If the number of these strings grows, may look at implementing Enums of
    // Metadata Values and Metadata Keys
    // Currently we have metadataTypes which does not contain the values.

    private final String TRUE = "TRUE";
    private final String FALSE = "FALSE";
    private final String YES = "YES";
    private final String NO = "NO";

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
    private PrerequisiteMetadataDto preRequisite;
    private Double scaleAdditiveTerm;
    private Double scaleMultiplicativeTerm;
    @JacksonXmlElementWrapper(localName = "competencyMapDisciplines")
    @JacksonXmlProperty(localName = "competencyMapDiscipline")
    private List<String> competencyMapDisciplines;
    @JacksonXmlElementWrapper(localName = "sections")
    @JacksonXmlProperty(localName = "section")
    private List<SectionMetadataDto> section;
    private OverallPerformanceMetadataDto overallPerformance;
    private CompetencyPerformanceMetadataDto competencyPerformance;
    private String instructions;
    HashMap<String, MetadataType> sectionMetadata = null;
    private DeliveryTypeMetadataDto deliveryType;
    private String generateAssessmentPlacement;

    HashMap<String, String> sectionTypeMap = null;   // sectionMetadataDto.identifier --> sectionMetadataDto.type
    HashMap<String, String> sectionCompMapDiscMap = null;   // sectionMetadataDto.identifier --> sectionMetadataDto.competencyMapDiscipline
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

    public PrerequisiteMetadataDto getPreRequisite() {
        return preRequisite;
    }

    public void setPreRequisite(PrerequisiteMetadataDto preRequisite) {
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

    public List<SectionMetadataDto> getSection() {
        return section;
    }

    public void setSection(List<SectionMetadataDto> section) {
        this.section = section;
    }

    public OverallPerformanceMetadataDto getOverallPerformance() {
        return overallPerformance;
    }

    public void setOverallPerformance(OverallPerformanceMetadataDto overallPerformance) {
        this.overallPerformance = overallPerformance;
    }

    public CompetencyPerformanceMetadataDto getCompetencyPerformance() {
        return competencyPerformance;
    }

    public void setCompetencyPerformance(CompetencyPerformanceMetadataDto competencyPerformance) {
        this.competencyPerformance = competencyPerformance;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getGenerateAssessmentPlacement() {
        return generateAssessmentPlacement;
    }

    public boolean isGeneratePlacement() {
        if (TRUE.equalsIgnoreCase(getGenerateAssessmentPlacement()) || YES.equalsIgnoreCase(getGenerateAssessmentPlacement())) {
            return true;
        }

        return false;
    }

    public void setGenerateAssessmentPlacement(String generateAssessmentPlacement) {
        this.generateAssessmentPlacement = generateAssessmentPlacement;
    }

    public HashMap<String, MetadataType> getSectionMetadata() {
        // memento pattern
        return (sectionMetadata == null) ? computeSectionMetadata() : sectionMetadata;
    }

    public HashMap<String, MetadataType> computeSectionMetadata() {
        sectionMetadata = new HashMap<>();
        if (getType().equals(MetadataType.ASSESSMENTMETADATA.toString()) && getSection() != null) {
            for (SectionMetadataDto sectionMetadataDto : getSection()) {
                if ((sectionMetadataDto.getIdentifier() != null) && (sectionMetadataDto.getType() != null)) {
                    sectionMetadata.put(sectionMetadataDto.getIdentifier(),
                            MetadataType.getType(sectionMetadataDto.getType()));
                }
            }
        }
        return sectionMetadata;
    }

    public boolean isSectionItemBundle(String sectionIdentifier) {
        return isSectionType(sectionIdentifier, MetadataType.ITEMBUNDLE);
    }

    private boolean isSectionType(String sectionIdentifier, MetadataType expectedType) {
        HashMap<String, MetadataType> metadataSectionDetails = getSectionMetadata();
        if (metadataSectionDetails == null || metadataSectionDetails.isEmpty()
                || StringUtils.isEmpty(sectionIdentifier)) {
            return false;
        }
        if (expectedType.equals(metadataSectionDetails.get(sectionIdentifier))) {
            return true;
        }
        return false;
    }

    public boolean isRequirePasscode() {
        if (FALSE.equalsIgnoreCase(getRequirePasscode()) || NO.equalsIgnoreCase(getRequirePasscode())) {
            return false;
        } else if (TRUE.equalsIgnoreCase(getRequirePasscode()) || YES.equalsIgnoreCase(getRequirePasscode())) {
            return true;
        }

        return true;
    }
    /**
     * Currently this node, attribute is not used but has been put in place for future work.
     * If and when this function is used, we will need to confirm that the assessments in the system
     * have the expected availability.  Obviously null will return false and the assessment will not be available.
     */
    public boolean isAvailable(){
        if(YES.equalsIgnoreCase(getAvailable())){
            return true;
        } else {
            return false;
        }
    }

    public DeliveryTypeMetadataDto getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryTypeMetadataDto deliveryType) {
        this.deliveryType = deliveryType;
    }

    public boolean isPaper() {
        if (getDeliveryType() == null || FALSE.equalsIgnoreCase(getDeliveryType().getPaper())
                || NO.equalsIgnoreCase(getDeliveryType().getPaper())) {
            return false;
        } else if (TRUE.equalsIgnoreCase(getDeliveryType().getPaper())
                || YES.equalsIgnoreCase(getDeliveryType().getPaper())) {
            return true;
        }
        return false; // Default is false
    }

    public boolean isOnline() {
        if (getDeliveryType() == null || TRUE.equalsIgnoreCase(getDeliveryType().getOnline())
                || YES.equalsIgnoreCase(getDeliveryType().getOnline())) {
            return true;
        } else if (FALSE.equalsIgnoreCase(getDeliveryType().getOnline())
                || NO.equalsIgnoreCase(getDeliveryType().getOnline())) {
            return false;
        }
        return true; // Default is true
    }

    public String getSectionType(String identifier){
        if(sectionTypeMap == null){
            initSectionMaps();
        }

        return sectionTypeMap.get(identifier);
    }

    private void initSectionMaps(){
        sectionTypeMap = new HashMap<>();
        sectionCompMapDiscMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(getSection())) {
            for (SectionMetadataDto sectionMetadataDto: getSection()){

                if(StringUtils.isNotEmpty(sectionMetadataDto.getIdentifier()) && StringUtils.isNotEmpty(sectionMetadataDto.getType())){
                    sectionTypeMap.put(sectionMetadataDto.getIdentifier(), sectionMetadataDto.getType());
                }

                if(StringUtils.isNotEmpty(sectionMetadataDto.getIdentifier()) && StringUtils.isNotEmpty(sectionMetadataDto.getCompetencyMapDiscipline())){
                    sectionCompMapDiscMap.put(sectionMetadataDto.getIdentifier(), sectionMetadataDto.getCompetencyMapDiscipline());
                }
            }
        }
    }

    public String getSectionCompetencyMapDiscipline(String identifier){
        if(sectionCompMapDiscMap == null){
            initSectionMaps();
        }

        return sectionCompMapDiscMap.get(identifier);
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
