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
package org.cccnext.tesuto.preview.test;

import java.util.List;

import org.cccnext.tesuto.content.dto.competency.Category;
import org.cccnext.tesuto.content.service.CategoryService;

public class CategoryServiceStub implements CategoryService {

	@Override
	public boolean isCategoryUsedInBranchRuleEvaluation(
			List<String> categoryNames, String namespace) {
		//Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCategoryUsedInPlacementModelEvaluation(
			List<String> categoryNames, String namespace) {
		//Auto-generated method stub
		return false;
	}

	@Override
	public Category create(Category category) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public Category create(String categoryName, String namespace,
			boolean useForBranchRule, boolean useForPlacementModel) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Category category) {
		//Auto-generated method stub

	}

}
