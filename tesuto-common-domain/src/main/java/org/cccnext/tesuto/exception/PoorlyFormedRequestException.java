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
package org.cccnext.tesuto.exception;

/**
 * Created by jstanley on 12/14/15.
 *
 * A base class for exceptions that should trigger a 500 from a controller based
 * on service failure due to improperly formed request/SQL/URI/JQUERY etc
 */
public class PoorlyFormedRequestException extends RuntimeException {

    public PoorlyFormedRequestException(String message) {
        super(message);
    }

    public PoorlyFormedRequestException(String message, Exception exp) {
        super(message, exp);
    }
}
