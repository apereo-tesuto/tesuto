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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Entity
@Table(schema="public",name = "mmap_equivalent")
public class MMAPEquivalent implements Serializable, Comparable<MMAPEquivalent> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="mmap_equivalent_code")
    String code;

    @Column(name="competency_map_discipline")
    String competencyMapDiscipline;

    @Column(name="mmap_equivalent")
    String mmapEquivalent;

    @Column(name="mmap_equivalent_order")
    int order;

    @Override
    public int compareTo(MMAPEquivalent o) {
        return this.order - o.getOrder();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline;
    }

    public String getMmapEquivalent() {
        return mmapEquivalent;
    }

    public void setMmapEquivalent(String mmapEquivalent) {
        this.mmapEquivalent = mmapEquivalent;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MMAPEquivalent that = (MMAPEquivalent) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "MMAPEquivalent{" +
                "code='" + code + '\'' +
                ", competencyMapDiscipline='" + competencyMapDiscipline + '\'' +
                ", mmapEquivalent='" + mmapEquivalent + '\'' +
                ", order=" + order +
                '}';
    }
}
