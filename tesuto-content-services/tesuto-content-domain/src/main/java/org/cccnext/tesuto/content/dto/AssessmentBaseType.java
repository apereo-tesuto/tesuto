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

;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public enum AssessmentBaseType {
    IDENTIFIER("identifier"), BOOLEAN("boolean"), INTEGER("integer"), FLOAT("float"), STRING("string"), POINT(
            "point"), PAIR("pair"), DIRECTED_PAIR("directedPair"), DURATION("duration"), FILE("file"), URI("uri");

    private final String value;

    AssessmentBaseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
