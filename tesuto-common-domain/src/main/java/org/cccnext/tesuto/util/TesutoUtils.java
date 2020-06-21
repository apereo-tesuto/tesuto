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
package org.cccnext.tesuto.util;

import java.util.UUID;

public class TesutoUtils {

    static public String newId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Mongo does not allow $ or . periods to be part of strings in a map key
     * @see <a href=https://docs.mongodb.com/manual/reference/limits/#Restrictions-on-Field-Names>Mongo Restrictions</a>
     * Use this method to encode before persisting.
     * @param key
     * @return
     */
    static public String encodeKey(String key){
        return key.replace("$", "\\u0024").replace(".", "\\u002e");
    }

    /**
     * Mongo does not allow $ or . periods to be part of strings in a map key
     * @see <a href=https://docs.mongodb.com/manual/reference/limits/#Restrictions-on-Field-Names>Mongo Restrictions</a>
     * Use this method to decode after retrieving from document
     * @param key
     * @return
     */
    static public String decodeKey(String key){
        return key.replace("\\u002e", ".").replace("\\u0024", "$");
    }
}
