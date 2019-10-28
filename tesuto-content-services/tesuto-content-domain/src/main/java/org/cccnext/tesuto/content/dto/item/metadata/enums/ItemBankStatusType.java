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
package org.cccnext.tesuto.content.dto.item.metadata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Jason Brown jbrown@unicon.net on 8/18/16.
 */
public enum ItemBankStatusType {

    AVAILABLE("available"),
    DO_NOT_USE("do not use"),
    REVIEW("review"),
    GET_MORE_DATA("get more data");

    private final String key;

    ItemBankStatusType(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    /**
     * Will remove whitespace of key before checking equality to mitigate user input errors.
     * @param key
     * @return ItemBankStatusType
     */
    @JsonCreator
    public static ItemBankStatusType getType(String key) {
        for (ItemBankStatusType type : ItemBankStatusType.values()) {
            if (type.toString().replaceAll("\\s", "").equalsIgnoreCase(key.replaceAll("\\s", ""))) {
                return type;
            }
        }
        return null;
    }
}
