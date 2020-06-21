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
package org.cccnext.tesuto.service.importer.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 12/22/16.
 */
public class ValidatedNode<T> {

    List<ValidationMessage> errors = new ArrayList<>();
    List<ValidationMessage> warnings = new ArrayList<>();
    T value;

    public ValidatedNode(List<ValidationMessage> errors, T value) {
        this.errors = errors;
        this.value = value;
    }

    public ValidatedNode(List<ValidationMessage> errors, List<ValidationMessage> warnings, T value) {
        this.errors = errors;
        this.warnings = warnings;
        this.value = value;
    }

    public ValidatedNode(T value){
        this.value = value;
    }

    public List<ValidationMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationMessage> errors) {
        this.errors = errors;
    }

    public List<ValidationMessage> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationMessage> warnings) {
        this.warnings = warnings;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
