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
package org.cccnext.tesuto.content.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Josh Corbin <jcorbin@unicon.net>
 */
public class AssessmentDto implements AbstractAssessmentDto, AssessmentComponentDto {

	private static final long serialVersionUID = 1l;  // Not sure if this is needed for Assessment?

	private String id;
	private String namespace;
	private String identifier;
	private int version;
	private boolean published;
	private List<String> resources;
	private String stylesheets;
	private String title;
	private String language;		// Not in QTI, but will need to populate this from metadata 
	private String toolName;
	private String toolVersion;
	private Double duration;
	private List<AssessmentOutcomeDeclarationDto> outcomeDeclarations;
	private List<AssessmentPartDto> assessmentParts;
	private AssessmentOutcomeProcessingDto outcomeProcessing;
	private AssessmentMetadataDto assessmentMetadata;
	// TODO: feedback

	private Map<String, AssessmentComponentDto> componentsById = null;
	private Map<String, AssessmentComponentDto> parentsById = null;

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}

	public List<AssessmentOutcomeDeclarationDto> getOutcomeDeclarations() {
		return outcomeDeclarations;
	}

	public void setOutcomeDeclarations(List<AssessmentOutcomeDeclarationDto> outcomeDeclarations) {
		this.outcomeDeclarations = outcomeDeclarations;
	}

	public List<AssessmentPartDto> getAssessmentParts() {
		return assessmentParts;
	}

	public void setAssessmentParts(List<AssessmentPartDto> assessmentParts) {
		this.assessmentParts = assessmentParts;
	}

	public AssessmentOutcomeProcessingDto getOutcomeProcessing() {
		return outcomeProcessing;
	}

	public void setOutcomeProcessing(AssessmentOutcomeProcessingDto outcomeProcessingDto) {
		this.outcomeProcessing = outcomeProcessingDto;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getStylesheets() {
		return stylesheets;
	}

	public void setStylesheets(String stylesheets) {
		this.stylesheets = stylesheets;
	}

	public AssessmentMetadataDto getAssessmentMetadata() {
		return assessmentMetadata;
	}

	public void setAssessmentMetadata(AssessmentMetadataDto assessmentMetadata) {
		this.assessmentMetadata = assessmentMetadata;
	}

	public boolean isPaper() {
		if(getAssessmentMetadata() != null){
			return getAssessmentMetadata().isPaper();
		}else{
			return false; //default false
		}
	}

	public boolean isOnline() {
		if(getAssessmentMetadata() != null){
			return getAssessmentMetadata().isOnline();
		}else{
			return true; //default true
		}
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public ScopedIdentifier getScopedIdentifier() {
		return new ScopedIdentifier(namespace, identifier);
	}

	public Stream<? extends AssessmentComponentDto> getChildren() {
		return getAssessmentParts().stream();
	}

	public List<AssessmentComponentDto> getAncestors(String identifier) {
		Optional parent;
		List<AssessmentComponentDto> ancestors = new ArrayList<AssessmentComponentDto>();

		do {
			parent = getParent(identifier);
			if (parent.isPresent()) {
				AssessmentComponentDto assessmentComponentDto = (AssessmentComponentDto) parent.get();
				ancestors.add(assessmentComponentDto);
				identifier = assessmentComponentDto.getId();
			}
		} while (parent.isPresent());

		return ancestors;
	}

	public Optional<AssessmentComponentDto> getComponent(String id) {
		if (componentsById == null) {
			initializeComponentMaps();
		}
		return Optional.ofNullable(componentsById.get(id));
	}

	public Optional<AssessmentComponentDto> getParent(String id) {
		if (parentsById == null) {
			initializeComponentMaps();
		}
		return Optional.ofNullable(parentsById.get(id));
	}

	private void initializeComponentMaps() {
		componentsById = new HashMap<String,AssessmentComponentDto>();
		parentsById = new HashMap<String,AssessmentComponentDto>();
		getChildren().forEach(child -> initializeFromComponent(child));
	}

	private void initializeFromComponent(AssessmentComponentDto component) {
		componentsById.put(component.getId(), component);
		component.getChildren().forEach(child -> {
			parentsById.put(child.getId(), component);
			initializeFromComponent(child);
		});
	}


	public AssessmentItemSessionControlDto getItemSessionControl() {
		return null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String scopedId() {
        return getScopedIdentifier().scopedTag() + ":" + version;
    }
    
    public String scopedId(String seperator) {
        return getScopedIdentifier().scopedTag(seperator) + seperator + "v" + version;
    }

}








	

