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
package org.cccnext.tesuto.content.web.controller;

import java.util.List;

import org.cccnext.tesuto.content.service.UseItemCategoryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service/v1/use-item-category")
public class UseItemCategoryEndpoint {

	@Autowired
	UseItemCategoryReader reader;
	
	@PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_ITEM', 'API')")
    @RequestMapping(value = "branch-rule/{namespace}", method = RequestMethod.GET)
	public @ResponseBody String isCategoryUsedInBranchRuleEvaluation(@RequestBody(required=false) List<String> categoryNames,@PathVariable("namespace") String namespace) {
		
		if(reader.isCategoryUsedInBranchRuleEvaluation(categoryNames, namespace))
			return UseItemCategoryReader.BOOLEAN_TRUE;
		return UseItemCategoryReader.BOOLEAN_FALSE;
	}
	
	@PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_ITEM', 'API')")
    @RequestMapping(value = "placement-model/{namespace}", method = RequestMethod.GET)
	public @ResponseBody String isCategoryUsedInPlacementModelEvaluation(@RequestBody(required=false) List<String> categoryNames,@PathVariable("namespace") String namespace) {
		
		if(reader.isCategoryUsedInPlacementModelEvaluation(categoryNames, namespace))
			return UseItemCategoryReader.BOOLEAN_TRUE;
		return UseItemCategoryReader.BOOLEAN_FALSE;
	}
}
