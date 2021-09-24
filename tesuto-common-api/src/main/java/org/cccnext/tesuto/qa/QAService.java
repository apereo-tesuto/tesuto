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
package org.cccnext.tesuto.qa;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface QAService<T> {

    default Set<T> getResources(Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader cl = this.getClass().getClassLoader(); 
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resourceResolver.getResources(getDirectoryPath() + "/*.json");
        Set<T> objects = new HashSet<>();
        for (Resource resource : resources) {
            T object = (T) mapper.readValue(resource.getInputStream(), objectClass);
            objects.add(object);
        }
        return objects;
    }
    
    String getDirectoryPath();

    public void setDefaults() throws IOException;

}
