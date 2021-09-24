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
package org.ccctc.droolseditor.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public class StringToSetConverter implements Converter<String, Set<String>> {

    private static final long serialVersionUID = 1L;

    @Override
    public Result<Set<String>> convertToModel(String value, ValueContext context) {
        if(StringUtils.isBlank(value)) {
            return Result.ok(new HashSet<String>());
        }
        value = StringUtils.deleteWhitespace(value);
        return Result.ok(new HashSet(Arrays.asList(value.split(","))));
    }

    @Override
    public String convertToPresentation(Set<String> value, ValueContext context) {
        if(value == null || value.isEmpty()) {
            return " ";
        }
        return  StringUtils.deleteWhitespace(StringUtils.join(value, ","));
    }

}
