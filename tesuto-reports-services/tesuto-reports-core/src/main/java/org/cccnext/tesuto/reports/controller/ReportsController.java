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
import org.cccnext.tesuto.reports.service.ReportStorage;
import org.cccnext.tesuto.reports.service.ReportsService;
import org.cccnext.tesuto.reports.service.ResultsSearchParameters;
import org.cccnext.tesuto.reports.service.StaticReportService;
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

@Service
public class ReportsController  {

    private static final int BUFFER_SIZE = 4096;

    private static final String MIME_TYPE = "application/zip";

    @Autowired @Qualifier("reportsService")
    private ReportsService reportService;

    @Autowired @Qualifier("staticReportService")
    private StaticReportService staticReportService;

    @Autowired @Qualifier("staticReportStorage")
    private ReportStorage reportStorageService;

    @Value("${report.response.report.s3.path}") private String responseReportS3Path;


    public void doDownloadReportZip(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("daysBefore") Integer daysBefore) throws IOException {

        ResultsSearchParameters searchParameters = new ResultsSearchParameters();

        DateTime completionDateLowerBound = DateTime.now().minusDays(daysBefore).withTimeAtStartOfDay();
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());

        FileSystemResource fileSystemResource = reportService.buildAndStoreReports(searchParameters);

        HttpUtils.getInstance().downloadResource(fileSystemResource.getFile(), response, MIME_TYPE, BUFFER_SIZE);
        deleteResource(fileSystemResource.getFile());
    }


    public void doDownloadStaticReportZip(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("namespace") String namespace,
            @PathVariable("assessmentIdentifier") String assessmentIdentifier) throws IOException {



        FileSystemResource encryptedZip = staticReportService.processResults(namespace, assessmentIdentifier);

        HttpUtils.getInstance().downloadResource(encryptedZip.getFile(), response, MIME_TYPE, BUFFER_SIZE);
        deleteResource(encryptedZip.getFile());
    }

    public void storeReportToS3(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("daysBefore") Integer daysBefore) throws IOException {

        ResultsSearchParameters searchParameters = new ResultsSearchParameters();

        DateTime completionDateLowerBound = DateTime.now().minusDays(daysBefore).withTimeAtStartOfDay();
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
        reportService.clearLock();
        FileSystemResource fileSystemResource = reportService.buildAndStoreReportsWithLock(searchParameters);
        reportService.clearLock();

        reportStorageService.storeFile(responseReportS3Path, fileSystemResource.getFile());
        deleteResource(fileSystemResource.getFile());

    }


    public void downloadReportByDateRange(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
                                    @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd")  Date endDate,
                                    @RequestParam(value="search", required=false) ResultsSearchParameters.SearchSet searchSet) throws IOException {

        ResultsSearchParameters searchParameters = new ResultsSearchParameters();

        if(searchSet != null) {
            searchParameters.setSearchSet(searchSet);
        }

        DateTime completionDateLowerBound = new DateTime(startDate).withTimeAtStartOfDay();
        DateTime completionDateUpperBound = new DateTime(endDate).withTime(23, 59, 59, 999);
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());
        searchParameters.setCompletionDateUpperBound(completionDateUpperBound.toGregorianCalendar().getTime());
        FileSystemResource fileSystemResource = reportService.buildAndStoreReportsWithLock(searchParameters);

        HttpUtils.getInstance().downloadResource(fileSystemResource.getFile(), response, MIME_TYPE, BUFFER_SIZE);

        deleteResource(fileSystemResource.getFile());

    }

    private void deleteResource(File file) throws IOException {
        if(file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        }else {
            FileUtils.deleteQuietly(file);
        }
    }

}
