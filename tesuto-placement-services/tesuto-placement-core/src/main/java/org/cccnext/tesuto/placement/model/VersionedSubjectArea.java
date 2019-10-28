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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema="public", name="versioned_subject_area")
@IdClass(VersionedSubjectAreaId.class)
public class VersionedSubjectArea {
  @Id
  @Column(name="college_discipline_id")
  private int disciplineId;

  @Id
  @Column(name="version")
  private int version;

  @Column(name="published")
  private boolean published;

  @Column(name="json")
  private String json;

  @ManyToOne
  @JoinColumn(name="college_discipline_id", insertable = false, updatable = false)
  private Discipline discipline;

  public int getDisciplineId() {
    return disciplineId;
  }

  public void setDisciplineId(int disciplineId) {
    this.disciplineId = disciplineId;
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

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  public Discipline getDiscipline() {
    return discipline;
  }

  public void setDiscipline(Discipline discipline) {
    this.discipline = discipline;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    VersionedSubjectArea that = (VersionedSubjectArea) o;

    return new EqualsBuilder()
      .append(disciplineId, that.disciplineId)
      .append(version, that.version)
      .append(published, that.published)
      .append(json, that.json)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(disciplineId)
      .append(version)
      .append(published)
      .append(json)
      .toHashCode();
  }

  @Override
  public String toString() {
    return "VersionedSubjectArea{" +
      "disciplineId=" + disciplineId +
      ", version=" + version +
      ", published=" + published +
      ", json='" + json + '\'' +
      '}';
  }
}
