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
package org.ccctc.common.droolscommon.action.result;

public class ErrorActionResult extends ActionResult {
    private static final long serialVersionUID = 1L;
    private Throwable error;
    
    public ErrorActionResult(String name, String message) {
        super(name, message);
    }

    public ErrorActionResult(Throwable err, String name, String message) {
        super(name, message);
        this.error = err;
    }
    
    @Override
    public String toString() {
        return "Unable to complete action [" + actionName + "] successfully due to: " + error == null ? "" : error.getClass() + "/n" + message;
    }
}
