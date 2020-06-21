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
package org.cccnext.tesuto.domain.dto;

public class ScopedIdentifierDto implements Dto {

	private static final long serialVersionUID = 2920406022487347206L;

	private String namespace;
	private String identifier;

	public ScopedIdentifierDto() {

	}

	public ScopedIdentifierDto(String namespace, String identifer) {
		this.namespace = namespace;
		this.identifier = identifer;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ScopedIdentifierDto that = (ScopedIdentifierDto) o;

		if (!identifier.equals(that.identifier))
			return false;
		return namespace.equals(that.namespace);

	}

	@Override
	public int hashCode() {
		int result = 31 * identifier.hashCode();
		result = 31 * result + namespace.hashCode();
		return result;
	}
}
