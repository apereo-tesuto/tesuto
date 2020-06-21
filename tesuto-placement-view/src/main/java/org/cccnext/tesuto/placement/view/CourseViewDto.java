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

import java.io.Serializable;
import java.util.Set;

public class CourseViewDto  implements Serializable {
	static final long serialVersionUID= 2L;

	private Integer courseId;
	private String name;
	private String subject;
	private String number;
	private String cid;
	private String description;
	private char cb21Code;
	private int courseGroup;
	private String mmapEquivalentCode;
	private String sisTestCode;
	private String competencyGroupLogic;
	private int disciplineId;
	private Integer auditId;

	private Set<CompetencyGroupViewDto> competencyGroups;

	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCompetencyGroupLogic() {
		return competencyGroupLogic;
	}

	public void setCompetencyGroupLogic(String competencyGroupLogic) {
		this.competencyGroupLogic = competencyGroupLogic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDisciplineId() {
		return disciplineId;
	}

	public void setDisciplineId(int disciplineId) {
		this.disciplineId = disciplineId;
	}

	public char getCb21Code() {
		return cb21Code;
	}

	public void setCb21Code(char cb21) {
		this.cb21Code = cb21;
	}

	public int getCourseGroup() {
		return courseGroup;
	}

	public void setCourseGroup(int courseGroup) {
		this.courseGroup = courseGroup;
	}

	public String getMmapEquivalentCode() {
		return mmapEquivalentCode;
	}

	public void setMmapEquivalentCode(String mmapEquivalentCode) {
		this.mmapEquivalentCode = mmapEquivalentCode;
	}

	public String getSisTestCode() {
		return sisTestCode;
	}

	public void setSisTestCode(String sisTestCode) {
		this.sisTestCode = sisTestCode;
	}

	public Integer getAuditId() {
		return auditId;
	}

	public void setAuditId(Integer auditId) {
		this.auditId = auditId;
	}

	public Set<CompetencyGroupViewDto> getCompetencyGroups() {
		return competencyGroups;
	}

	public void setCompetencyGroups(Set<CompetencyGroupViewDto> competencyGroups) {
		this.competencyGroups = competencyGroups;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CourseViewDto)) return false;

		CourseViewDto that = (CourseViewDto) o;

		if (getDisciplineId() != that.getDisciplineId()) return false;
		if (getCb21Code() != that.getCb21Code()) return false;
		if (getCourseId() != null ? !getCourseId().equals(that.getCourseId()) : that.getCourseId() != null)
			return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
		if (getCid() != null ? !getCid().equals(that.getCid()) : that.getCid() != null) return false;
		if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
		if (getNumber() != null ? !getNumber().equals(that.getNumber()) : that.getNumber() != null) return false;
		if (getCompetencyGroupLogic() != null ? !getCompetencyGroupLogic().equals(that.getCompetencyGroupLogic()) : that.getCompetencyGroupLogic() != null) return false;
		return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;

	}

	@Override
	public int hashCode() {
		int result = getCourseId() != null ? getCourseId().hashCode() : 0;
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		result = 31 * result + (getCid() != null ? getCid().hashCode() : 0);
		result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
		result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
		result = 31 * result + (getNumber() != null ? getNumber().hashCode() : 0);
		result = 31 * result + (getCompetencyGroupLogic() != null ? getCompetencyGroupLogic().hashCode() : 0);
		result = 31 * result + getDisciplineId();
		result = 31 * result + getCb21Code();
		return result;
	}

	@Override
	public String toString() {
		return "CourseViewDto{" +
				"courseId=" + courseId +
				", name='" + name + '\'' +
				", cid='" + cid + '\'' +
				", subject='" + subject + '\'' +
				", number='" + number + '\'' +
				", description='" + description + '\'' +
				", competencyGroupLogic='" + competencyGroupLogic + '\'' +
				", disciplineId=" + disciplineId +
				", cb21=" + cb21Code +
				'}';
	}

}
