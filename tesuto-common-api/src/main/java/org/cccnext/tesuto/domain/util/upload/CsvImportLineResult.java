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
package org.cccnext.tesuto.domain.util.upload;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CsvImportLineResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public CsvImportLineResult() {
	}

	public CsvImportLineResult(T importedValue) {
		this.importedValue = importedValue;
	}

	public CsvImportLineResult(int lineNumber, Collection<CsvImportError> errors) {
		this.errors = errors;
		this.lineNumber = lineNumber;
	}

	public CsvImportLineResult(Collection<CsvImportError> errors) {
		this.errors = errors;
	}

	private Integer lineNumber;
	private Collection<CsvImportError> errors;
	private T importedValue;

	public Integer getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Collection<CsvImportError> getErrors() {
		return errors;
	}

	public void setErrors(Collection<CsvImportError> errors) {
		this.errors = errors;
	}

	public T getImportedValue() {
		return importedValue;
	}

	public void setImportedValue(T importedValue) {
		this.importedValue = importedValue;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
