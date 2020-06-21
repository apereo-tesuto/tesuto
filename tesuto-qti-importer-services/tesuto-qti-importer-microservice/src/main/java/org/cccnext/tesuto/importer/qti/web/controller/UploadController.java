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
package org.cccnext.tesuto.importer.qti.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.exception.TesutoException;
import org.cccnext.tesuto.importer.qti.web.service.UploadService;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;


@Controller
@RequestMapping(value = "/service/v1/upload")
public class UploadController extends BaseUploadController {
	
    @Autowired
    private UploadService uploadService;
    
    @Value("${tesuto.import.test.assessment.session.url}")
    String importTestAssessmentSessionUrl;
    
    @Value("${tesuto.import.view.competency.order.url}")
    String importViewCompletencyOrderUrl;

    @Override
    protected BaseUploadService getUploadService() {
        return uploadService;
    }

    @Override
    protected String getNamespace() {
        UserAccountDto userAccountDto = getUser();
        String namespace = userAccountDto.getNamespace();
        return namespace;
    }

    @PreAuthorize("hasAuthority('UPLOAD_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded",
            "multipart/form-data" }, produces = "application/json")
    public ResponseEntity<ImportResults> uploadMultipartFile(HttpSession session, HttpServletRequest request, Model model,
            @ModelAttribute("standaloneRunCommand") @Valid StandaloneRunCommand standaloneRunCommand,
            UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException, TesutoException,
            BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException, AmazonServiceException,
            ParserConfigurationException, TransformerException, SAXException {
        ImportResults importResults = addUrlsToImportResults(
                addPermissions(session, super.uploadMultipartFile(model, standaloneRunCommand, uriBuilder)),
                request);
        return getResponse(importResults);
    }

    // TODO: Is this ever used?
    @PreAuthorize("hasAuthority('UPLOAD_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/octet-stream",
            "application/zip" }, produces = "application/json")
    public ResponseEntity<ImportResults> uploadZipFileStream(HttpSession session, HttpServletRequest request,
            @RequestBody byte[] bytes, UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException,
            TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        ImportResults importResults =  addUrlsToImportResults(addPermissions(session, super.uploadZipFileStream(bytes, uriBuilder)),
                request);
        return getResponse(importResults);
    }

    // TODO: Is this ever used?
    @PreAuthorize("hasAuthority('UPLOAD_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = "application/xml", produces = "application/json")
    public ResponseEntity<ImportResults> uploadXmlFileStream(HttpSession session, HttpServletRequest request,
            @RequestBody byte[] bytes, UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException,
            TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        ImportResults importResults =   addUrlsToImportResults(addPermissions(session, super.uploadXmlFileStream(bytes, uriBuilder)),
                request);
        return getResponse(importResults);
    }
    
    
    protected ImportResults addUrlsToImportResults(ImportResults results, HttpServletRequest request) {
        addUrlsToImportResults(results, importTestAssessmentSessionUrl);
        Map<String, String> urls = buildCompetencyMapOrderUrls(results.getImportedCompetencyMapOrderIds(),  request);
        results.setCompetencyMapOrderUrls(urls);
        return results;
    }

    
    private  Map<String, String> buildCompetencyMapOrderUrls(Map<String, String> competencyMapOrders, HttpServletRequest request) {
        Map<String, String> competencyMapOrderUrls = null;
        if(competencyMapOrders != null && competencyMapOrders.size() > 0) {
            for(String key:competencyMapOrders.keySet()) {
                if(StringUtils.isNotEmpty(competencyMapOrders.get(key))) { 
                   if(competencyMapOrderUrls == null) {
                       competencyMapOrderUrls = new HashMap<String, String>();
                   }
                   competencyMapOrderUrls.put(key, importViewCompletencyOrderUrl + "/" + competencyMapOrders.get(key));
                }
            }
        }
        return competencyMapOrderUrls;
    }

}
