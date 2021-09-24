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
package org.cccnext.tesuto.admin.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Entity
@Table(schema="public", name = "test_location_assessment")
@IdClass(TestLocationAssessmentId.class)
public class TestLocationAssessment implements Serializable {

    private static final long serialVersionUID = 3L;

    @Id
    @Column(name = "test_location_id", nullable = false)
    private String testLocationId;

    @Id
    @Column(name = "assessment_identifier", nullable = false)
    private String assessmentIdentifier;

    @Id
    @Column(name = "assessment_namespace", nullable = false)
    private String assessmentNamespace;


    public String getTestLocationId() {
        return testLocationId;
    }

    public void setTestLocationId(String testLocationId) {
        this.testLocationId = testLocationId;
    }

    public String getAssessmentIdentifier() {
        return assessmentIdentifier;
    }

    public void setAssessmentIdentifier(String assessmentIdentifier) {
        this.assessmentIdentifier = assessmentIdentifier;
    }

    @Override
    public String toString() {
        return "TestLocationAssessment{" +
                "testLocationId='" + testLocationId + '\'' +
                ", assessmentIdentifier='" + assessmentIdentifier + '\'' +
                ", assessmentNamespace='" + assessmentNamespace + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLocationAssessment that = (TestLocationAssessment) o;

        if (!testLocationId.equals(that.testLocationId)) return false;
        if (!assessmentIdentifier.equals(that.assessmentIdentifier)) return false;
        return assessmentNamespace.equals(that.assessmentNamespace);

    }

    @Override
    public int hashCode() {
        int result = testLocationId.hashCode();
        result = 31 * result + assessmentIdentifier.hashCode();
        result = 31 * result + assessmentNamespace.hashCode();
        return result;
    }

    public String getAssessmentNamespace() {
        return assessmentNamespace;
    }

    public void setAssessmentNamespace(String assessmentNamespace) {
        this.assessmentNamespace = assessmentNamespace;
    }
}
