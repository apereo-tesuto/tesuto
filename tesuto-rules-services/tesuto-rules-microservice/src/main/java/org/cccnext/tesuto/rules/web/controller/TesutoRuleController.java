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
package org.cccnext.tesuto.rules.web.controller;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetDTOMapper;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This controller handles all REST requests that are coming from TESUTO. That actually allows us to
 * separate the rules edit controller from the REST controller used by TESUTO.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Controller
@RequestMapping(value = "/service/v1")
public class TesutoRuleController {

	@Autowired(required=false)
	RuleService ruleService;

	@Autowired(required=false)
	RuleSetService ruleSetService;

	@Autowired
	RuleSetDTOMapper mapper;

	@PreAuthorize("hasAuthority('VIEW_SUBJECT_AREA_RULES')")
	@GetMapping("/colleges/{college-miscode}/ruleset")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Collection<RuleSetDTO> findRulesForCollege(@PathVariable("college-miscode") String collegeMisCode, @RequestParam(value="category", required=false) String category) {
		RuleAttributeFacetSearchForm search = new RuleAttributeFacetSearchForm();
		search.setFamily(collegeMisCode);
		search.setCategory(category);
		search.setStatus("published");   // should we make the version constants in RuleSetRowServiceImpl public?
		List<RuleSetDTO> ruleSets = ruleSetService.find(search);
		// we need to filter the row and probably only return the highest version for each ruleId
		return ruleSets;
	}

	@PreAuthorize("hasAuthority('VIEW_SUBJECT_AREA_RULES')")
	@GetMapping("/colleges/{college-miscode}/competency/{competency}/ruleset")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Collection<RuleSetDTO> findRulesForCollegeAndCompetency(@PathVariable("college-miscode") String collegeMisCode, @PathVariable("competency") String competency,
			@RequestParam(value="category", required=false) String category) {
		RuleAttributeFacetSearchForm search = new RuleAttributeFacetSearchForm();
		search.setFamily(collegeMisCode);
		search.setCompetencyMapDiscipline(competency);
		if ( StringUtils.isNotBlank(category)) {
			search.setCategory(category);
		}
		search.setStatus("published");   // should we make the version constants in RuleSetRowServiceImpl public?
		List<RuleSetDTO> ruleSets = ruleSetService.find(search);
		// we need to filter the row and probably only return the highest version for each ruleId
		return ruleSets;
	}

	@PreAuthorize("hasAuthority('VIEW_SUBJECT_AREA_RULES')")
	@GetMapping("/colleges/{college-miscode}/competency/{competency}/ruleset/{ruleSetId}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public RuleSetDTO getRuleSetRow(@PathVariable("college-miscode") String collegeMisCode, @PathVariable("competency") String competency,
			@PathVariable("ruleSetId") String ruleSetId) {

		return ruleSetService.getRuleSet(ruleSetId);
	}

	@GetMapping("rules/check")
	public ResponseEntity<String> checkIfRunning() {
		try {
			if (ruleSetService == null) {
				return new ResponseEntity<String>( "RuleSetService is not available", HttpStatus.SERVICE_UNAVAILABLE);
			} else {
				try {
					ruleSetService.getRuleSet("9123231");
				} catch (Exception ex) {
					return new ResponseEntity<String>( "RuleSetService call failed", HttpStatus.SERVICE_UNAVAILABLE);
				}
			}
			if (ruleService == null) {
				return new ResponseEntity<String>( "RuleService is not available", HttpStatus.SERVICE_UNAVAILABLE);
			} else {
				try {
					ruleService.getRule("9123231");
				} catch (Exception ex) {
					return new ResponseEntity<String>( "RuleSet call failed", HttpStatus.SERVICE_UNAVAILABLE);
				}
			}
		} catch (Exception ex) {
			return new ResponseEntity<String>( ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/**
	 * Compares two version strings.
	 *
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
	 *
	 * @param version1 a string of ordinal numbers separated by decimal points.
	 * @param version2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if version1 is _numerically_ less than version2.
	 *         The result is a positive integer if version1 is _numerically_ greater than version2.
	 *         The result is zero if the strings are _numerically_ equal.
	 */
	private int compareVersions(String version1, String version2) {
		if (version1 == null || version2 == null) {
			return version1 != null ? 1 : (version2 != null ? -1 : 0);
		}

		String[] vals1 = version1.split("\\.");
		String[] vals2 = version2.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
		i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length);
	}

}
