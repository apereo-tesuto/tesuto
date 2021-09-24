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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CsvImportError implements Serializable {
	private static final long serialVersionUID = 2L;

	public enum CsvImportErrorCode {
		CCCID_HEADER_MISSING,
		INVALID_CCCID_LENGTH,
		DUPLICATE_CCCID,
		UPLOAD_FAILED,
		MINIMUM_STUDENTS_NOT_MET,
		FILE_DOESNT_EXIST,
		INVALID_STUDENT,
		JSON_MAPPING_ERROR,
		PARSING_FAILED,
		FIRST_NAME_EMPTY,
		LAST_NAME_EMPTY,
		CCCID_EMPTY,
		NAME_MISMATCH,
		MINIMUM_ASSESSED_STUDENTS_NOT_MET,
		MINIMUM_SCORED_STUDENTS_NOT_MET,
		MISSING_ASSESSMENT_METADATA
	}

	public CsvImportError() {
	}

	public CsvImportError(String columnName, String columnValue, CsvImportErrorCode errorCode, Object[] errorMessageArgs) {
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.errorCode = errorCode;
		this.errorMessageArgs = errorMessageArgs;
	}

	public CsvImportError(CsvImportErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public CsvImportError(CsvImportErrorCode errorCode, Object[] errorMessageArgs) {
		this.errorCode = errorCode;
		this.errorMessageArgs = errorMessageArgs;
	}

	public CsvImportError(String columnName, CsvImportErrorCode errorCode) {
		this.columnName = columnName;
		this.errorCode = errorCode;
	}

	private String columnName;
	private CsvImportErrorCode errorCode;
	private Object[] errorMessageArgs;
	private String columnValue;

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public CsvImportErrorCode getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(CsvImportErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	public Object[] getErrorMessageArgs() {
		return errorMessageArgs;
	}
	public void setErrorMessageArgs(Object[] errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
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
