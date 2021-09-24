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
package org.cccnext.tesuto.rules.function;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.placement.view.MmapDataSourceType;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.rules.service.PlacementComponentActionService;
import org.ccctc.common.droolscommon.RulesAction;

public class DrlFunctions {

	private static Comparator<PlacementComponentViewDto> placementComponentViewDtoComparator =
			(PlacementComponentViewDto o1, PlacementComponentViewDto o2) -> {
		if (o1.getCb21().getLevel() == o2.getCb21().getLevel()) {
			return o1.getCreatedOn().compareTo(o2.getCreatedOn());
		} else {
			return o2.getCb21().getLevel() - o1.getCb21().getLevel();
		}
	};

	private static Comparator<PlacementViewDto> placementViewDtoComparator =
			(PlacementViewDto o1, PlacementViewDto o2) -> {
		if (o1.getCb21().getLevel() == o2.getCb21().getLevel()) {
			return o1.getCreatedOn().compareTo(o2.getCreatedOn());
		} else {
			return o2.getCb21().getLevel() - o1.getCb21().getLevel();
		}
	};

	public static PlacementViewDto getHighestAndBestPlacementFromList(List<PlacementViewDto> placements) {
		PlacementViewDto highestAndBestPlacement = null;
		if (placements != null && placements.size() != 0) {
			highestAndBestPlacement = Collections.max(placements, placementViewDtoComparator);
		}
		return highestAndBestPlacement;
	}

	public static PlacementComponentViewDto getHighestAndBestPlacementComponentFromList(List<PlacementComponentViewDto> placementComponents) {
		PlacementComponentViewDto highestAndBestPlacementComponent = null;
		if (placementComponents != null && placementComponents.size() != 0) {
			highestAndBestPlacementComponent = Collections.max(placementComponents, placementComponentViewDtoComparator);
		}
		return highestAndBestPlacementComponent;
	}

	public static PlacementComponentViewDto getHighestAndBestPlacementComponentFromList(List<PlacementComponentViewDto> placementComponents, boolean useStandalone, boolean useSelfReportedData) {
		PlacementComponentViewDto highestAndBestPlacementComponent = null;
		if (placementComponents != null && placementComponents.size() != 0) {
			Collections.sort(placementComponents, placementComponentViewDtoComparator);
			for (int index = placementComponents.size() -1; index >= 0; index--) {
				if ((useSelfReportedData || placementComponents.get(index).getDataSourceType() != MmapDataSourceType.SELF_REPORTED) &&
						((useStandalone && placementComponents.get(index).getStandalonePlacement()) || !useStandalone)) {
					highestAndBestPlacementComponent = placementComponents.get(index);
					break;
				}
			}
		}
		return highestAndBestPlacementComponent;
	}

	public static PlacementComponentViewDto getHighestAndBestPlacementComponent(PlacementComponentViewDto component1, PlacementComponentViewDto component2) {
		PlacementComponentViewDto highestAndBestPlacementComponent = null;
		if (component1 != null && component2 == null) {
			return component1;
		} else if (component1 == null && component2 != null) {
			return component2;
		} else if (placementComponentViewDtoComparator.compare(component1, component2) > 0) {
			highestAndBestPlacementComponent = component1;
		} else if (placementComponentViewDtoComparator.compare(component1, component2) <= 0) {
			highestAndBestPlacementComponent = component2;
		}
		return highestAndBestPlacementComponent;
	}

	   /*
     * This filter finds an action with a name of "MULTIPLE_MEASURE_PLACEMENT"
     * from the rulesActions, creating one if it doesn't exist. It then creates
     * a placement (transfer level) in
     * action.getActionParameters().get("placements"
     * ).get(placement.getCollegeId()).get(placement.getSubjectArea())
     */
    public static void filterByCollegeSubjectPlacement(List<RulesAction> rulesActions,
            PlacementComponentActionResult placement) {

        // Validate there is only one MULTIPLE_MEASURE_PLACEMENT Action
        List<RulesAction> multipleMeasurePlacements = rulesActions
                .stream()
                .filter(ra -> ra.getActionName().equalsIgnoreCase(
                        PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME)).collect(Collectors.toList());

        if (multipleMeasurePlacements.size() > 1) {
            throw new RuntimeException(
                    "Too Many Multiple Measure Placement RulesAction Objects found, only one allowed per execution");
        }

        RulesAction action = null;
        if (!CollectionUtils.isEmpty(multipleMeasurePlacements)) {
            action = multipleMeasurePlacements.get(0);
        } else {
            action = new RulesAction(PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME);
            action.addActionParameter("placements", new HashMap<String, Map<String, PlacementComponentActionResult>>());
            rulesActions.add(action);
        }

        Map<String, Map<String, PlacementComponentActionResult>> placementsbyCollegeBySubjectArea = (Map<String, Map<String, PlacementComponentActionResult>>) action
                .getActionParameters().get("placements");

        // checks for placementsBySubjectArea by expected miscode, if null
        // create and adds to placementsbyCollegeBySubjectArea
        placementsbyCollegeBySubjectArea.putIfAbsent(placement.getCollegeId(),
                new HashMap<String, PlacementComponentActionResult>());
        Map<String, PlacementComponentActionResult> placementsBySubjectArea = placementsbyCollegeBySubjectArea.get(placement
                .getCollegeId());

        // Adds placement by subjectArea by taking the highest available
        // transfer level between current and new possible placement
        placementsBySubjectArea.put(placement.getSubjectArea(),
                getBestTransferLevel(placementsBySubjectArea.get(placement.getSubjectArea()), placement));
    }

    static private PlacementComponentActionResult getBestTransferLevel(PlacementComponentActionResult currentPlacement,
            PlacementComponentActionResult placement) {
        if (currentPlacement == null || currentPlacement.getLevelsBelowTransfer() > placement.getLevelsBelowTransfer()) {
            return placement;
        }
        if (currentPlacement.getLevelsBelowTransfer() == placement.getLevelsBelowTransfer()) {
            // If either placement is invalid then set the placement to valid
            // placement
            if (!currentPlacement.getInsufficientData() || !placement.getInsufficientData()) {
                currentPlacement.setInsufficientData(false);
            }
            if (!placement.getInsufficientData()) {
                currentPlacement.addPrograms(placement.getPrograms());
            }
        }
        return currentPlacement;
    }

    public static Double parseDouble(String value) {
        if (StringUtils.isBlank(value)) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    public static Double parseDoubleOrNull(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Double.parseDouble(value);
    }

    public static Integer parseInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public static Double parseFactToDouble(Fact fact) {
        if (fact == null || StringUtils.isBlank(fact.getValue())) {
            return 0.0;
        }
        return Double.parseDouble(fact.getValue());
    }

    public static Integer parseFactToInteger(Fact fact) {
        if (fact == null || StringUtils.isBlank(fact.getValue())) {
            return 0;
        }
        return Integer.parseInt(fact.getValue());
    }
    
    public static boolean parseFactToBoolean(Fact fact) {
        if (fact == null || StringUtils.isBlank(fact.getValue())) {
            return false;
        }
        return Boolean.parseBoolean(fact.getValue());
    }

    public static String parseFactToString(Fact fact) {
        if (fact == null || StringUtils.isBlank(fact.getValue())) {
            return null;
        }
        return fact.getValue();
    }


    public static String OPT_IN_KEY() {
        return PlacementMapKeys.OPT_IN_KEY;
    }

    public static String CCCID_KEY() {
        return PlacementMapKeys.CCCID_KEY;
    }

    public static String MISCODE_KEY() {
        return PlacementMapKeys.MISCODE_KEY;
    }

    public static String MULTIPLE_MEASURE_VARIABLE_SET_KEY() {
        return PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY;
    }

    public static String RULE_SET_ID_KEY() {
        return PlacementMapKeys.RULE_SET_ID_KEY;
    }

    public static String STUDENT_VIEW_KEY() { 
        return PlacementMapKeys.STUDENT_VIEW_KEY;
    }

    public static String SUBJECT_AREA_KEY() {
        return PlacementMapKeys.SUBJECT_AREA_KEY;
    }

    public static String TRACKING_ID_KEY() {
        return PlacementMapKeys.TRACKING_ID_KEY;
    }

    public static String HIGHEST_READING_LEVEL_COURSE_KEY() {
        return PlacementMapKeys.HIGHEST_READING_LEVEL_COURSE_KEY;
    }

    public static String PREREQUISITE_GENERAL_EDUCATION_KEY() {
        return PlacementMapKeys.PREREQUISITE_GENERAL_EDUCATION_KEY;
    }

    public static String PREREQUISITED_STATISTICS_KEY() {
        return PlacementMapKeys.PREREQUISITED_STATISTICS_KEY;
    }

	public static String COMPETENCY_ATTRIBUTES_KEY() {
		return PlacementMapKeys.COMPETENCY_ATTRIBUTES_KEY;
	}

	public static String PLACEMENT_COMPONENTS_KEY() {
		return PlacementMapKeys.PLACEMENT_COMPONENTS_KEY;
	}

	public static String PLACEMENTS_KEY() {
		return PlacementMapKeys.PLACEMENTS_KEY;
	}
}
