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


import java.io.Serializable;

public class DisciplineSequenceId implements Serializable {
    final static long serialVersionUID = 2l;

    private int disciplineId;
    private char cb21Code;
    private int courseGroup;

    public DisciplineSequenceId() {}

    public DisciplineSequenceId(int disciplineId, char cb21Code, int courseGroup) {
        this.disciplineId = disciplineId;
        this.cb21Code = cb21Code;
        this.courseGroup = courseGroup;
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

    public void setCb21Code(char cb21Code) {
        this.cb21Code = cb21Code;
    }

    public int getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(int courseGroup) {
        this.courseGroup = courseGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisciplineSequenceId that = (DisciplineSequenceId) o;

        if (disciplineId != that.disciplineId) return false;
        if (cb21Code != that.cb21Code) return false;
        return courseGroup == that.courseGroup;
    }

    @Override
    public int hashCode() {
        int result = disciplineId;
        result = 31 * result + (int) cb21Code;
        result = 31 * result + courseGroup;
        return result;
    }

    @Override
    public String toString() {
        return "DisciplineSequenceId{" +
                "disciplineId=" + disciplineId +
                ", cb21Code=" + cb21Code +
                ", courseGroup=" + courseGroup +
                '}';
    }
}
