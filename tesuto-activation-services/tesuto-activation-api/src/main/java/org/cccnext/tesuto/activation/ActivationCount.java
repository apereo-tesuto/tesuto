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
package org.cccnext.tesuto.activation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.Activation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivationCount implements Comparable<ActivationCount> {

    private String locationId;
    private String assessment;
    private String proctor;
    private Map<Activation.Status, Integer> counts = new HashMap<>();

    // compare strings, with null > any other string
    private static int compareStrings(String string1, String string2) {
        if (string1 == null) {
            return (string2 == null ? 0 : 1);
        } else if (string2 == null) {
            return -1;
        } else {
            return string1.compareTo(string2);
        }
    }

    // This constructor is useful for JPQL queries
    public ActivationCount(String locationId, String assessment, String proctor, Activation.Status status, int count) {
        this.locationId = locationId;
        this.assessment = assessment;
        this.proctor = proctor;
        counts.put(status, count);
    }

    public ActivationCount(String locationId, String assessment, String proctor, Activation.Status status, long count) {
        this(locationId, assessment, proctor, status, (int) count);
    }

    // This constructor builds "keys" used for grouping related counts
    public ActivationCount(String locationId, String assessment, String proctor) {
        this.locationId = locationId;
        this.assessment = assessment;
        this.proctor = proctor;
    }

    @Override
    public int compareTo(ActivationCount other) {
        int comparison = compareStrings(String.valueOf(this.locationId), String.valueOf(other.locationId));
        if (comparison != 0) {
            return comparison;
        }
        comparison = compareStrings(this.getAssessment(), other.getAssessment());
        if (comparison != 0) {
            return comparison;
        }
        return compareStrings(this.getProctor(), other.getProctor());
    }

    public int getCount(Activation.Status status) {
        Integer count = counts.get(status);
        return (count == null ? 0 : count);
    }

    public void addCount(Activation.Status status, int count) {
        Integer originalCount = counts.get(status);
        if (originalCount == null)
            originalCount = 0;
        counts.put(status, originalCount + count);
    }

    public ActivationCount merge(ActivationCount other) {
        other.getCounts().forEach((status, count) -> this.addCount(status, count));
        return this;
    }

    public ActivationCount merge(Collection<ActivationCount> counts) {
        counts.forEach(count -> this.merge(count));
        return this;
    }

    // Used for grouping when we get the count for each status in a different
    // object
    public ActivationCount getKey() {
        return new ActivationCount(locationId, assessment, proctor);
    }


}
