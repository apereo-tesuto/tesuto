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
package org.cccnext.tesuto.placement.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ENGLISH")
@Audited
@AuditTable(schema="public", value ="history_competency_attributes")
public class EnglishCompetencyAttributes extends EslCompetencyAttributes {

	private static final long serialVersionUID = 1L;

	@Transient
	@Override
	public CompetencyAttributeEnum getCompetencyCode() {
		return CompetencyAttributeEnum.ENGLISH;
	}

}
