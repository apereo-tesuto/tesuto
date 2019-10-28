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

import javax.xml.ws.WebFault;

/**
 * Application level exception.<br>
 * All system level exceptions are caught, wrapped and re-thrown as an
 * application level exception.
 * 
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@WebFault(name = "AssessException", faultBean = "org.cccnext.tesuto.service.exception.TesutoExceptionBean")
public class TesutoException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * messageKey The error message.
     */
    private String messagekey;

    /**
     * Default Constructor
     * 
     */
    public TesutoException() {

    }

    /**
     * Taking exception as parameter.
     * 
     * @param exception
     */
    public TesutoException(Exception exception) {
        super(exception);
    }

    /**
     * Taking error key and exception as parameters.
     * 
     * @param key
     * @param exception
     */
    public TesutoException(String key, Exception exception) {
        super(exception);
        messagekey = key;
    }

    /**
     * Taking error code and error message as parameters.
     * 
     * @param key
     * @param msg
     */
    public TesutoException(String key, String msg) {
        super(msg);
        messagekey = key;
    }

    /**
     * Taking error code, error message an exception object as parameters.
     * 
     * @param key
     * @param msg
     * @param exception
     */
    public TesutoException(String key, String msg, Exception exception) {
        super(msg, exception);
        messagekey = key;
    }

    /**
     * Taking error message as parameter.
     * 
     * @param msg
     */
    public TesutoException(String msg) {
        super(msg);
        messagekey = msg;
    }

    /**
     * Returns the message key.
     * 
     * @return String messageKey
     */
    public String getMessageKey() {
        return messagekey;
    }
}
