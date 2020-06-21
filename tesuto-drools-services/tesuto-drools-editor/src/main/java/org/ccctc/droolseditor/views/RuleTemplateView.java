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
package org.ccctc.droolseditor.views;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;

public class RuleTemplateView extends RuleAttributesDTO {

	private static final long serialVersionUID = 1L;
	private String imports;
	private String definitions;
	private String whenStatement;
	private String thenClause;
	private String testTokenValues;
	private String customFunctions;

	public String getImports() {
		return imports;
	}

	public void setImports(String imports) {
		this.imports = imports;
	}

	public String getDefinitions() {
		return definitions;
	}

	public void setDefinitions(String definitions) {
		this.definitions = definitions;
	}

	public String getThenClause() {
		return thenClause;
	}

	public void setThenClause(String thenClause) {
		this.thenClause = thenClause;
	}

	public String getWhenStatement() {
		return whenStatement;
	}

	public void setWhenStatement(String whenStatement) {
		this.whenStatement = whenStatement;
	}

	public String getTestTokenValues() {
		return testTokenValues;
	}

	public void setTestTokenValues(String testTokenValues) {
		this.testTokenValues = testTokenValues;
	}

	@Override
	public void adjustValuesForUI() {
		super.adjustValuesForUI();
		whenStatement = whenStatement == null ? "" : whenStatement;
		thenClause = thenClause == null ? "" : thenClause;
		definitions = definitions == null ? "" : definitions;
		imports = imports == null ? "" : imports;
		testTokenValues = testTokenValues == null ? "" : testTokenValues;
		customFunctions = customFunctions == null ? "" : customFunctions;
	}

	@Override
	public void adjustValuesForStorage() {
		super.adjustValuesForStorage();
		whenStatement = StringUtils.isBlank(whenStatement) ? null : whenStatement;
		thenClause = StringUtils.isBlank(thenClause) ? null : thenClause;
		definitions = StringUtils.isBlank(definitions) ? null : definitions;
		imports = StringUtils.isBlank(imports) ? null : imports;
		testTokenValues = StringUtils.isBlank(testTokenValues) ? null : testTokenValues;
		customFunctions = StringUtils.isBlank(customFunctions) ? null : customFunctions;
	}

	public String getCustomFunctions() {
        return customFunctions;
    }

    public void setCustomFunctions(String customFunctions) {
        this.customFunctions = customFunctions;
    }

    @Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
