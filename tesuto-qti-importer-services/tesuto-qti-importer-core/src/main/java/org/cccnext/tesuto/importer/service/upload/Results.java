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
package org.cccnext.tesuto.importer.service.upload;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason Brown jbrown@unicon.net on 1/3/17.
 */
public abstract class Results {

    private List<ValidationMessage> validationErrors;
    private List<ValidationMessage> validationWarnings;

    public List<ValidationMessage> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationMessage> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void addValidationErrors(List<ValidationMessage> validationMessages){
        if(this.validationErrors == null){
            setValidationErrors(validationMessages);
        }else {
            Set<ValidationMessage> set = new LinkedHashSet<>(validationErrors);
            set.addAll(validationMessages);
            this.validationErrors = new ArrayList<>(set);
        }
    }

    public boolean hasValidationErrors(){
        return CollectionUtils.isNotEmpty(getValidationErrors());
    }

    public List<ValidationMessage> getValidationWarnings() {
        return validationWarnings;
    }

    public void setValidationWarnings(List<ValidationMessage> validationWarnings) {
        this.validationWarnings = validationWarnings;
    }

    public void addValidationWarnings(List<ValidationMessage> validationMessages){
        if(this.validationWarnings == null){
            setValidationWarnings(validationMessages);
        }else {
            Set<ValidationMessage> set = new LinkedHashSet<>(validationWarnings);
            set.addAll(validationMessages);
            this.validationWarnings = new ArrayList<>(set);
        }
    }

    public boolean hasValidationWarnings() {
        return CollectionUtils.isNotEmpty(getValidationWarnings());
    }

}
