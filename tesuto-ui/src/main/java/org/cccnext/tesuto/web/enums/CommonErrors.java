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
package org.cccnext.tesuto.web.enums;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public enum CommonErrors {
    TIMED_OUT("Your login has timed out."),
    UNAUTHORIZED("You are unauthorized to do that."),
    DISABLED("Your user account is disabled."),
    NOT_FOUND("No account was found for that username."),
    NO_COLLEGES("Your account is not associated with any colleges."),
    LOCKED("Your account is locked."),
    EXPIRED("Your account has expired."),
    NO_CCCID_EPPN("Your account has no CCC ID or EPPN."),
    INVALID_AFFILIATIONS("No valid affiliations for this user."),
    INVALID_REQUEST("The format of the request was invalid.");

    private final String error;

    CommonErrors(final String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return error;
    }
}
