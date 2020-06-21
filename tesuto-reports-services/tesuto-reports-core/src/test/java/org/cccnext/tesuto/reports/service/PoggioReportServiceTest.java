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
package org.cccnext.tesuto.reports.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.activation.ActivationSearchService;
import org.cccnext.tesuto.activation.ActivationService;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.service.DeliveryService;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.service.AttemptRecordService;
import org.cccnext.tesuto.reports.service.PoggioReportsService;
import org.cccnext.tesuto.reports.service.ResponseRecordService;
import org.cccnext.tesuto.reports.service.ResultsSearchParameters;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo",
		"org.cccnext.tesuto.delivery.repository.mongo"})
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class PoggioReportServiceTest {

	private static String TEST_REPORT_PROCTOR = "TestReportProctor";
   
	@Value("${report.test.student}")
    String reportTestStudent;

    @Autowired
    @Qualifier("reportsGeneratorService")
    DeliveryTestService service;
    
	@Autowired
	@Qualifier("poggioReportsService")
	private PoggioReportsService reportService;

	@Autowired
	@Qualifier("activationService")
	private ActivationService activationService;

	@Autowired
	@Qualifier("attemptRecordService")
	private AttemptRecordService attemptRecordService;

	@Autowired
	@Qualifier("responseRecordService")
	private ResponseRecordService responseRecordService;

	@Autowired
	@Qualifier("activationSearchService")
	private ActivationSearchService activationSearchService;

	@Autowired
	@Qualifier("assessmentService")
	private AssessmentService assessmentService;

	@Autowired
	@Qualifier("deliveryService")
	private DeliveryService deliveryService;

	private List<AttemptRecord> attemptsInReport;

	private Boolean cleanReportTables = true; // FOR JENKINS should be true
	private Boolean deleteReportFiles = true; // FOR JENKINS should be true

	private Boolean readLatestPublishedOnly = true; // FOR JENKINS should be
														// true
	private String simulatedProctor = TEST_REPORT_PROCTOR; // FOR JENKINS should
															// be
															// TEST_REPORT_PROCTOR
	private Integer numberOfAttempts = 1; // FOR JENKINS should be 1
	private Double percentInProgressThreshold = 97.0; // FOR JENKINS should be
														// 97.0
	private Integer startDateRangeInDays = 4; // FOR JENKINS should be 4
	private Boolean expireInProgress = true; // FOR JENKINS should be true
	private long expectedReportGenerationTime = 3L; // FOR JENKINS should be 2
	private String[] competencyMapDisciplines = new String[] { "MATH", "ENGLISH", "ESL" };

	@Before
	public void setUp() throws Exception {
		attemptsInReport = attemptRecordService.findByCccid(reportTestStudent);
		deleteAttempts(cleanReportTables);
	}

	@After
	public void tearDown() throws Exception {
		deleteActivations();
		deleteAttempts(cleanReportTables);
	}

	@Test
	public void test() throws IOException {
		int pageSize = 100;
		for (AssessmentDto assessment : getAssesments()) {
			int n = 0;
			int generateN = 0;
			log.warn(String.format(
					"\n Starting Validation of assessment. Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s ",
					assessment.getScopedIdentifier(), assessment.getTitle(), assessment.getId()));
			try {
				if (numberOfAttempts - n > pageSize) {
					generateN = pageSize;
				} else {
					generateN = numberOfAttempts - n;
				}
				do {
					service.generateAssessmentSessions(simulatedProctor, assessment.getScopedIdentifier(), generateN,
							percentInProgressThreshold, startDateRangeInDays, expireInProgress);
					n += generateN;
				} while (n < numberOfAttempts);
				Instant start = Instant.now();
				deleteActivations();

				log.warn(String.format(
						"\nEnding Validation of assessment. Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s \n",
						assessment.getScopedIdentifier(), assessment.getTitle(), assessment.getId()));
			} catch (Exception ex) {
				log.error(String.format(
						"\nReports failed on test for assessment: Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s \n",
						assessment.getScopedIdentifier(), assessment.getTitle(), assessment.getId()));

			}
		}
		for (String completencyMapDiscipline : competencyMapDisciplines) {
			FileSystemResource file = generateReports(startDateRangeInDays, completencyMapDiscipline);
			if (deleteReportFiles && file != null) {
				file.getFile().delete();
			}
		}
	}
	
	@Test
	public void testReportGeneration() throws IOException {
		for (String completencyMapDiscipline : competencyMapDisciplines) {
			FileSystemResource file = generateReports(startDateRangeInDays, completencyMapDiscipline);
			if (deleteReportFiles && file != null) {
				file.getFile().delete();
			}
		}
	}

	private List<AssessmentDto> getAssesments() {
		if (readLatestPublishedOnly) {
			return assessmentService.readPublishedUnique();
		} else {
			return assessmentService.read();
		}
	}

    private void deleteActivations() {
        SearchParameters parameters = new SearchParameters();
        Set<String> creators = new HashSet<>();
        creators.add(TEST_REPORT_PROCTOR);
        parameters.setCreatorIds(creators);
        Set<Activation> activations = activationSearchService.search(parameters);
        activations.forEach(a -> activationService.delete(a.getActivationId()));
        activations.forEach(a -> deliveryService.remove(a.getCurrentAssessmentSessionId()));
    }

	private void deleteAttempts(Boolean cleanReportTables) {
		if (attemptsInReport != null && cleanReportTables) {
			attemptsInReport.forEach(ar -> responseRecordService.deleteByAttemptId(ar.getAttemptId()));
			attemptsInReport.forEach(ar -> attemptRecordService.deleteByAttemptId(ar.getAttemptId()));
			attemptsInReport = null;
		}
	}

	private FileSystemResource generateReports(Integer startDateRangeInDays, String competencyMapDiscipline)
			throws IOException {
		ResultsSearchParameters searchParameters = new ResultsSearchParameters();
		searchParameters.setSearchSet(ResultsSearchParameters.SearchSet.ALL);
		Date startDate = new Date();
		DateTime completionDateLowerBound = new DateTime(startDate).withTimeAtStartOfDay()
				.minusDays(startDateRangeInDays);
		DateTime completionDateUpperBound = new DateTime(startDate).withTime(23, 59, 59, 999);
		searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
		searchParameters.setCompletionDateUpperBound(completionDateUpperBound.toGregorianCalendar().getTime());
		searchParameters.setCompetencyMapDiscipline(competencyMapDiscipline);
		return reportService.buildAndStoreReports(searchParameters);
	}

}
