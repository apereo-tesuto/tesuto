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
package org.ccctc.common.droolscommon.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class RuleAttributesDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String family;
	private String engine;
	private String version;
	private String title;
	private String status;
	private String event;
	private String category;
	private String competencyMapDiscipline;
	private String description;

	public String getId() {
		return id;
	}

	public RuleAttributesDTO setId(String id) {
		this.id = id;
		return this;
	}

	public String getFamily() {
		return family;
	}

	public RuleAttributesDTO setFamily(String family) {
		this.family = family;
		return this;
	}

	public String getEngine() {
		return engine;
	}

	public RuleAttributesDTO setEngine(String engine) {
		this.engine = engine;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public RuleAttributesDTO setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public RuleAttributesDTO setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public RuleAttributesDTO setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getCompetencyMapDiscipline() {
		return competencyMapDiscipline;
	}

	public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
		this.competencyMapDiscipline = competencyMapDiscipline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public void adjustValuesForStorage() {

		id = StringUtils.isBlank(id) ? null : id;
		family = StringUtils.isBlank(family) ? null : family;
		engine = StringUtils.isBlank(engine) ? null : engine;
		version = StringUtils.isBlank(version) ? null : version;
		title = StringUtils.isBlank(title) ? null : title;
		status = StringUtils.isBlank(status) ? null : status;
		event = StringUtils.isBlank(event) ? null : event;
		category = StringUtils.isBlank(category) ? null : category;
		competencyMapDiscipline = StringUtils.isBlank(competencyMapDiscipline) ? null : competencyMapDiscipline;
		description = StringUtils.isBlank(description) ? null : description;
	}

	public void adjustValuesForUI() {
		id = id == null ? "" : id;
		family = family == null ? "" : family;
		engine = engine == null ? "" : engine;
		version = version == null ? "" : version;
		title = title == null ? "" : title;
		status = status == null ? "" : status;
		event = event == null ? "" : event;
		category = category == null ? "" : category;
		competencyMapDiscipline = competencyMapDiscipline == null ? "" : competencyMapDiscipline;
		description = description == null ? "" : description;
	}
}
