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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.activation.ActivationSearchService;
import org.cccnext.tesuto.activation.ActivationService;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.DeliveryService;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.service.AttemptRecordService;
import org.cccnext.tesuto.reports.service.ReportsService;
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
@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo"})
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class ReportServiceTest {

    private static String TEST_REPORT_PROCTOR = "TestReportProctor";
    
    @Value("${report.test.student}")
    String reportTestStudent;

    @Autowired
    @Qualifier("reportsGeneratorService")
    DeliveryTestService service;

    @Autowired @Qualifier("reportsService")
    private ReportsService reportService;

    @Autowired @Qualifier("activationService")
    private ActivationService activationService;

    @Autowired @Qualifier("attemptRecordService")
    private AttemptRecordService attemptRecordService;

    @Autowired @Qualifier("responseRecordService")
    private ResponseRecordService responseRecordService;

    @Autowired @Qualifier("activationSearchService")
    private ActivationSearchService activationSearchService;

    @Autowired @Qualifier("assessmentService")
    private AssessmentService assessmentService;

    @Autowired @Qualifier("deliveryService")
    private DeliveryService deliveryService;

    private List<AttemptRecord> attemptsInReport;

    private Boolean cleanReportTables = true;  // FOR JENKINS should be true
    private Boolean deleteReportFiles = true; // FOR JENKINS should be true

    private Boolean readLatestPublishedOnly = true; // FOR JENKINS should be true
    private String simulatedProctor  = TEST_REPORT_PROCTOR;  // FOR JENKINS should be TEST_REPORT_PROCTOR
    private Integer numberOfAttempts = 1; // FOR JENKINS should be 1
    private Double percentInProgressThreshold = 97.0; // FOR JENKINS should be 97.0
    private Integer startDateRangeInDays = 4;  // FOR JENKINS should be 4
    private Boolean expireInProgress = true;  // FOR JENKINS should be true
    private long expectedReportGenerationTime = 3L; // FOR JENKINS should be 2

    @Before
    public void setUp() throws Exception {
        attemptsInReport = attemptRecordService.findByCccid(reportTestStudent);
        deleteAttempts(true);
    }

    @After
    public void tearDown() throws Exception {
        deleteActivations();
        deleteAttempts(cleanReportTables);
    }
    @Test
    public void test() throws IOException {
        Integer expectedAttempts = 0;
        Integer actualAttempts = 0;
        for(AssessmentDto assessment : getAssesments()){
            log.warn(String.format("\n Starting Validation of assessment. Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s ",
                    assessment.getScopedIdentifier(),
                    assessment.getTitle(),
                    assessment.getId()));
            List<AssessmentSession> sessions = null;
            try {
               service.generateAssessmentSessions(simulatedProctor,
                    assessment.getScopedIdentifier(),
                    numberOfAttempts,
                    percentInProgressThreshold,
                    startDateRangeInDays, expireInProgress);
            } catch(Exception ex) {
                if(!assessment.getTitle().contains("Basic Linear Assesment From Assessment Items") && assessment.getId().split("-").length !=  5) {
                    ex.printStackTrace();
                    throw ex;
                } else {
                    continue;
                }
            }
            Instant start = Instant.now();

            FileSystemResource file = null;
            try {
                 file = generateReports(startDateRangeInDays, assessment.getScopedIdentifier());
            } catch(Exception ex) {
                if(!assessment.getTitle().contains("Basic Linear Assesment From Assessment Items")) {
                    throw ex;
                } else {
                    continue;
                }
            }
            Instant end = Instant.now();
            if(deleteReportFiles && file != null) {
                file.getFile().delete();
            }

            attemptsInReport = attemptRecordService.findByCccid(reportTestStudent);

            actualAttempts = attemptsInReport.size();


            if(!expectedAttempts.equals(actualAttempts)) {
                String message = String.format("\nAttempts expected: %s Actual Attempts: %s. Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s \n",
                        expectedAttempts,
                        actualAttempts,
                        assessment.getScopedIdentifier(),
                        assessment.getTitle(),
                        assessment.getId());
                log.error(message);
            }

            long gap = ChronoUnit.SECONDS.between(start, end);
            if(gap > expectedReportGenerationTime) {
                String message = String.format("\nReports took longer than %s second to generate. totalTime: %s (s), number of attempts: %s, Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s \n",
                        expectedReportGenerationTime,
                        gap,
                        actualAttempts,
                        assessment.getScopedIdentifier(),
                        assessment.getTitle(),
                        assessment.getId());
                log.error(message);
            }


            if(cleanReportTables) {
                expectedAttempts = 0;
                actualAttempts = 0;
            }
            deleteActivations();
            deleteAttempts(cleanReportTables);

            log.warn(String.format("\nEnding Validation of assessment. Assessment Identifier: %s, Assessment Title: %s, Mongo Id: %s \n",
                    assessment.getScopedIdentifier(),
                    assessment.getTitle(),
                    assessment.getId()));
        }
    }

    private List<AssessmentDto> getAssesments() {
        if(readLatestPublishedOnly) {
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
        if(attemptsInReport != null && cleanReportTables) {
            attemptsInReport.forEach(ar -> responseRecordService.deleteByAttemptId(ar.getAttemptId()));
            attemptsInReport.forEach(ar -> attemptRecordService.deleteByAttemptId(ar.getAttemptId()));
            attemptsInReport = null;
        }
    }

    private FileSystemResource generateReports(Integer startDateRangeInDays, ScopedIdentifier scopedIdentifier) throws IOException {
        ResultsSearchParameters searchParameters = new ResultsSearchParameters();
        searchParameters.setSearchSet(ResultsSearchParameters.SearchSet.ALL);
        Date startDate = new Date();
        searchParameters.setAssessmentScopedIdentifier(scopedIdentifier);
        DateTime completionDateLowerBound = new DateTime(startDate).withTimeAtStartOfDay().minusDays(startDateRangeInDays);
        DateTime completionDateUpperBound = new DateTime(startDate).withTime(23, 59, 59, 999);
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
        searchParameters.setCompletionDateUpperBound(completionDateUpperBound.toGregorianCalendar().getTime());
        return reportService.buildAndStoreReports(searchParameters);
    }

}
