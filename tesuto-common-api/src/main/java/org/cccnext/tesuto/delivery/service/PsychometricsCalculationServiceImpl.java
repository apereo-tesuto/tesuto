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
package org.cccnext.tesuto.delivery.service;

import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 9/7/16.
 */
@Service("psychometricsCalculationService")
public class PsychometricsCalculationServiceImpl implements PsychometricsCalculationService {
    /**
     * For a student who has a Student Performance (SP) of 0%, the value .05% (.005) should be substituted.
     * For students who has a Student Performance (SP) of 100%, the value 99.5% (.995) should be substituted
     * @param pointsEarned
     * @param maxPointsPossible
     * @return
     */
    @Override
    public Double calculatePerformanceAsPercentage(Double pointsEarned, Double maxPointsPossible) {
        Double performance;

        if (maxPointsPossible == 0.0d || pointsEarned.equals(maxPointsPossible)) {
            performance = .995d;
        }
        else if (pointsEarned == 0.0d) {
            performance = 0.005d;
        }
        else {
            performance = pointsEarned / maxPointsPossible;
        }
        return round(performance, 4);
    }

    /**
     * Student Item Difficulty
     * SID = Average (D1 + D2 + ... + Dn)
     * @param calibratedDifficulties
     * @return
     */
    @Override
    public Double calculateAverageStudentItemDifficulty(List<Double> calibratedDifficulties, Double previousAverageOutcome, Double countOutcome) {
        double sum = calibratedDifficulties.stream().mapToDouble(Double::doubleValue).sum();
        double denominator = calibratedDifficulties.size();
        if(countOutcome != null){
            sum += previousAverageOutcome * countOutcome;
            denominator += countOutcome;
        }

        double average = sum / denominator;

        return average;
    }

    /**
     *  Student Ability (SA) from Student Performance (SP):
     *  SP / 1 - SP = e^(SA - SID)
     *
     *  Solve for for SA (Student Ability)
     *
     *  SP / 1 - SP = Odds
     *  Odds = e^(SA - SID)
     *  ln(Odds) = ln(e^SA - SID)
     *  ln(Odds) = (SA - SID)ln (e)
     *  ln(Odds) = (SA - SID)
     *  ln(Odds) + SID =  SA
     * @param Odds
     * @param averageStudentItemDifficulty
     * @return
     */
    @Override
    public Double calculatedStudentAbility(Double Odds, Double averageStudentItemDifficulty) {
        return (Math.log(Odds) + averageStudentItemDifficulty);
    }
    
    /**
     *  Adjusted Competency Difficulty (ACD) from Probability of Success Threshold
     *  MSA: minimum student ability
     *  MCD: Course Mean (average) Competency Difficulty (MCD)
     *  MSA / 1 - MSA = e^(ACD - MCD)
     *
     *  Solve for for ACD (Adjusted Competency Difficulty)
     *
     *  MSA / 1 - MSA = Odds
     *  MSA = e^(ACD - MCD)
     *  ln(MSA) = ln(e^ACD - MCD)
     *  ln(MSA) = (ACD - MCD)ln (e)
     *  ln(MSA) = (ACD - MCD)
     *  ln(MSA) + MCD =  ACD
     * @param minimumStudentAbility
     * @param adjustedCompetencyDifficulty
     * @return
     */
    @Override
    public Double adjustedCompetencyDifficulty(Double minimumStudentAbility, Double adjustedCompetencyDifficulty) {
        return calculatedStudentAbility(minimumStudentAbility, adjustedCompetencyDifficulty);
    }

    /**
     * Odds = SP / 1 - SP
     * TODO what happens with a student has a perfect score (divide by zero here)
     *
     * @param studentPerformanceAsPercentage
     * @return
     */
    @Override
    public Double calculateOdds(Double studentPerformanceAsPercentage) {
        return studentPerformanceAsPercentage / (1 - studentPerformanceAsPercentage);
    }
    
    /**
     * Odds = SP / 1 - SP
     * TODO what happens with a student has a perfect score (divide by zero here)
     *
     * @param studentPerformanceAsPercentage
     * @return
     */
    @Override
    public Double calculateMinimumStudentAbility(Double probabilitySuccessThreshold) {
        return calculateOdds(probabilitySuccessThreshold);
    }

    /**
     * RS = additiveTerm + (multiplicativeTerm * studentAbility)
     *
     * @param studentAbility
     * @param additiveTerm given in assessmentMetadata
     * @param multiplicativeTerm given in assessmentMetadata
     * @return
     */
    @Override
    public Double calculateReportingScale(Double studentAbility, Double additiveTerm, Double multiplicativeTerm) {
        return additiveTerm + (multiplicativeTerm * studentAbility);
    }

    private Double round(Double number, int precision) {
        return new BigDecimal(number).setScale(precision, RoundingMode.HALF_UP).doubleValue();
    }
}
