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

public enum MmapDataSourceType {
	SELF_REPORTED("Self Reported"),
    VERIFIED("Verified"),
    STANDARD("Standard"), // temporary type to represent "not self reported"
	UNKNOWN("unknown");

    private final String value;

    MmapDataSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    static public MmapDataSourceType parseEnum(String value) {
        switch( value) {
            case "Self Reported":
                return SELF_REPORTED;
            case "Verified":
                return VERIFIED;
            case "Standard":
                return STANDARD; 
            case "SELF_REPORTED":
                return SELF_REPORTED;
            case "VERIFIED":
                return VERIFIED;
            case "STANDARD":
                return STANDARD;
        }
        return MmapDataSourceType.UNKNOWN;
    }
    
    
}
