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
package org.cccnext.tesuto.delivery.model.internal;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class ActivityLogData {
    private Integer w;
    private Integer h;
    private String uA;

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getuA() {
        return uA;
    }

    public void setuA(String uA) {
        this.uA = uA;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  w: ").append(w).append("\n");
        sb.append("  h: ").append(h).append("\n");
        sb.append("  uA: ").append(uA);
        return sb.toString();
    }
}
