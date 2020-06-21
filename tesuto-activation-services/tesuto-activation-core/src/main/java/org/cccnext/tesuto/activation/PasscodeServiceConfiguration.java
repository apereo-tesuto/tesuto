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
package org.cccnext.tesuto.activation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by bruce on 1/28/16.
 */
public class PasscodeServiceConfiguration {
    private Integer minuteHand; // If non-null,passcodes only expire on this
                                // minute of the hour
    private Integer hourHand; // If non-null passcodes only expire on this hour
                              // of the day
    private int minExpiration; // minimum number of minutes before expiring. If
                               // privateMinuteHand is null, this is the
                               // expiration time
    private int passcodeLength = 6; // does NOT include the prefix
    private String prefix = "";

    public Integer getMinuteHand() {
        return minuteHand;
    }

    public void setMinuteHand(Integer minuteHand) {
        this.minuteHand = minuteHand;
    }

    public int getMinExpiration() {
        return minExpiration;
    }

    public Integer getHourHand() {
        return hourHand;
    }

    public void setHourHand(Integer hourHand) {
        this.hourHand = hourHand;
    }

    public void setMinExpiration(int minExpiration) {
        this.minExpiration = minExpiration;
    }

    public int getPasscodeLength() {
        return passcodeLength;
    }

    public void setPasscodeLength(int passcodeLength) {
        this.passcodeLength = passcodeLength;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
