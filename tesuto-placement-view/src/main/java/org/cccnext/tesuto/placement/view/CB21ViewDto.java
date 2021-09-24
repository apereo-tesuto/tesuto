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
package org.cccnext.tesuto.placement.view;

import java.io.Serializable;

public class CB21ViewDto implements Serializable {

    static final long serialVersionUID=2l;

    private char cb21Code;
    private int level;

    public char getCb21Code() {
        return cb21Code;
    }
    public void setCb21Code(char cb21Code) {
        this.cb21Code = cb21Code;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CB21ViewDto)) return false;

        CB21ViewDto that = (CB21ViewDto) o;

        if (getCb21Code() != that.getCb21Code()) return false;
        return getLevel() == that.getLevel();

    }

    @Override
    public int hashCode() {
        int result = (int) getCb21Code();
        result = 31 * result + (int) getLevel();
        return result;
    }

    @Override
    public String toString() {
        return "CB21ViewDto{" +
                "cb21Code=" + cb21Code +
                ", level=" + level +
                '}';
    }
}
