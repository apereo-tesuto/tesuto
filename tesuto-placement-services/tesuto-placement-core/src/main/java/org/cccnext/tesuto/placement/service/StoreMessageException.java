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
package org.cccnext.tesuto.placement.service;

/**
 * The StoreMessageException should be thrown if the message that was send should be stored
 * in a persistence store. The problem can be analyzed and the message be re-send at at later time.
 */
public class StoreMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StoreMessageException() {
        super();
    }

    public StoreMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreMessageException(String message) {
        super(message);
    }

    public StoreMessageException(Throwable cause) {
        super(cause);
    }

}
