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

import java.util.Date;
import java.util.Set;

public class VersionedSubjectAreaViewDto extends DisciplineViewDto {

	private static final long serialVersionUID = 2L;

	private Integer version;

	private boolean published;

	private Integer publishedVersion;

	private Date publishedDate;

	private String publishedTitle;

	// TODO: Consider removing the database trigger and doing a deep object comparison of the college_discipline entity bean.
	private boolean dirty;

	private Date lastEditedDate;

	private boolean archived; // Default false for now

	private Set<DisciplineSequenceViewDto> disciplineSequences;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public Set<DisciplineSequenceViewDto> getDisciplineSequences() {
		return disciplineSequences;
	}

	public void setDisciplineSequences(Set<DisciplineSequenceViewDto> disciplineSequences) {
		this.disciplineSequences = disciplineSequences;
	}
	public Integer getPublishedVersion() {
		return publishedVersion;
	}

	public void setPublishedVersion(Integer publishedVersion) {
		this.publishedVersion = publishedVersion;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public Date getLastEditedDate() {
		return lastEditedDate;
	}

	public void setLastEditedDate(Date lastEditedDate) {
		this.lastEditedDate = lastEditedDate;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "lastEditedDate", "publishedDate");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getPublishedTitle() {
		return publishedTitle;
	}

	public void setPublishedTitle(String publishedTitle) {
		this.publishedTitle = publishedTitle;
	}
}
