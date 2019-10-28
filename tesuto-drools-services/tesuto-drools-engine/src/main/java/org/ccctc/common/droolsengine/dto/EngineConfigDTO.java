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
package org.ccctc.common.droolsengine.dto;

import java.util.List;

public class EngineConfigDTO {
    private List<String> enabledActionServices;
    private List<String> enabledPreProcessors;
    private String id;

    public List<String> getEnabledActionServices() {
        return enabledActionServices;
    }

    public List<String> getEnabledPreProcessors() {
        return enabledPreProcessors;
    }

    public String getId() {
        return id;
    }

    public void setEnabledActionServices(List<String> enabledActionServices) {
        this.enabledActionServices = enabledActionServices;
    }

    public void setEnabledPreProcessors(List<String> enabledPreProcessors) {
        this.enabledPreProcessors = enabledPreProcessors;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return "EngineConfigDTO.id=" + id + ":enabledFV=" + enabledPreProcessors + ":enabvledActions=" + enabledActionServices;

    }
}
