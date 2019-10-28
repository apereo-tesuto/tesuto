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

import java.util.stream.Stream;

import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Created by bruce on 1/21/16. Assessment, AssessmentPart, Section, or ItemRef.
 * This interface is used for tree walking.
 */

@JsonTypeInfo(use = Id.CLASS,
              include = JsonTypeInfo.As.PROPERTY,
              property = "type")
@JsonSubTypes({
    @Type(value = AssessmentDto.class),
    @Type(value = AssessmentItemRefDto.class),
    @Type(value = AssessmentPartDto.class),
    @Type(value = AssessmentSectionDto.class)
    })
public interface AssessmentComponentDto {
    String getId();

    Stream<? extends AssessmentComponentDto> getChildren();

    AssessmentItemSessionControlDto getItemSessionControl();

    // This method is just useful for testing, and is only here because Groovy
    // 2.x doesn't deal well with streams
    default boolean isParentOf(String id) {
        return getChildren().anyMatch(child -> child.getId().equals(id));
    }

    @JsonIgnore
    default Stream<? extends AssessmentComponentDto> getDescendants() {
        Stream<? extends AssessmentComponentDto> descendants = getChildren().flatMap(child -> child.getDescendants());
        return Stream.concat(Stream.of(this), descendants);
    }
}
