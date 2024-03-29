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
package org.cccnext.tesuto.content.dto.enums;

public enum MetadataType {

    ASSESSMENTMETADATA("assessmentmetadata"), ITEMMETADATA("itemmetadata"), SECTIONS("sections"), SECTION(
            "section"), IDENTIFIER("identifier"), TYPE("type"), TESTLET("testlet"), ITEMBUNDLE("itemBundle"),
            ENTRYTESTLET("entry-testlet");

    private final String key;

    MetadataType(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    public static MetadataType getType(String key) {
        for (MetadataType type : MetadataType.values()) {
            if (type.toString().equals(key)) {
                return type;
            }
        }
        return null;
    }
}
