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
package org.cccnext.tesuto.reports.model;

import java.util.ArrayList;
import java.util.List;

public class StructureDescription {

    String structureType;
    String structureId;
    List<String> itemsInStructure;

    public String getStructureType() {
        return structureType;
    }

    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }

    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public List<String> getItemsInStructure() {
        if (itemsInStructure == null) {
            itemsInStructure = new ArrayList<String>();
        }
        return itemsInStructure;
    }

    public void setItemsInStructure(List<String> itemsInStructure) {
        this.itemsInStructure = itemsInStructure;
    }
}
