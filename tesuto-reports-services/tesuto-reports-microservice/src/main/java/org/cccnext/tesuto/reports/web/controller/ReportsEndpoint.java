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
package org.cccnext.tesuto.reports.web.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cccnext.tesuto.reports.service.ResultsSearchParameters;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.reports.controller.ReportsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/service/v1/reports")
public class ReportsEndpoint extends BaseController {

	@Autowired
	ReportsController controller;
    //Will be shut off for production
    @PreAuthorize("hasAuthority('DOWNLOAD_RESPONSE_REPORT')")
    @RequestMapping(value="download/{daysBefore}", method = RequestMethod.GET)
    public void doDownloadReportZip(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("daysBefore") Integer daysBefore) throws IOException {
    	controller.doDownloadReportZip(request, response, daysBefore);
    }

    //Will be shut off for production
    @PreAuthorize("hasAuthority('DOWNLOAD_STATIC_REPORT')")
    @RequestMapping(value="static/{namespace}/{assessmentIdentifier}", method = RequestMethod.GET)
    public void doDownloadStaticReportZip(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("namespace") String namespace,
            @PathVariable("assessmentIdentifier") String assessmentIdentifier) throws IOException {

    	controller.doDownloadStaticReportZip(request, response, namespace, assessmentIdentifier);
    }

    @PreAuthorize("hasAuthority('STORE_RESPONSE_REPORT')")
    @RequestMapping(value="s3/{daysBefore}", method = RequestMethod.GET)
    public void storeReportToS3(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("daysBefore") Integer daysBefore) throws IOException {

    	controller.storeReportToS3(request, response, daysBefore);

    }

    @PreAuthorize("hasAuthority('DOWNLOAD_RESPONSE_REPORT')")
    @RequestMapping(value="download/{startDate}/{endDate}", method = RequestMethod.GET)
    public void downloadReportByDateRange(HttpServletRequest request,
                                    HttpServletResponse response, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
                                    @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd")  Date endDate,
                                    @RequestParam(value="search", required=false) ResultsSearchParameters.SearchSet searchSet) throws IOException {

    	controller.downloadReportByDateRange(request, response, startDate, endDate, searchSet);

    }

}
