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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.client.ActivationServiceRestClient;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.Activation.Status;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.service.TestLocationReader;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.enums.MetadataType;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.delivery.service.TaskReader;
import org.cccnext.tesuto.domain.util.NumberedAscii;
import org.cccnext.tesuto.domain.util.ZipFileCompressor;
import org.cccnext.tesuto.reports.model.AssessmentReportStructure;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.model.ResponseRecord;
import org.cccnext.tesuto.reports.model.inner.JsonBResponseStructure;
import org.joda.time.DateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.amazonaws.util.CollectionUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ReportsService {


    private final static String ERROR_NO_ASSEMENTSESSIONS_FOUND = "No assessment sessions were found for the search criteria";
    private final static String NO_ASSESMENT_SESSIONS_FILE_NAME = "noAssessmentSessionsFound";
    @Autowired
    private AssessmentSessionDao assessmentSessionDao;

    @Autowired
    private AssessmentReader assessmentReader;

    @Autowired
    private AssessmentItemReader assessmentItemReader;

    @Autowired
    private AssessmentStructureService assessmentReportStructureService;

    @Autowired
    private AttemptRecordService attemptRecordService;

    @Autowired
    private TaskReader taskService;

    @Autowired
    private ResponseRecordService responseRecordService;

    @Autowired
    private ActivationServiceRestClient activationSearchService;

    @Autowired
    private TestLocationReader testLocationService;

    @Qualifier("staticReportStorage")
    @Autowired
    private ReportStorage reportStorage;

    @Autowired
    private ZipFileCompressor zipfileCompressor;

    @Autowired
    private DataSource dataSource;

    @Value("${report.directory}")
    private String reportDirectory;

    @Value("${report.response.report.s3.path}")
    private String responseReportS3Path;

    @Value("${report.zip.file.password}")
    private String password;

    @Value("${report.zip.file.directory}")
    private String zipfileDirectory;

    @Value("${report.lower.bound.completion.date.in.days}")
    private Integer lowerBoundCompletionDate;
    
    @Value("${report.attempt.page.size}")
    private Integer pageSize;

    public AssessmentSessionDao getAssessmentSessionDao() {
        return assessmentSessionDao;
    }

    public void setAssessmentSessionDao(AssessmentSessionDao assessmentSessionDao) {
        this.assessmentSessionDao = assessmentSessionDao;
    }

    public AssessmentReader getAssessmentReader() {
        return assessmentReader;
    }

    public void setAssessmentReader(AssessmentReader assessmentReader) {
        this.assessmentReader = assessmentReader;
    }

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public void getByDateCompletedReport() throws IOException {
        ResultsSearchParameters searchParameters = new ResultsSearchParameters();

        DateTime completionDateLowerBound = DateTime.now().minusDays(lowerBoundCompletionDate).withTimeAtStartOfDay();
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());

        FileSystemResource zippedFile = buildAndStoreReportsWithLock(searchParameters);

        reportStorage.storeFile(responseReportS3Path, zippedFile.getFile());
    }

    public File processResults(ResultsSearchParameters searchParameters) {
        searchParameters.setFields(getAsssessmentSessionSelectFields());
        File fileDirectory = getDirectory();
        if (searchParameters.searchCompletedAssessmentSessions()) {
            List<AssessmentSession> completedAttempts = findCompletedAssessmentSessions(searchParameters);
            if (CollectionUtils.isNullOrEmpty(completedAttempts)) {
                logNoAssessmentSessionsFound(fileDirectory, "completed_");
            } else {
                processResults(completedAttempts, "completed_", fileDirectory);
            }
        }

        if (searchParameters.searchExpiredAssessmentSessions()) {
            List<AssessmentSession> expiredAttempts = findExpiredAssessmentSessions(
                    searchParameters.getCompletionDateLowerBound(), searchParameters.getCompletionDateUpperBound());

            if (CollectionUtils.isNullOrEmpty(expiredAttempts)) {
                logNoAssessmentSessionsFound(fileDirectory, "expired_");
            } else {
                processResults(expiredAttempts, "expired_", fileDirectory);
            }
        }

        return fileDirectory;
    }

    private List<String> getAsssessmentSessionSelectFields() {
        List<String> fields = new ArrayList<String>();
        fields.add("assessmentSessionId");
        fields.add("contentId");
        return fields;
    }

    public File processResults(List<AssessmentSession> allAttempts, String filePrefix, File fileDirectory) {

        Map<String, List<AssessmentSession>> mappedAttemptsToContentId = mapAssessmentSessionsToContentId(allAttempts);

        log.debug("Total assessmentSessions reported on {}", mappedAttemptsToContentId.size());
        log.debug("Number different assessments reported on {}", mappedAttemptsToContentId.keySet().size());
        for (String contentId : mappedAttemptsToContentId.keySet()) {

            List<AssessmentSession> attemptsForContentId = mappedAttemptsToContentId.get(contentId);
            ReportStructureData data = prepReportStrutureData(contentId);
            if (data == null)
                continue;
            BufferedWriter writer = null;
            File file = getMappedFilesByContentId(fileDirectory, filePrefix, data.fileSuffix);
            try {
                writer = getFileWriter(file);
                writer.append(headers(data.orderedStructures));
                writer.newLine();
                writer.flush();
                int cursorEnd = 0;
                int cursor = 0;
                do {
                    cursorEnd = cursor + pageSize;
                    if(attemptsForContentId.size() < cursorEnd) {
                        cursorEnd = attemptsForContentId.size();
                    }
                    List<AssessmentSession> attempts = attemptsForContentId.subList(cursor, cursorEnd);
                    cursor += pageSize;
                    Pair<List<String>, Map<String, AttemptRecord>> attemptRecords = attemptRecordService
                            .getAttemptRecords(attempts.stream().map(as -> as.getAssessmentSessionId())
                                    .collect(Collectors.toList()));

                    for (AssessmentSession attempt : attempts) {
                        if (attemptRecords.getLeft().contains(attempt.getAssessmentSessionId())) {
                            log.debug("Updating report table for assessmentSession {}",
                                    attempt.getAssessmentSessionId());
                            data.upsertReportTable = true;
                            data.attemptIdsToBeUploaded.add(attempt.getAssessmentSessionId());
                            data.attemptRecord = processAttemptRecordForAttempt(attempt, data.sectionMetaData);
                            processResponseRecords(attempt, data);
                            updateFormattedDuration(data.attemptRecord);
                            data.attemptRecord.setResultsByColumn(printLine(contentId, data));
                            attemptRecordService.save(data.attemptRecord);
                        } else {
                            data.attemptRecord = attemptRecords.getRight().get(attempt.getAssessmentSessionId());
                            if (StringUtils.isBlank(data.attemptRecord.getResultsByColumn())) {
                                data.mapResponseStructures(responseRecordService
                                        .getResponseRecordsForAttempt(attempt.getAssessmentSessionId()));
                                data.attemptRecord.setResultsByColumn(printLine(contentId, data));
                                attemptRecordService.save(data.attemptRecord);
                            }

                        }
                        writer.write(data.attemptRecord.getResultsByColumn());
                        writer.newLine();
                        writer.flush();
                        flushResponses(data);
                    }
                } while (attemptsForContentId.size() != cursorEnd);
            } catch (IOException e) {
                String message = String.format("IOException while writing out to : %s", file.getName());
                log.error(message, e);
                throw new ReportIOException(message);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }

        return fileDirectory;
    }
    
    private void updateFormattedDuration(AttemptRecord attemptRecord) {
        attemptRecord.setTotalDurationFormatted(DurationFormatUtils.formatDuration(attemptRecord.getTotalDuration(), "H:mm:ss", true));
    }

    private String headers(List<AssessmentReportStructure> structures) {
        StringBuilder headers = new StringBuilder();
        headers.append("Attempt ID").append(",")
                .append("User ID").append(",")
                .append("Assessment Identifier").append(",")
                .append("College").append(",")
                .append("Test Location").append(",")
                .append("Testlet Sequence").append(",")
                .append("Start Date").append(",")
                .append("Completion Date").append(",")
                .append("Total Time On Task").append(",")
                .append("Student Ability").append(",")
                .append("Points Scored").append(",")
                .append("Percent Score").append(",")
                .append("Odds Success").append(",")
                .append("Avg. Item Difficult").append(",")
                .append("Item Difficulty Count").append(",")
                .append("Reported Scale").append(",");
        for (AssessmentReportStructure structure : structures) {
            headers.append(structure.getItemScopedIdentifier()).append("-Parent").append(",");
            headers.append(structure.getItemScopedIdentifier()).append("-Responses").append(",");
            headers.append(structure.getItemScopedIdentifier()).append("-Outcome").append(",");
            headers.append(structure.getItemScopedIdentifier()).append("-Duration").append(",");
        }

        return headers.toString();
    }

    private String printLine(String contentId, ReportStructureData data) {
        saveReportStructureData(data);
        StringBuilder stringBuilder = new StringBuilder(attemptRecordOut(data.attemptRecord, data.assessementScopedId));

        for (AssessmentReportStructure structure : data.orderedStructures) {
            ResponseRecord responseRecord = data.getResponseRecord(structure);
            if (responseRecord != null) {
                stringBuilder.append(structure.getParentTestletId()).append(",");
                stringBuilder.append(StringEscapeUtils.escapeCsv(responseRecord.getResponses())).append(",");
                stringBuilder.append(responseRecord.getOutcomeScore()).append(",");
                stringBuilder.append(responseRecord.getDuration()).append(",");
            } else {
                stringBuilder.append(",,,,");
            }
        }

        return stringBuilder.toString();
    }

    private String attemptRecordOut(AttemptRecord attemptRecord, String assessmentScopedId) {

        StringBuilder csv = new StringBuilder();
        csv.append(attemptRecord.getAttemptId()).append(",")
                .append(attemptRecord.getCccid()).append(",")
                .append(assessmentScopedId).append(",")
                .append(StringEscapeUtils.escapeCsv(attemptRecord.getCollegeLabel())).append(",")
                .append(StringEscapeUtils.escapeCsv(attemptRecord.getTestLocationLabel())).append(",")
                .append(StringEscapeUtils.escapeCsv(attemptRecord.getTestletSequence())).append(",")
                .append(StringEscapeUtils.escapeCsv(getFormatedDate(attemptRecord.getStartDate()))).append(",")
                .append(StringEscapeUtils.escapeCsv(getFormatedDate(attemptRecord.getCompletionDate()))).append(",")
                .append(attemptRecord.getTotalDurationFormatted()).append(",")
                .append(attemptRecord.getStudentAbility()).append(",")
                .append(attemptRecord.getPointsScored()).append(",")
                .append(attemptRecord.getPercentScore()).append(",")
                .append(attemptRecord.getOddsSuccess()).append(",")
                .append(attemptRecord.getAverageItemDifficulty()).append(",")
                .append(attemptRecord.getItemDifficultyCount()).append(",")
                .append(attemptRecord.getReportedScale()).append(",");
            
        return csv.toString();
    }

    private String getFormatedDate(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        if (date != null) {
            return dateFormat.format(date);
        }
        return "";
    }

    private void flushResponses(ReportStructureData data) {
        responseRecordService.save(data.dirtyResponseRecords);
        data.dirtyResponseRecords.clear();
        data.responseItemIdStructureMap.clear();
        data.attemptRecord = null;
    }

    private File getMappedFilesByContentId(File fileDirectory, String filePrefix, String suffix) {

        File file = null;
        String fileName = filePrefix + getFileNameForAssessmentIdentifier(suffix);
        file = new File(fileDirectory, fileName);
        return file;
    }

    private Map<String, File> getMappedFiles(File fileDirectory, String filePrefix, Set<String> uniqueIdentifiers) {

        Map<String, File> mappedFiles = new HashMap<String, File>();
        for (String identifier : uniqueIdentifiers) {
            String fileName = filePrefix + getFileNameForAssessmentIdentifier(identifier);
            File file = new File(fileDirectory, fileName);
            mappedFiles.put(identifier, file);
        }

        return mappedFiles;
    }

    private ReportStructureData prepReportStrutureData(String contentId) {
        AssessmentDto assessment = assessmentReader.read(contentId);
        if (assessment == null) {
            return null;
        }

        ReportStructureData data = new ReportStructureData(assessment);

        List<AssessmentReportStructure> structures = assessmentReportStructureService
                .getReportStructureForAssessment(contentId);
        if (CollectionUtils.isNullOrEmpty(structures)) {
            log.debug("Building Assessment Structure for {}", data.assessmentId);
            buildAssessmentStructure(data);
            log.debug("Completed building Assessment Structure for {}", data.assessmentId);
        } else {
            data.setOrderedStructures(structures);
        }

        data.sort();
        return data;
    }

    private void saveReportStructureData(ReportStructureData data) {
        log.debug("{} item structures found for assessment {}", data.dirtyAssessmentStructures.size(),
                data.assessmentId);
        assessmentReportStructureService.save(data.dirtyAssessmentStructures);
        data.dirtyAssessmentStructures.clear();
    }

    private void buildAssessmentStructure(ReportStructureData data) {
        String testletParentId = "";
        for (AssessmentPartDto assessmentPart : data.assessment.getAssessmentParts()) {
            for (AssessmentSectionDto assessmentSection : assessmentPart.getAssessmentSections()) {
                processSectionSingleItem(data, assessmentSection, testletParentId);
            }
        }
    }

    private String getTestSequence(Map<String, MetadataType> sectionMetadata, Collection<String> sequence) {
        StringBuilder testletSequence = new StringBuilder("");
        if (sectionMetadata != null) {
            log.debug("SectionMetadata processed ofr TestletSequence.");
            for (String s : sequence) {
                if (sectionMetadata.containsKey(s) &&
                        (sectionMetadata.get(s).equals(MetadataType.TESTLET) ||
                        sectionMetadata.get(s).equals(MetadataType.ENTRYTESTLET))) {
                    testletSequence.append(s).append(":");
                }
            }
            if (testletSequence.length() > 1) {
                testletSequence.deleteCharAt(testletSequence.length() - 1);
            }
        }
        log.debug("SectionMetadata processed TestletSequence is {}", testletSequence);
        return testletSequence.toString();
    }

    List<AssessmentSession> findCompletedAssessmentSessions(ResultsSearchParameters searchParameters) {
        updateSearchParameters(searchParameters);
        return assessmentSessionDao.search(searchParameters);
    }

    List<AssessmentSession> findExpiredAssessmentSessions(Date expirationDateLowerBound,
            Date expirationDateUpperBound) {
        SearchParameters parameters = new SearchParameters();
        parameters.setCurrentStatus(Activation.Status.IN_PROGRESS);
        parameters.setMinEndDate(expirationDateLowerBound);
        Date expirationDateMax = expirationDateUpperBound == null ? new Date() : expirationDateUpperBound;
        parameters.setMaxEndDate(expirationDateMax);

        List<Activation> activations = activationSearchService.search(parameters);

        List<String> assessmentSessionIds = activations.stream()
                .filter(act -> act.getCurrentAssessmentSessionId() != null).collect(Collectors.toList()).stream()
                .map(act -> act.getCurrentAssessmentSessionId()).collect(Collectors.toList());

        ResultsSearchParameters searchParameters = new ResultsSearchParameters();
        searchParameters.setIds(assessmentSessionIds);
        searchParameters.setFields(getAsssessmentSessionSelectFields());
        return assessmentSessionDao.search(searchParameters);
    }

    private Long getNormalizedDuration(Long duration, Integer itemsInBundle) {
        if (duration == null || itemsInBundle == null || itemsInBundle.equals(0)) {
            return 0L;
        }
        return duration / itemsInBundle;
    }

    Map<String, List<AssessmentSession>> mapAssessmentSessionsToContentId(List<AssessmentSession> attempts) {
        Map<String, List<AssessmentSession>> mappedAssessmentSessions = new HashMap<String, List<AssessmentSession>>();
        for (AssessmentSession attempt : attempts) {
            if (mappedAssessmentSessions.containsKey(attempt.getContentId())) {
                mappedAssessmentSessions.get(attempt.getContentId()).add(attempt);
            } else {
                List<AssessmentSession> mappedAttempts = new ArrayList<AssessmentSession>();
                mappedAttempts.add(attempt);
                mappedAssessmentSessions.put(attempt.getContentId(), mappedAttempts);
            }
        }
        return mappedAssessmentSessions;
    }

    private AttemptRecord processAttemptRecordForAttempt(AssessmentSession assessmentSession,
            Map<String, MetadataType> sectionMetaData) {

        assessmentSession = assessmentSessionDao.find(assessmentSession.getAssessmentSessionId());
        AttemptRecord attemptRecord = new AttemptRecord();
        log.debug("Creating attemptRecord for assessmentSession: {}", assessmentSession.getAssessmentSessionId());

        attemptRecord.setAssessmentId(assessmentSession.getContentId());

        attemptRecord.setAttemptId(assessmentSession.getAssessmentSessionId());

        attemptRecord.setCccid(assessmentSession.getUserId());

        Activation activation = getAssessmentActivation(assessmentSession.getUserId(),
                assessmentSession.getAssessmentSessionId());

        attemptRecord.setStartDate(assessmentSession.getStartDate());
        attemptRecord.setCompletionDate(assessmentSession.getCompletionDate());

        attemptRecord.setTestletSequence(getTestSequence(sectionMetaData, assessmentSession.getSequence().values()));
        setOutcomes(assessmentSession,  attemptRecord);
        TestLocationDto testLocation = null;
        if(activation != null) {
             testLocation = testLocationService.read(activation.getLocationId());
        }
        if (testLocation == null) {
            attemptRecord.setTestLocationLabel("Test Location not available.");
            attemptRecord.setTestLocationId("1");
            
            attemptRecord.setCollegeLabel("College not available.");
            attemptRecord.setCollegeId("NONE");
            return attemptRecord;
        } else {
        
            attemptRecord.setTestLocationLabel(testLocation.getName());
            attemptRecord.setTestLocationId(activation.getLocationId());
        
            attemptRecord.setCollegeLabel(testLocation.getCollegeName());
            attemptRecord.setCollegeId(testLocation.getCollegeId());
        
            return attemptRecord;
        }
    }
    
    private void setOutcomes(AssessmentSession assessmentSession, AttemptRecord attemptRecord) {
        attemptRecord.setStudentAbility(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY)));
        attemptRecord.setOddsSuccess(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_ODDS_SUCCESS)));
        attemptRecord.setPointsScored(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_POINTS_SCORE)));
        attemptRecord.setPercentScore(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_PERCENT_SCORE)));
        attemptRecord.setAverageItemDifficulty(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_AVG_ITEM_DIFFICULTY)));
        attemptRecord.setItemDifficultyCount(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT)));
        attemptRecord.setReportedScale(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_REPORTED_SCALE)));
    }
    
    private Double returnOutcomeAsDouble(Outcome outcome) {
        if (outcome != null 

                && !Double.isNaN(outcome.getValue())) {
            return outcome.getValue();
        }
        return null;
    }

    private void processResponseRecords(AssessmentSession attempt, ReportStructureData data) {

        attempt = assessmentSessionDao.find(attempt.getAssessmentSessionId());
        for (TaskSet taskSet : taskService.allTaskSets(attempt)) {
            for (Task task : taskSet.getTasks()) {
                for (ItemSession itemSession : task.getItemSessions()) {
                    ResponseRecord responseRecord = new ResponseRecord();

                    AssessmentReportStructure structure = getItemStructure(itemSession.getItemId(),
                            itemSession.getItemRefIdentifier(), data);

                    if(StringUtils.isBlank(structure.getItemRefIdentifier())) {
                        continue;
                    }
                    responseRecord.setItemRefIdentifier(structure.getItemRefIdentifier());
                    responseRecord.setDuration(getNormalizedDuration(taskSet.getDuration(), structure.getItemsInBundle()));
                    responseRecord.setAttemptIndex(itemSession.getItemSessionIndex());
                    responseRecord.setOutcomeScore(getScore(itemSession));
                    responseRecord.setAttemptId(attempt.getAssessmentSessionId());
                    responseRecord.setItemId(itemSession.getItemId());

                    generateResponses(itemSession, responseRecord, structure, data);
                    data.attemptRecord.sumTotalDuration(responseRecord.getDuration());
                    data.addResponseStructure(responseRecord);
                }
            }
        }
    }

    private void generateResponses(ItemSession itemSession, ResponseRecord responseRecord,
            AssessmentReportStructure structure, ReportStructureData data) {
        if (MapUtils.isEmpty(itemSession.getResponses())) {
            responseRecord.setResponses("");
            responseRecord.setResponseIdentifiers("");
            responseRecord.setSingleCharacterResponse(' ');
        } else {
            String response = buildResponseString(itemSession.getResponses().values(), structure);
            responseRecord.setResponses(response);

            responseRecord.setResponseIdentifiers(buildResponse(itemSession.getResponses().values(), structure));
            Character singleCharacterResponse = buildResponseAsSingleCharacter(response, structure, data);
            responseRecord.setSingleCharacterResponse(singleCharacterResponse);
        }

    }

    private Double getScore(ItemSession itemSession) {
        Outcome outcome = itemSession.getOutcome("SCORE");
        List<String> outcomes = (outcome == null) ? null : outcome.getValues();
        String outcomeScore = (CollectionUtils.isNullOrEmpty(outcomes)) ? "0" : outcomes.get(0);
        return Double.parseDouble(outcomeScore);
    }

    private Character buildResponseAsSingleCharacter(String response, AssessmentReportStructure structure,
            ReportStructureData data) {
        Character singleCharacter = ' ';
        JsonBResponseStructure responseStructure = structure.getResponseStructure();

        if (CollectionUtils.isNullOrEmpty(responseStructure.getValueOrder()) || StringUtils.isBlank(response)) {
            return singleCharacter;
        }
        Map<String, Character> singleCharacterResponses = responseStructure.getSingleCharacterResponses();

        if (singleCharacterResponses.containsKey(response)) {
            singleCharacter = singleCharacterResponses.get(response);
        } else {
            singleCharacter = NumberedAscii.getSingleCharacter(singleCharacterResponses.size());
            singleCharacterResponses.put(response, singleCharacter);
            responseStructure.setSingleCharacterResponses(singleCharacterResponses);
            structure.setResponseStructure(responseStructure);
            data.upsertItemStructure(structure);
        }

        return singleCharacter;
    }

    private AssessmentReportStructure getItemStructure(String itemId, String itemRefIdentifier,
            ReportStructureData data) {
        AssessmentReportStructure structure = data.getStructureByItemId(itemId);
        if (structure == null) {
            structure = new AssessmentReportStructure();
            AssessmentItemDto item = assessmentItemReader.read(itemId);
            JsonBResponseStructure responseStructure = getItemReponseStructure(item);
            structure.setAssessmentId(data.assessmentId);
            structure.setItemId(itemId);
            structure.setItemScopedIdentifier(item.getScopedIdTag());
            structure.setItemRefIdentifier(itemRefIdentifier);
            structure.setResponseStructure(responseStructure);
            data.upsertItemStructure(structure);
        } else if (structure.getResponseStructure() == null) {
            AssessmentItemDto item = assessmentItemReader.read(itemId);
            structure.setResponseStructure(getItemReponseStructure(item));
        }
        return structure;
    }

    private String buildResponse(Collection<Response> responses, AssessmentReportStructure structure) {
        StringBuilder responseString = new StringBuilder();

        JsonBResponseStructure responseStructure = structure.getResponseStructure();

        // Must be test entry only return test entry
        if (CollectionUtils.isNullOrEmpty(responseStructure.getValueOrder())) {
            for (Response response : responses) {
                for (String value : response.getValues()) {
                    responseString.append(value).append(":");
                }
            }
            return StringUtils.abbreviate(responseString.toString(), 254);
        }

        Map<String, Response> mappedResponses = new HashMap<String, Response>();

        for (Response response : responses) {
            String responseIdentifier = response.getResponseId();
            for (String value : response.getValues()) {
                mappedResponses.put(String.format("%s:%s", responseIdentifier, value), response);
            }
        }
        List<String> orderValue = responseStructure.getValueOrder();

        for (String value : orderValue) {
            if (mappedResponses.containsKey(value)) {
                responseString.append(value.split(":")[1]);
            }
        }
        return responseString.toString();
    }

    private String buildResponseString(Collection<Response> itemResponses, AssessmentReportStructure structure) {
        Integer order = 1;
        JsonBResponseStructure responseStructure = structure.getResponseStructure();

        // Is text entry or extended text
        if (CollectionUtils.isNullOrEmpty(responseStructure.getValueOrder())) {
            StringBuilder responseValues = new StringBuilder();
            for (Response response : itemResponses) {
                for (String value : response.getValues()) {
                    responseValues.append(value).append(":");
                }
            }
            if (responseValues.length() > 0) {
                responseValues.deleteCharAt(responseValues.length() - 1);
            }
            return responseValues.toString();
        }
        Map<String, Integer> orderedMap = responseStructure.getOrderMap();
        String responseString = "";
        for (Response response : itemResponses) {
            String responseIdentifier = response.getResponseId();
            for (String value : response.getValues()) {
                order = orderedMap.get(String.format("%s:%s", responseIdentifier, value));
                if (order == null) {
                    log.warn("No match found for responseIdentifier {} and value {}", responseIdentifier, value);
                    responseString += responseIdentifier + ":" + value;
                } else {
                    responseString += NumberedAscii.getSingleCharacter(order);
                }
            }
        }
        return responseString;
    }

    private Activation getAssessmentActivation(String cccid, String assessmentSessionId) {
        SearchParameters parameters = new SearchParameters();
        parameters.setCurrentAssessmentSessionIds(new HashSet<String>(Arrays.asList(assessmentSessionId)));
        parameters.setUserIds(new HashSet<String>(Arrays.asList(cccid)));
        List<Activation> activations = activationSearchService.search(parameters);
        for (Activation activation : activations) {
            for (String activatedAssessmentSessionId : activation.getAssessmentSessionIds()) {
                if (activatedAssessmentSessionId.equals(assessmentSessionId)) {
                    if (activation.getStatus() == Status.COMPLETE) {
                        return activation;
                    } else {
                        log.info("Unable to find COMPLETED Activation for assessmentSessionId {} and cccid {}",
                                assessmentSessionId, cccid);
                        return null;
                    }
                }
            }
        }

        return null;
    }

    private void updateSearchParameters(ResultsSearchParameters searchParameters) {
        List<AssessmentDto> assessments = null;
        if (searchParameters.getAssessmentScopedIdentifier() != null) {
            assessments = assessmentReader.read(searchParameters.getAssessmentScopedIdentifier());
            if (assessments == null) {
                return;
            }
            List<String> assessmentIds = new ArrayList<String>();
            if (!CollectionUtils.isNullOrEmpty(searchParameters.getContentIds())) {
                assessmentIds.addAll(searchParameters.getContentIds());
            }
            for (AssessmentDto assessment : assessments) {
                assessmentIds.add(assessment.getId());
            }
            searchParameters.setContentIds(assessmentIds);
        }
        return;
    }

    private void processSectionSingleItem(ReportStructureData data, AssessmentSectionDto assessmentSection,
            String testletParentId) {
        int itemsInBundle = 1;
        List<AssessmentItemRefDto> refItems = assessmentSection.getAssessmentItemRefs();
        if (data.sectionMetaData != null && data.sectionMetaData.containsKey(assessmentSection.getId())) {
            if (data.sectionMetaData.get(assessmentSection.getId()) == MetadataType.TESTLET) {
                testletParentId = assessmentSection.getId();
            } else if (data.sectionMetaData.get(assessmentSection.getId()) == MetadataType.ITEMBUNDLE) {
                itemsInBundle = refItems.size();
            }
        }
        int order = data.orderedStructures.size() + 1;
        for (AssessmentItemRefDto refItem : refItems) {
            AssessmentReportStructure assessmentReportStructure = new AssessmentReportStructure();
            // TODO: Validate usages of this, why are we not using the Assessment Item ID GUID?
            AssessmentItemDto item = assessmentItemReader.readLatestPublishedVersion(data.namespace,
                    refItem.getItemIdentifier());
            assessmentReportStructure.setAssessmentId(data.assessmentId);
            assessmentReportStructure.setParentTestletId(testletParentId);
            assessmentReportStructure.setReportOrder(order++);
            assessmentReportStructure.setItemScopedIdentifier(item.getScopedIdTag());
            assessmentReportStructure.setItemsInBundle(itemsInBundle);
            assessmentReportStructure.setItemRefIdentifier(refItem.getIdentifier());
            assessmentReportStructure.setAssessmentScopedId(data.assessementScopedId);
            assessmentReportStructure.setItemId(item.getId());
            data.addAssessmentStructure(assessmentReportStructure);
        }
        for (AssessmentSectionDto assessmentSubsection : assessmentSection.getAssessmentSections()) {
            processSectionSingleItem(data, assessmentSubsection, testletParentId);
        }
    }

    private String getFileNameForAssessmentIdentifier(String assessmentIdentifier) {
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_hh_mm_ss");
        return String.format("%s_%s.csv", assessmentIdentifier.replace(":", "_"), sdf.format(new Date()));
    }

    private File getDirectory() {
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyy_hh_mm_ss");
        File fileDirectory = new File(reportDirectory.toString() + "/" + sdf.format(new Date()));
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        return fileDirectory;
    }

    private BufferedWriter getFileWriter(File writeFile) throws IOException {

        writeFile.createNewFile();
        FileWriter fileWriter = new FileWriter(writeFile.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        return bufferedWriter;

    }

    private JsonBResponseStructure getItemReponseStructure(AssessmentItemDto item) {
        Integer order = 0;
        JsonBResponseStructure responseStructure = new JsonBResponseStructure();
        Map<String, Integer> orderMap = responseStructure.getOrderMap();
        List<String> valueOrder = responseStructure.getValueOrder();
        for (AssessmentInteractionDto interaction : item.getInteractions()) {
            String responseIdentifier = interaction.getResponseIdentifier();
            switch (interaction.getType()) {
            case CHOICE_INTERACTION:
                AssessmentChoiceInteractionDto choice = (AssessmentChoiceInteractionDto) interaction;
                for (AssessmentSimpleChoiceDto value : choice.getChoices()) {

                    String key = String.format("%s:%s", responseIdentifier, value.getIdentifier());
                    orderMap.put(key, order++);
                    valueOrder.add(key);
                }
                break;
            case EXTENDED_TEXT_INTERACTION:
                break;
            case INLINE_CHOICE_INTERACTION:
                AssessmentInlineChoiceInteractionDto inlineChoice = (AssessmentInlineChoiceInteractionDto) interaction;
                for (AssessmentInlineChoiceDto value : inlineChoice.getInlineChoices()) {
                    String key = String.format("%s:%s", responseIdentifier, value.getIdentifier());
                    orderMap.put(key, order++);
                    valueOrder.add(key);
                }
                break;
            case MATCH_INTERACTION:
                AssessmentMatchInteractionDto match = (AssessmentMatchInteractionDto) interaction;

                AssessmentSimpleMatchSetDto rows = match.getMatchSets().get(0);
                AssessmentSimpleMatchSetDto columns = match.getMatchSets().get(1);
                for (AssessmentSimpleAssociableChoiceDto row : rows.getMatchSet()) {
                    for (AssessmentSimpleAssociableChoiceDto column : columns.getMatchSet()) {
                        String value = row.getIdentifier() + " " + column.getIdentifier();
                        String key = String.format("%s:%s", responseIdentifier, value);
                        orderMap.put(key, order++);
                        valueOrder.add(key);
                    }
                }
                break;
            case NULL_INTERACTION:
                break;
            case TEXT_ENTRY_INTERACTION:

                break;
            default:
                break;

            }
        }

        return responseStructure;
    }

    public FileSystemResource buildAndStoreReports(ResultsSearchParameters searchParameters) throws IOException {
        File reportDirectory = processResults(searchParameters);
        FileSystemResource encryptedZip = buildEncryptedZip(reportDirectory);
        deleteResource(reportDirectory);
        return encryptedZip;
    }

    public void clearLock() {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            stmt.execute("delete from report_lock where 1=1");
            stmt.close();
        } catch (Exception ex) {
            log.error("unable to execute delete report_lock", ex);
        }
    }

    public FileSystemResource buildAndStoreReportsWithLock(ResultsSearchParameters searchParameters)
            throws IOException {
        try (Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt
                            .executeQuery("Select last_report_date, current_date from report_lock for update");) {
                Date lastDate = null;
                Date currentDate = null; // current date according to database
                                         // server
                while (rs.next()) {
                    lastDate = rs.getDate(1);
                    currentDate = rs.getDate(2);
                }
                if (lastDate == null || !lastDate.equals(currentDate)) {
                    log.info("report lock acquired");
                    stmt.executeUpdate("update report_lock set last_report_date = current_date");
                    FileSystemResource res = buildAndStoreReports(searchParameters);
                    connection.commit();
                    return res;
                } else {
                    log.info("report lock not acquired");
                    return null;
                }
            } finally {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    // don't rethrow a rollback exception in case it would hide
                    // an earlier exception
                    log.error(e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private FileSystemResource buildEncryptedZip(File reportDirectory) throws IOException {

        List<File> files = Lists.asList(reportDirectory, new File[0]);

        return new FileSystemResource(
                zipfileCompressor.compressFiles(zipfileDirectory, getZipFileName(), files, password));
    }

    private void deleteResource(File file) throws IOException {
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        } else {
            FileUtils.deleteQuietly(file);
        }
    }

    private String getZipFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_hh_mm_ss");
        return String.format("%s_%s.zip", "tesuto_daily_reponses", sdf.format(new Date()));
    }

    private void writeErrorFile(File file, String message) {
        try {
            BufferedWriter fileWriter = getFileWriter(file);
            fileWriter.write(message);
            fileWriter.newLine();
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            log.error("Unable to create error file to leave the following message: {} ", message, ex);
        }
    }

    private void logNoAssessmentSessionsFound(File fileDirectory, String filePrefix) {
        log.warn(ERROR_NO_ASSEMENTSESSIONS_FOUND);
        Set<String> noAssessmentSessionsFound = new HashSet<String>();
        noAssessmentSessionsFound.add(NO_ASSESMENT_SESSIONS_FILE_NAME);
        Map<String, File> mappedFiles = getMappedFiles(fileDirectory, filePrefix, noAssessmentSessionsFound);
        writeErrorFile(mappedFiles.get(NO_ASSESMENT_SESSIONS_FILE_NAME), ERROR_NO_ASSEMENTSESSIONS_FOUND);

    }

    protected class ReportStructureData {

        String assessementScopedId;
        String namespace;
        String assessmentId;
        Map<String, MetadataType> sectionMetaData;
        int assessmentVersion;
        String fileSuffix;
        AssessmentDto assessment;
        List<String> attempsToAddToReport;
        boolean upsertReportTable = false;
        boolean updateReportTable = false;
        AttemptRecord attemptRecord;

        Map<String, AssessmentReportStructure> structuresMap = new HashMap<String, AssessmentReportStructure>();
        Map<String, AssessmentReportStructure> structureItemIdMap = new HashMap<String, AssessmentReportStructure>();
        List<AssessmentReportStructure> dirtyAssessmentStructures = new ArrayList<AssessmentReportStructure>();
        List<AssessmentReportStructure> orderedStructures = new ArrayList<AssessmentReportStructure>();
        Map<String, ResponseRecord> responseItemIdStructureMap = new HashMap<String, ResponseRecord>();
        List<ResponseRecord> dirtyResponseRecords = new ArrayList<ResponseRecord>();
        List<String> attemptIdsToBeUploaded = new ArrayList<String>();

        protected ReportStructureData(AssessmentDto assessment) {
            assessementScopedId = assessment.scopedId("_");
            namespace = assessment.getNamespace();
            assessmentId = assessment.getId();
            assessmentVersion = assessment.getVersion();
            fileSuffix = assessment.scopedId("_");
            if (assessment.getAssessmentMetadata() != null)
                sectionMetaData = assessment.getAssessmentMetadata().getSectionMetadata();
            this.assessment = assessment;
        }

        protected void addAssessmentStructure(AssessmentReportStructure structure) {
            if (!structuresMap.containsKey(structure.getItemRefIdentifier())) {
                structuresMap.put(structure.getItemRefIdentifier(), structure);
                dirtyAssessmentStructures.add(structure);
                orderedStructures.add(structure);
                structureItemIdMap.put(structure.getItemId(), structure);
            }
        }

        protected void setOrderedStructures(List<AssessmentReportStructure> orderedStructures) {
            this.orderedStructures = orderedStructures;
            mapAssessmentStructures(orderedStructures);
        }

        protected void mapAssessmentStructures(List<AssessmentReportStructure> orderedStructures) {
            orderedStructures.forEach(r -> structuresMap.put(r.getItemRefIdentifier(), r));
            orderedStructures.forEach(st -> structureItemIdMap.put(st.getItemId(), st));
        }

        protected void upsertItemStructure(AssessmentReportStructure structure) {
            if (!structureItemIdMap.containsKey(structure.getItemId())) {
                AssessmentReportStructure baseStructure = structuresMap.get(structure.getItemRefIdentifier());
                if (baseStructure == null) {
                    log.error(
                            "Unable to find Expected Assessment Report Structure was not created as part of orginal assessment structure: " + structure.getItemRefIdentifier());
                    baseStructure = structure;
                    if(!updateIncompleteStructure(baseStructure)) {
                        return;
                    }
                    structuresMap.put(structure.getItemRefIdentifier(), structure);
                }
                baseStructure.updateMetadata(structure);
                structure.setReportOrder(orderedStructures.size() + 1);
                structureItemIdMap.put(structure.getItemId(), structure);
                orderedStructures.add(structure);
            }
            this.dirtyAssessmentStructures.add(structure);
        }
        
        private Boolean updateIncompleteStructure(AssessmentReportStructure assessmentStructure) {
                if(StringUtils.isBlank(assessmentStructure.getAssessmentScopedId())){
                    if(!StringUtils.isBlank(assessmentStructure.getAssessmentId())) {
                        AssessmentDto dto = assessmentReader.read(assessmentStructure.getAssessmentId());
                        if(dto != null) {
                            assessmentStructure.setAssessmentScopedId(dto.scopedId("_"));
                            return true;
                        }
                    }
                    return false;
                }
               return false;
        }

        protected AssessmentReportStructure getStructureByItemId(String itemId) {
            return structureItemIdMap.get(itemId);
        }

        protected void sort() {
            orderedStructures = orderedStructures.stream()
                    .sorted(Comparator.comparing(AssessmentReportStructure::getReportOrder))
                    .collect(Collectors.toList());
        }

        protected void mapResponseStructures(List<ResponseRecord> responses) {
            responses.forEach(r -> responseItemIdStructureMap.put(r.getItemId(), r));
        }

        protected void addResponseStructure(ResponseRecord response) {
            dirtyResponseRecords.add(response);
            responseItemIdStructureMap.put(response.getItemId(), response);
        }

        protected ResponseRecord getResponseRecord(AssessmentReportStructure arStructure) {
            return responseItemIdStructureMap.get(arStructure.getItemId());
        }
    }

}
