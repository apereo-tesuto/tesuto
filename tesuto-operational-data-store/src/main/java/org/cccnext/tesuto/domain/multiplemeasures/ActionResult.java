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
package org.cccnext.tesuto.domain.multiplemeasures;

import java.io.Serializable;

public class ActionResult  implements Serializable {

	private String cccid;
	private String collegeId;
	private String trackingId;
	private String ruleSetId;
	private String ruleSetRowId;
	private String ruleId;

	public String getCccid() {
		return cccid;
	}

	public void setCccid(String cccid) {
		this.cccid = cccid;
	}

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String miscode) {
		this.collegeId = miscode;
	}
	public String getRuleSetId() {
		return ruleSetId;
	}
	public void setRuleSetId(String ruleSetId) {
		this.ruleSetId = ruleSetId;
	}
	
	public String getRuleSetRowId() {
		return ruleSetRowId;
	}
	public void setRuleSetRowId(String ruleSetRowId) {
		this.ruleSetRowId = ruleSetRowId;
	}
	public String getTrackingId() {
        return trackingId;
    }
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
}
