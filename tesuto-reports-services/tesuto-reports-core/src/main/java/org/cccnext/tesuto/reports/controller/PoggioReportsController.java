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
package org.cccnext.tesuto.reports.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.reports.service.PoggioReportsService;
import org.cccnext.tesuto.reports.service.PoggioStaticReportService;
import org.cccnext.tesuto.reports.service.ReportStorage;
import org.cccnext.tesuto.reports.service.ResultsSearchParameters;
import org.cccnext.tesuto.util.HttpUtils;
import org.joda.time.DateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PoggioReportsController {

	private static final int BUFFER_SIZE = 4096;

	private static final String MIME_TYPE = "application/zip";

	@Autowired
	@Qualifier("poggioReportsService")
	private PoggioReportsService reportService;

	@Autowired
	@Qualifier("poggioStaticReportService")
	private PoggioStaticReportService staticReportService;

	@Autowired
	@Qualifier("staticReportStorage")
	private ReportStorage reportStorageService;

	@Value("${report.poggio.response.report.s3.path}")
	private String responseReportS3Path;

	public void doDownloadReportZip(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("daysBefore") Integer daysBefore) throws IOException {

		ResultsSearchParameters searchParameters = new ResultsSearchParameters();

		DateTime completionDateLowerBound = DateTime.now().minusDays(daysBefore).withTimeAtStartOfDay();
		searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());

		FileSystemResource fileSystemResource = reportService.buildAndStoreReports(searchParameters);

		HttpUtils.getInstance().downloadResource(fileSystemResource.getFile(), response, MIME_TYPE, BUFFER_SIZE);
		deleteResource(fileSystemResource.getFile());
	}

	public void doDownloadStaticReportZip(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("competencyMapDiscipline") String competencyMapDiscipline,
			@PathVariable("identifierRegEx") String identifierRegEx) throws IOException {

		log.info(String.format("identifierRegEx: %s", identifierRegEx));
		FileSystemResource encryptedZip = staticReportService.processResults(competencyMapDiscipline, identifierRegEx);

		HttpUtils.getInstance().downloadResource(encryptedZip.getFile(), response, MIME_TYPE, BUFFER_SIZE);
		deleteResource(encryptedZip.getFile());
	}
	
	public void doStoreStaticReportZip(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("competencyMapDiscipline") String competencyMapDiscipline,
			@PathVariable("identifierRegEx") String identifierRegEx) throws IOException {

		FileSystemResource encryptedZip = staticReportService.processResults(competencyMapDiscipline, identifierRegEx);

		reportStorageService.storeFile(responseReportS3Path, encryptedZip.getFile());
		deleteResource(encryptedZip.getFile());
	}


	public void storeReportToS3(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("daysBefore") Integer daysBefore) throws IOException {

		ResultsSearchParameters searchParameters = new ResultsSearchParameters();

		DateTime completionDateLowerBound = DateTime.now().minusDays(daysBefore).withTimeAtStartOfDay();
		searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
		FileSystemResource fileSystemResource = reportService.buildAndStoreReports(searchParameters);

		reportStorageService.storeFile(responseReportS3Path, fileSystemResource.getFile());
		deleteResource(fileSystemResource.getFile());

	}

	public void storeReportToS3DateRange(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("competencyMapDiscipline") String competencyMapDiscipline,
			@PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@PathVariable("identifierRegEx") String identifierRegEx,
			@RequestParam(value = "search", required = false) ResultsSearchParameters.SearchSet searchSet,
			@RequestParam(value = "includeUnaffiliatedItems", required = false) Boolean includeUnaffiliatedItems)
			throws IOException {

		ResultsSearchParameters searchParameters = new ResultsSearchParameters();
		
		if (searchSet != null) {
			searchParameters.setSearchSet(searchSet);
		} else {
			searchParameters.setSearchSet(ResultsSearchParameters.SearchSet.COMPLETED);
		}

		searchParameters.setPartialIdentifier(identifierRegEx);
		DateTime completionDateLowerBound = new DateTime(startDate).withTimeAtStartOfDay();
		DateTime completionDateUpperBound = new DateTime(endDate).withTime(23, 59, 59, 999);
		searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
		searchParameters.setCompletionDateUpperBound(completionDateUpperBound.toGregorianCalendar().getTime());

		searchParameters.setCompetencyMapDiscipline(competencyMapDiscipline);
		searchParameters.setIncludeUnaffiliatedItems(includeUnaffiliatedItems);
		FileSystemResource fileSystemResource = reportService.buildAndStoreReports(searchParameters);

		reportStorageService.storeFile(responseReportS3Path, fileSystemResource.getFile());
		deleteResource(fileSystemResource.getFile());

	}

	public void downloadReportByDateRange(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("competencyMapDiscipline") String competencyMapDiscipline,
			@PathVariable("identifierRegEx") String identifierRegEx,
			@PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam(value = "search", required = false) ResultsSearchParameters.SearchSet searchSet,
			@RequestParam(value = "includeUnaffiliatedItems", required = false) Boolean includeUnaffiliatedItems)
			throws IOException {

		ResultsSearchParameters searchParameters = new ResultsSearchParameters();
		searchParameters.setPartialIdentifier(identifierRegEx);

		if (searchSet != null) {
			searchParameters.setSearchSet(searchSet);
		} else {
			searchParameters.setSearchSet(ResultsSearchParameters.SearchSet.COMPLETED);
		}

		DateTime completionDateLowerBound = new DateTime(startDate).withTimeAtStartOfDay();
		DateTime completionDateUpperBound = new DateTime(endDate).withTime(23, 59, 59, 999);
		searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
		searchParameters.setCompletionDateUpperBound(completionDateUpperBound.toGregorianCalendar().getTime());
		searchParameters.setCompetencyMapDiscipline(competencyMapDiscipline);
		searchParameters.setIncludeUnaffiliatedItems(includeUnaffiliatedItems);
		
		FileSystemResource fileSystemResource = reportService.buildAndStoreReports(searchParameters);

		HttpUtils.getInstance().downloadResource(fileSystemResource.getFile(), response, MIME_TYPE, BUFFER_SIZE);

		deleteResource(fileSystemResource.getFile());

	}

	private void deleteResource(File file) throws IOException {
		if (file.isDirectory()) {
			FileUtils.deleteDirectory(file);
		} else {
			FileUtils.deleteQuietly(file);
		}
	}

}
