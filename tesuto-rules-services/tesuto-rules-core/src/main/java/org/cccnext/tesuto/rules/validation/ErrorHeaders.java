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
package org.cccnext.tesuto.rules.validation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;

public interface ErrorHeaders {

    public static List<String> getDefaultParameterNames(Collection<Fact> factNames)
    {
        return factNames.stream().map(f -> f.getName()).collect(Collectors.toList());
    }
    
    public static String actionHeaders(List<String> factNames) {
        StringBuilder headers = new StringBuilder();
        factNames.forEach(f -> headers.append(",").append(f));
        return "\nAction Errors\ntest number,type,expected,actual" + headers.toString() + "\n";
    }
    
    public String getDataSource();
    public String getDataType();
    public List<String> getParameterNames();
    
}
