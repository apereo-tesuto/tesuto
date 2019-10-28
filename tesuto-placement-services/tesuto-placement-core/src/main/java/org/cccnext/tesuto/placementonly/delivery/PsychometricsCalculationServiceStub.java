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
package org.cccnext.tesuto.placementonly.delivery;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;

public class PsychometricsCalculationServiceStub implements PsychometricsCalculationService {

	@Override
	public Double calculatePerformanceAsPercentage(Double pointsEarned, Double maxPointsPossible) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double calculateOdds(Double studentPerformanceAsPercentage) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double calculatedStudentAbility(Double logOdds, Double averageStudentItemDifficulty) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double calculateAverageStudentItemDifficulty(List<Double> calibratedDifficulties,
			Double previousAverageOutcome, Double countOutcome) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double adjustedCompetencyDifficulty(Double minimumStudentAbility, Double adjustedCompetencyDifficulty) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double calculateMinimumStudentAbility(Double probabilitySuccessThreshold) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public Double calculateReportingScale(Double studentAbility, Double additiveTerm, Double multiplicativeTerm) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

}
