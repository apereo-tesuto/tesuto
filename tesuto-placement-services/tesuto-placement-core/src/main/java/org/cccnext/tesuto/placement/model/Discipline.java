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
package org.cccnext.tesuto.placement.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.cccnext.tesuto.domain.model.AbstractEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Set;

@Entity
@Audited
@AuditTable(schema="public", value ="history_college_discipline")
@Table(schema="public",name = "college_discipline")
public class Discipline extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name="college_discipline_id")
	private Integer disciplineId;

	@Column(name="college_id")
	private String collegeId;

	@Column(name="title")
	private String title;

	@Column(name="description")
	private String description;

	@Column(name="competency_map_discipline")
	private String competencyMapDiscipline;

	@Column(name="competency_map_version")
	private Integer competencyMapVersion;

	@Column(name="sis_code")
	private String sisCode;

	@Column(name="no_placement_message")
	private String noPlacementMessage;

	@Column(name="use_prereq_placement_method")
	private boolean usePrereqPlacementMethod = true;

	@Column(name = "is_dirty")
	private boolean isDirty;

	@Column(name="max_subject_area_version")
  private Integer maxSubjectAreaVersion;

	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name="competency_attribute_id")
	private CompetencyAttributes competencyAttributes;

	@OneToMany(mappedBy="discipline", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@NotAudited
	private Set<DisciplineSequence> disciplineSequences;

	@OneToOne
  @JoinColumns({
    @JoinColumn(name = "college_discipline_id", insertable = false, updatable = false),
    @JoinColumn(name = "max_subject_area_version", insertable = false, updatable = false)
  })
  @NotAudited
  private VersionedSubjectArea publishedSubjectAreaVersion;

	public Integer getDisciplineId() {
		return disciplineId;
	}

	public void setDisciplineId(Integer disciplineId) {
		this.disciplineId = disciplineId;
	}

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompetencyMapDiscipline() {
		return competencyMapDiscipline;
	}

	public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
		this.competencyMapDiscipline = competencyMapDiscipline;
	}

	public String getSisCode() {
		return sisCode;
	}

	public void setSisCode(String sisCode) {
		this.sisCode = sisCode;
	}

	public CompetencyAttributes getCompetencyAttributes() {
		return competencyAttributes;
	}

	public void setCompetencyAttributes(CompetencyAttributes subjectAreaAttributes) {
		this.competencyAttributes = subjectAreaAttributes;
	}

	public Set<DisciplineSequence> getDisciplineSequences() {
		return disciplineSequences;
	}

	public void setDisciplineSequences(Set<DisciplineSequence> disciplineSequences) {
		this.disciplineSequences = disciplineSequences;
	}

	public Integer getCompetencyMapVersion() {
		return competencyMapVersion;
	}

	public void setCompetencyMapVersion(Integer competencyMapVersion) {
		this.competencyMapVersion = competencyMapVersion;
	}

	public String getNoPlacementMessage() {
		return noPlacementMessage;
	}

	public void setNoPlacementMessage(String noPlacementMessage) {
		this.noPlacementMessage = noPlacementMessage;
	}

	public boolean isUsePrereqPlacementMethod() {
		return usePrereqPlacementMethod;
	}

	public void setUsePrereqPlacementMethod(boolean usePrereqPlacementMethod) {
		this.usePrereqPlacementMethod = usePrereqPlacementMethod;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean dirty) {
		isDirty = dirty;
	}

  public Integer getMaxSubjectAreaVersion() {
    return maxSubjectAreaVersion;
  }

  public void setMaxSubjectAreaVersion(Integer maxSubjectAreaVersion) {
    this.maxSubjectAreaVersion = maxSubjectAreaVersion;
  }

  public VersionedSubjectArea getPublishedSubjectAreaVersion() {
    return publishedSubjectAreaVersion;
  }

  public void setPublishedSubjectAreaVersion(VersionedSubjectArea publishedSubjectAreaVersion) {
    this.publishedSubjectAreaVersion = publishedSubjectAreaVersion;
  }

  @Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "disciplineSequences", "createdOnDate", "lastUpdatedDate","publishedSubjectAreaVersion");

	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[]{"disciplineSequences", "createdOnDate", "lastUpdatedDate", "publishedSubjectAreaVersion"});
	}

  @Override
  public String toString() {
    return "Discipline{" +
      "disciplineId=" + disciplineId +
      ", collegeId='" + collegeId + '\'' +
      ", title='" + title + '\'' +
      ", description='" + description + '\'' +
      ", competencyMapDiscipline='" + competencyMapDiscipline + '\'' +
      ", competencyMapVersion=" + competencyMapVersion +
      ", sisCode='" + sisCode + '\'' +
      ", noPlacementMessage='" + noPlacementMessage + '\'' +
      ", usePrereqPlacementMethod=" + usePrereqPlacementMethod +
      ", isDirty=" + isDirty +
      ", maxSubjectAreaVersion=" + maxSubjectAreaVersion +
      '}';
  }
}
