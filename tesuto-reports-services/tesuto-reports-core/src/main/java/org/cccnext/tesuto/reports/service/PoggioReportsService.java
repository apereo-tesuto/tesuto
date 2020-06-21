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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.client.ActivationServiceRestClient;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.Activation.Status;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.service.TestLocationReader;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.domain.util.NumberedAscii;
import org.cccnext.tesuto.domain.util.ZipFileCompressor;
import org.cccnext.tesuto.reports.model.AttemptRecord;
import org.cccnext.tesuto.reports.model.ResponseRecord;
import org.cccnext.tesuto.reports.model.inner.JsonBResponseStructure;


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
public class PoggioReportsService {

	Map<String, Integer> itemVersions = new ConcurrentHashMap<String, Integer>();
	Map<String, String> itemScopedIdentifiers = new ConcurrentHashMap<String, String>();


	private final static String ERROR_NO_ASSEMENTSESSIONS_FOUND = "No assessment sessions were found for the search criteria";
	private final static String NO_ASSESMENT_SESSIONS_FILE_NAME = "noAssessmentSessionsFound";
	
	@Autowired
	private AssessmentSessionDao assessmentSessionnDao;

	@Autowired
	private AssessmentReader assessmentService;

	@Autowired
	private AssessmentItemReader assessmentItemReader;

	@Autowired
	private ActivationServiceRestClient activationService;

	@Autowired
	private TestLocationReader testLocationService;

	@Qualifier("staticReportStorage")
	@Autowired
	private ReportStorage reportStorage;

	@Autowired
	private ZipFileCompressor zipfileCompressor;

	@Value("${report.directory}")
	private String reportDirectory;

	@Value("${report.poggio.response.report.s3.path}")
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
		return assessmentSessionnDao;
	}

	public void setAssessmentSessionDao(AssessmentSessionDao assessmentSessionnDao) {
		this.assessmentSessionnDao = assessmentSessionnDao;
	}

	public AssessmentReader getAssessmentService() {
		return assessmentService;
	}

	public void setAssessmentService(AssessmentReader assessmentService) {
		this.assessmentService = assessmentService;
	}

	public AssessmentItemReader getAssessmentItemReader() {
		return assessmentItemReader;
	}

	public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
		this.assessmentItemReader = assessmentItemReader;
	}

	public File processResults(ResultsSearchParameters searchParameters) {
		updateSearchParameters(searchParameters);
		searchParameters.setFields(getAsssessmentSessionSelectFields());
		File fileDirectory = getDirectory(searchParameters.getCompetencyMapDiscipline() == null ? "NO_Competency_Map_Given" : searchParameters.getCompetencyMapDiscipline());
		List<AssessmentSession> allAttempts = new ArrayList<>();
		if (searchParameters.searchCompletedAssessmentSessions()) {
			List<AssessmentSession> completedAttempts = findCompletedAssessmentSessions(searchParameters);
			if (!CollectionUtils.isNullOrEmpty(completedAttempts)) {
				allAttempts.addAll(completedAttempts);
			}
		}

		if (searchParameters.searchExpiredAssessmentSessions()) {
			List<AssessmentSession> expiredAttempts = findExpiredAssessmentSessions(
					searchParameters);

			if (!CollectionUtils.isNullOrEmpty(expiredAttempts)) {
				allAttempts.addAll(expiredAttempts);
			}
		}

		if (CollectionUtils.isNullOrEmpty(allAttempts)) {
			logNoAssessmentSessionsFound(fileDirectory, "no-attempts-found-to-process");
		} else {
			processResults(allAttempts, "competencyMapDiscipline", fileDirectory,
					searchParameters.getCompetencyMapDiscipline(), 
					searchParameters.getIncludeUnaffiliatedItems(),
					searchParameters.getAssessments());
		}

		return fileDirectory;
	}

	private List<String> getAsssessmentSessionSelectFields() {
		List<String> fields = new ArrayList<String>();
		fields.add("assessmentSessionId");
		fields.add("contentId");
		return fields;
	}
	
	private static String[] ITEM_FIELDS = new String[]{"_id","identifier","version"};

	public File processResults(List<AssessmentSession> allAttempts, String filePrefix, File fileDirectory,
			String competencyMapDiscipline, Boolean includeUnaffiliatedItems, List<AssessmentDto> assessments) {

		Map<String, AssessmentDto> assessmentsByIds = new HashMap<>();

		
		for (AssessmentDto assessment : assessments) {
			assessmentsByIds.put(assessment.getId(), assessment);
		}
		List<String> fields = Arrays.asList(ITEM_FIELDS);
		
		List<AssessmentItemDto> assessmentItems = assessmentItemReader.getItemsByCompetencyMapDiscipline(competencyMapDiscipline, null);
		PoggioDataStructure data = new PoggioDataStructure();

		for (AssessmentItemDto assessmentItem : assessmentItems) {
			PoggioReportStructure structure = getItemStructure(assessmentItem);
			data.addStructure(structure);
		}
				
		data.sort();
		

		File file = getMappedFilesByContentId(fileDirectory, filePrefix, competencyMapDiscipline);

		BufferedWriter writer = null;

		try {
			writer = getFileWriter(file);
			writer.append(firstLineheaders(data.orderedStructures));
			writer.newLine();
			writer.flush();
			writer.append(secondLineHeaders(data.orderedStructures));
			writer.newLine();		

			writer.flush();
			int cursorEnd = 0;
			int cursor = 0;
			do {
				cursorEnd = cursor + pageSize;
				if (allAttempts.size() < cursorEnd) {
					cursorEnd = allAttempts.size();
				}
				List<AssessmentSession> attempts = allAttempts.subList(cursor, cursorEnd);
				cursor += pageSize;

				for (AssessmentSession attempt : attempts) {
					data.flushResponses();
					AssessmentDto assessment = assessmentsByIds.get(attempt.getContentId());
					if (assessment == null) {
						assessment = assessmentService.read(attempt.getContentId());
						assessmentsByIds.put(attempt.getContentId(), assessment);
						log.error("No assessment found for assessmentSession, was retrieved manually check assessment metadata:" + attempt.getAssessmentSessionId());
						continue;
					}
					String contentScopedId = assessment.scopedId();

					log.debug("Updating report table for assessmentSession {}", attempt.getAssessmentSessionId());
					AttemptRecord attemptRecord = processAttemptRecordForAttempt(attempt);
					attemptRecord.setPointsScored(processResponseRecords(attempt, data));
					if(attemptRecord.getPointsScored() == 0 && data.responseRecordsByItemId.size() == 0) {
						log.warn(String.format(" Report on Assessment_Session %s had 0 points and no responses for Competency_Map_Discipline:%s"
								, attemptRecord.getAttemptId(), competencyMapDiscipline));
						continue;
					}
					writer.write(printLine(attemptRecord, contentScopedId, data));
					writer.newLine();
					writer.flush();
				}
			} while (allAttempts.size() != cursorEnd);
		} catch (IOException e) {
			String message = String.format("IOException while writing out to : %s", file.getName());
			log.error(message, e);
			throw new ReportIOException(message);
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return fileDirectory;
	}
	

	private String firstLineheaders(List<PoggioReportStructure> structures) {
		StringBuilder headers = new StringBuilder();
		headers.append("").append(",").append("").append(",").append("").append(",").append("").append(",").append("")
				.append(",").append("").append(",").append("");
		for (PoggioReportStructure structure : structures) {
			headers.append(structure.getBaseIdentifier()).append(",");
			headers.append("").append(",");
		}

		return headers.toString();
	}

	private String secondLineHeaders(List<PoggioReportStructure> structures) {
		StringBuilder headers = new StringBuilder();
		headers.append("Attempt ID").append(",")
		.append("Assessment Identifier").append(",")
		.append("User ID").append(",")
		.append("College").append(",")
		.append("Completion Date").append(",")
		.append("Total Raw Score").append(",");
		for (PoggioReportStructure structure : structures) {
			headers.append("Response").append(",");
			headers.append("Outcome").append(",");
		}

		return headers.toString();
	}

	private String printLine(AttemptRecord attemptRecord, String assessmentScopedId, PoggioDataStructure data) {
		StringBuilder stringBuilder = new StringBuilder(attemptRecordOut(attemptRecord, assessmentScopedId));

		for (PoggioReportStructure structure : data.orderedStructures) {
			ResponseRecord responseRecord = data.getResponseRecord(structure.getBaseIdentifier());
			if (responseRecord != null) {
				stringBuilder.append(StringEscapeUtils.escapeCsv(responseRecord.getResponses())).append(",");
				stringBuilder.append(responseRecord.getOutcomeScore()).append(",");
			} else {
				stringBuilder.append(",,");
			}
		}

		return stringBuilder.toString();
	}

	private String attemptRecordOut(AttemptRecord attemptRecord, String assessmentScopedId) {

		StringBuilder csv = new StringBuilder();
		csv.append(attemptRecord.getAttemptId()).append(",")
		.append(assessmentScopedId).append(",")
		.append(attemptRecord.getCccid()).append(",")
		.append(StringEscapeUtils.escapeCsv(attemptRecord.getCollegeLabel())).append(",")
		.append(StringEscapeUtils.escapeCsv(getFormatedDate(attemptRecord.getCompletionDate()))).append(",")
		.append(attemptRecord.getPointsScored()).append(",");

		return csv.toString();
	}

	private String getFormatedDate(Date date) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		if (date != null) {
			return dateFormat.format(date);
		}
		return "";
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

	List<AssessmentSession> findCompletedAssessmentSessions(ResultsSearchParameters searchParameters) {
		return assessmentSessionnDao.search(searchParameters);
	}

	List<AssessmentSession> findExpiredAssessmentSessions(ResultsSearchParameters searchParameters) {
		SearchParameters parameters = new SearchParameters();
		parameters.setCurrentStatus(Activation.Status.IN_PROGRESS);
		parameters.setMinEndDate(searchParameters.getCompletionDateLowerBound());
		Date expirationDateMax = searchParameters.getCompletionDateUpperBound() == null ? new Date() : searchParameters.getCompletionDateUpperBound();
		parameters.setMaxEndDate(expirationDateMax);

		List<Activation> activations = activationService.search(parameters);

		List<String> assessmentSessionIds = activations.stream()
				.filter(act -> act.getCurrentAssessmentSessionId() != null).collect(Collectors.toList()).stream()
				.map(act -> act.getCurrentAssessmentSessionId()).collect(Collectors.toList());

		searchParameters.setIds(assessmentSessionIds);
		searchParameters.setFields(getAsssessmentSessionSelectFields());
		return assessmentSessionnDao.search(searchParameters);
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

	private AttemptRecord processAttemptRecordForAttempt(AssessmentSession assessmentSession) {

		assessmentSession = assessmentSessionnDao.find(assessmentSession.getAssessmentSessionId());
		AttemptRecord attemptRecord = new AttemptRecord();
		log.debug("Creating attemptRecord for assessmentSession: {}", assessmentSession.getAssessmentSessionId());

		attemptRecord.setAssessmentId(assessmentSession.getContentId());

		attemptRecord.setAttemptId(assessmentSession.getAssessmentSessionId());

		attemptRecord.setCccid(assessmentSession.getUserId());

		Activation activation = getAssessmentActivation(assessmentSession.getUserId(),
				assessmentSession.getAssessmentSessionId());

		attemptRecord.setStartDate(assessmentSession.getStartDate());
		attemptRecord.setCompletionDate(assessmentSession.getCompletionDate());

		setOutcomes(assessmentSession, attemptRecord);

		if (activation == null) {
			attemptRecord.setTestLocationLabel("Test Location not available.");
			attemptRecord.setTestLocationId("1");

			attemptRecord.setCollegeLabel("College not available.");
			attemptRecord.setCollegeId("NONE");
			return attemptRecord;
		}

		TestLocationDto testLocation = testLocationService.read(activation.getLocationId());
		attemptRecord.setTestLocationLabel(testLocation.getName());
		attemptRecord.setTestLocationId(activation.getLocationId());

		attemptRecord.setCollegeLabel(testLocation.getCollegeName());
		attemptRecord.setCollegeId(testLocation.getCollegeId());

		return attemptRecord;
	}

	private void setOutcomes(AssessmentSession assessmentSession, AttemptRecord attemptRecord) {
		attemptRecord
				.setStudentAbility(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY)));
		attemptRecord.setOddsSuccess(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_ODDS_SUCCESS)));
		attemptRecord.setPointsScored(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_POINTS_SCORE)));
		attemptRecord.setPercentScore(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_PERCENT_SCORE)));
		attemptRecord.setAverageItemDifficulty(
				returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_AVG_ITEM_DIFFICULTY)));
		attemptRecord.setItemDifficultyCount(
				returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT)));
		attemptRecord.setReportedScale(returnOutcomeAsDouble(assessmentSession.getOutcome(Outcome.CAI_REPORTED_SCALE)));
	}

	private Double returnOutcomeAsDouble(Outcome outcome) {
		if (outcome != null

				&& !Double.isNaN(outcome.getValue())) {
			return outcome.getValue();
		}
		return null;
	}

	private Double processResponseRecords(AssessmentSession attempt, PoggioDataStructure data) {

		attempt = assessmentSessionnDao.find(attempt.getAssessmentSessionId());
		double totalScoreForAttempt = 0.0;
		if(attempt.getTaskSets() == null || attempt.getTaskSets().isEmpty()) {
			return totalScoreForAttempt;
		}
		for (TaskSet taskSet : attempt.getTaskSets().values()) {
			for (Task task : taskSet.getTasks()) {
				for (ItemSession itemSession : task.getItemSessions()) {
					PoggioResponseRecord responseRecord = new PoggioResponseRecord();

					PoggioReportStructure structure = data.getStructureByItemId(itemSession.getItemId());

					if (structure == null) {
						log.error(String.format("A structure was not found for the AssessmentItemId:%s or items did not have correct competencyMapDiscipline",itemSession.getItemId()));
						continue;
					}
					responseRecord.setAttemptIndex(itemSession.getItemSessionIndex());
					responseRecord.setOutcomeScore(getScore(itemSession));
					responseRecord.setAttemptId(attempt.getAssessmentSessionId());
					responseRecord.setItemId(itemSession.getItemId());
					responseRecord.setBaseIdentifier(structure.getBaseIdentifier());
					if(responseRecord.getOutcomeScore() != null) {
						totalScoreForAttempt += responseRecord.getOutcomeScore();
					}
					generateResponses(itemSession, responseRecord, structure, data);
					data.addResponseStructure(responseRecord);
				}
			}
		}
		return totalScoreForAttempt;
	}

	private void generateResponses(ItemSession itemSession, PoggioResponseRecord responseRecord,
			PoggioReportStructure structure, PoggioDataStructure data) {
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

	private Character buildResponseAsSingleCharacter(String response, PoggioReportStructure structure,
			PoggioDataStructure data) {
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
		}

		return singleCharacter;
	}

	private PoggioReportStructure getItemStructure(AssessmentItemDto item) {
		PoggioReportStructure structure = new PoggioReportStructure();
		JsonBResponseStructure responseStructure = getItemReponseStructure(item);
		structure.setItemId(item.getId());
		structure.setItemScopedIdentifier(item.getScopedIdTag());
		structure.setResponseStructure(responseStructure);
		structure.setBaseIdentifier(cleanupIdentifier(item.getIdentifier()));
		structure.setItemVersion(item.getVersion());
		structure.setAssessmentItem(item);
		return structure;
	}

	private String cleanupIdentifier(String identifier) {
		String[] identifiers = identifier.split("-");

		if (identifiers.length > 1) {
			StringBuilder builder = new StringBuilder();
			String delimiter = "";
			for (int i = 0; i < identifiers.length - 1; i++) {
				builder.append(delimiter).append(identifiers[i]);
				delimiter = "-";
			}
			return builder.toString();
		}
		return identifiers[0];
	}

	private String buildResponse(Collection<Response> responses, PoggioReportStructure structure) {
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

	private String buildResponseString(Collection<Response> itemResponses, PoggioReportStructure structure) {
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
		List<Activation> activations = activationService.search(parameters);
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
			assessments = assessmentService.read(searchParameters.getAssessmentScopedIdentifier());
			searchParameters.getAssessments().addAll(assessments);
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

		if (StringUtils.isNotBlank(searchParameters.getCompetencyMapDiscipline())) {
			if(StringUtils.isBlank(searchParameters.getPartialIdentifier())) {
				assessments = assessmentService
					.readByCompetencyMapDisicpline(searchParameters.getCompetencyMapDiscipline());
			} else {
				assessments = assessmentService
						.readByCompetencyMapDisicplineOrPartialIdentifier(searchParameters.getCompetencyMapDiscipline(), 
								searchParameters.getPartialIdentifier());
			}
			searchParameters.getAssessments().addAll(assessments);
			if (assessments != null && assessments.size() > 0) {
				List<String> assessmentIds = new ArrayList<String>();
				if (!CollectionUtils.isNullOrEmpty(searchParameters.getContentIds())) {
					assessmentIds.addAll(searchParameters.getContentIds());
				}
				for (AssessmentDto assessment : assessments) {
					assessmentIds.add(assessment.getId());
				}
				searchParameters.setContentIds(assessmentIds);
			}
		}
		return;
	}

	private String getFileNameForAssessmentIdentifier(String assessmentIdentifier) {
		SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_hh_mm_ss");
		return String.format("%s_%s.csv", assessmentIdentifier.replace(":", "_"), sdf.format(new Date()));
	}

	private File getDirectory(String competencyMapDiscipline) {
		SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyy_hh_mm_ss");
		File fileDirectory = new File(reportDirectory.toString() + "/" + competencyMapDiscipline + "_" + sdf.format(new Date()));
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
		FileSystemResource encryptedZip = buildEncryptedZip(reportDirectory,
				searchParameters.getCompetencyMapDiscipline() == null ? "NO_Competency_Map_Given" : searchParameters.getCompetencyMapDiscipline());
		deleteResource(reportDirectory);
		return encryptedZip;
	}

	private FileSystemResource buildEncryptedZip(File reportDirectory, String competencyMapDiscipline) throws IOException {

		List<File> files = Lists.asList(reportDirectory, new File[0]);

		return new FileSystemResource(
				zipfileCompressor.compressFiles(zipfileDirectory, getZipFileName(competencyMapDiscipline), files, password));
	}

	private void deleteResource(File file) throws IOException {
		if (file.isDirectory()) {
			FileUtils.deleteDirectory(file);
		} else {
			FileUtils.deleteQuietly(file);
		}
	}

	private String getZipFileName(String competencyMapDiscipline) {
		SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_hh_mm_ss");
		return String.format("%s_%s.zip", competencyMapDiscipline + "_tesuto_daily_reponses_", sdf.format(new Date()));
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

	class PoggioReportStructure {
		private String itemScopedIdentifier;

		private String itemIdentifier;

		private Integer itemVersion;

		private String baseIdentifier;

		private String itemId;

		private Integer reportOrder;

		private String assessmentScopedId;
		
		private AssessmentItemDto assessmentItem;

		JsonBResponseStructure responseStructure;

		public String getItemScopedIdentifier() {
			return itemScopedIdentifier;
		}

		public void setItemScopedIdentifier(String itemScopedIdentifier) {
			this.itemScopedIdentifier = itemScopedIdentifier;
		}

		public String getItemIdentifier() {
			return itemIdentifier;
		}

		public void setItemIdentifier(String itemIdentifier) {
			this.itemIdentifier = itemIdentifier;
		}

		public Integer getItemVersion() {
			return itemVersion;
		}

		public void setItemVersion(Integer itemVersion) {
			this.itemVersion = itemVersion;
		}

		public String getBaseIdentifier() {
			return baseIdentifier;
		}

		public void setBaseIdentifier(String baseIdentifier) {
			this.baseIdentifier = baseIdentifier;
		}

		public String getItemId() {
			return itemId;
		}

		public void setItemId(String itemId) {
			this.itemId = itemId;
		}

		public Integer getReportOrder() {
			return reportOrder;
		}

		public void setReportOrder(Integer reportOrder) {
			this.reportOrder = reportOrder;
		}

		public String getAssessmentScopedId() {
			return assessmentScopedId;
		}

		public void setAssessmentScopedId(String assessmentScopedId) {
			this.assessmentScopedId = assessmentScopedId;
		}

		public JsonBResponseStructure getResponseStructure() {
			return responseStructure;
		}

		public void setResponseStructure(JsonBResponseStructure responseStructure) {
			this.responseStructure = responseStructure;
		}

		public AssessmentItemDto getAssessmentItem() {
			return assessmentItem;
		}

		public void setAssessmentItem(AssessmentItemDto assessmentItem) {
			this.assessmentItem = assessmentItem;
		}
	}
	
	public class PoggioResponseRecord extends ResponseRecord {
		String baseIdentifier;

		public String getBaseIdentifier() {
			return baseIdentifier;
		}

		public void setBaseIdentifier(String baseIdentifier) {
			this.baseIdentifier = baseIdentifier;
		}
		
	}

	class PoggioDataStructure {
		Map<String, PoggioReportStructure> structuresMapByBaseIdentifier = new HashMap<String, PoggioReportStructure>();
		Map<String, PoggioReportStructure> structureItemIdMap = new HashMap<String, PoggioReportStructure>();
		List<PoggioReportStructure> orderedStructures = new ArrayList<PoggioReportStructure>();

		Map<String, PoggioResponseRecord> responseRecordsByItemId = new HashMap<String, PoggioResponseRecord>();

		void addStructure(PoggioReportStructure structure) {
			if (!structuresMapByBaseIdentifier.containsKey(structure.getBaseIdentifier())) {
				orderedStructures.add(structure);
			}
			structuresMapByBaseIdentifier.put(structure.getBaseIdentifier(), structure);
			structureItemIdMap.put(structure.getItemId(), structure);
		}

		void sort() {
			orderedStructures = orderedStructures.stream()
					.sorted(Comparator.comparing(PoggioReportStructure::getBaseIdentifier))
					.collect(Collectors.toList());
		}

		PoggioReportStructure getStructureByItemId(String itemId) {
			return structureItemIdMap.get(itemId);
		}

		void addResponseStructure(PoggioResponseRecord record) {
			responseRecordsByItemId.put(record.getBaseIdentifier(), record);
		}

		PoggioResponseRecord getResponseRecord(String baseIdentifier) {
			return responseRecordsByItemId.get(baseIdentifier);
		}
		
		void flushResponses() {
			responseRecordsByItemId.clear();
		}
		
	}

}
