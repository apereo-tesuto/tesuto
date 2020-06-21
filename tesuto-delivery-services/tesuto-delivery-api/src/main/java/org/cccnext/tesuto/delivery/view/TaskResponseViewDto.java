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
package org.cccnext.tesuto.delivery.view;

import java.util.List;

/**
 * Created by bruce on 11/19/15.
 */
public class TaskResponseViewDto {

    private Long duration;
    private List<ItemSessionResponseViewDto> responses;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public List<ItemSessionResponseViewDto> getResponses() {
        return responses;
    }

    public void setResponses(List<ItemSessionResponseViewDto> responses) {
        this.responses = responses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskResponseViewDto))
            return false;

        TaskResponseViewDto that = (TaskResponseViewDto) o;

        if (getDuration() != that.getDuration())
            return false;
        return !(getResponses() != null ? !getResponses().equals(that.getResponses()) : that.getResponses() != null);
    }

    @Override
    public int hashCode() {
        int result = (int) (getDuration() ^ (getDuration() >>> 32));
        result = 31 * result + (getResponses() != null ? getResponses().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskResponseViewDto{" + "duration=" + duration + ", responses=" + responses + '}';
    }
}
