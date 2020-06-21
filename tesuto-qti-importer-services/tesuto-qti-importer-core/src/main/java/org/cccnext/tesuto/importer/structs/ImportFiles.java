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
package org.cccnext.tesuto.importer.structs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.util.CollectionUtils;

public class ImportFiles {

    List<URI> tests = new ArrayList<URI>();
    List<URI> items = new ArrayList<URI>();
    List<URI> testsMetadata = new ArrayList<URI>();
    List<URI> itemsMetadata = new ArrayList<URI>();
    List<URI> competencyMaps = new ArrayList<URI>();
    List<URI> competencies = new ArrayList<URI>();

    public List<URI> getTests() {
        return tests;
    }

    public void addTests(List<URI> tests) {
        addAll(this.tests, tests);
    }

    public List<URI> getItems() {
        return items;
    }

    public void addItems(List<URI> items) {
        addAll(this.items, items);
    }

    public List<URI> getTestsMetadata() {
        return testsMetadata;
    }

    public void addTestsMetadata(List<URI> testsMetadata) {
        addAll(this.testsMetadata, testsMetadata);
    }

    public List<URI> getItemsMetadata() {
        return itemsMetadata;
    }

    public void addItemsMetadata(List<URI> itemsMetadata) {
        addAll(this.itemsMetadata, itemsMetadata);
    }

    public List<URI> getCompetencyMaps() {
        return competencyMaps;
    }

    public void addCompetencyMaps(List<URI> competencyMaps) {
        addAll(this.competencyMaps, competencyMaps);
    }

    public List<URI> getCompetencies() {
        return competencies;
    }

    public void addCompetencys(List<URI> competencys) {
        addAll(this.competencies, competencys);
    }

    private void addAll(List<URI> list, List<URI> listToAdd) {
        if (CollectionUtils.isNullOrEmpty(listToAdd)) {
            return;
        }
        list.addAll(listToAdd);
    }
}
