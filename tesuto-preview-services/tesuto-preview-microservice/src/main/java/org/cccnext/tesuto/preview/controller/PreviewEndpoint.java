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
package org.cccnext.tesuto.preview.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.exception.TesutoException;
import org.cccnext.tesuto.importer.qti.service.AssessmentQtiImportService;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.cccnext.tesuto.preview.service.CacheUploadService;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/service/v1/preview/upload")
public class PreviewEndpoint extends BaseUploadController {
    @Autowired
    AssessmentQtiImportService assessmentQtiImportService;

    @Autowired
    private CacheUploadService uploadService;

    static final String PREVIEW_NAMESPACE = "preview";

    static final int PREVIEW_VERSION = 0;

    static final String PREVIEW_PATH = "/preview/assessment/";

    @Override
    protected BaseUploadService getUploadService() {
        return uploadService;
    }

    @Override
    protected String getNamespace() {
        return PREVIEW_NAMESPACE;
    }

    @Override
    public UserAccountDto getUser() {
        UserAccountDto userAccount = new UserAccountDto();
        userAccount.setDisplayName("Preview Session");
        userAccount.setUsername(TesutoUtils.newId());
        userAccount.setUserAccountId("previewer");
        userAccount.setEnabled(false);
        userAccount.setAccountLocked(false);
        return userAccount;
    }

    // Note: We're dynamically injecting the value from configuration on startup.  Annotation scanning
    // happens to early in the process, so this is defined in the security.xml
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded",
            "multipart/form-data" }, produces = "application/json")
    public ResponseEntity<ImportResults> uploadMultipartFile(HttpSession session, HttpServletRequest request, Model model,
            @ModelAttribute("standaloneRunCommand") @Valid StandaloneRunCommand standaloneRunCommand,
            UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException, TesutoException,
            BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException, AmazonServiceException,
            ParserConfigurationException, SAXException, TransformerException {
        ImportResults importResults = addUrlsToImportResults(
                addPermissions(session, super.uploadMultipartFile(model, standaloneRunCommand, uriBuilder)),
                getBaseUrl(request));
        return getResponse(importResults);
    }

    // Note: We're dynamically injecting the value from configuration on startup.  Annotation scanning
    // happens to early in the process, so this is defined in the security.xml
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/octet-stream",
            "application/zip" }, produces = "application/json")
    public ResponseEntity<ImportResults> uploadZipFileStream(HttpSession session, HttpServletRequest request,
            @RequestBody byte[] bytes, UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException,
            TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        ImportResults importResults = addUrlsToImportResults(addPermissions(session, super.uploadZipFileStream(bytes, uriBuilder)),
                getBaseUrl(request));
        return getResponse(importResults);
    }

    // Note: We're dynamically injecting the value from configuration on startup.  Annotation scanning
    // happens to early in the process, so this is defined in the security.xml
    @RequestMapping(method = RequestMethod.POST, consumes = "application/xml", produces = "application/json")
    public ResponseEntity<ImportResults> uploadXmlFileStream(HttpSession session, HttpServletRequest request,
            @RequestBody byte[] bytes, UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException,
            TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        ImportResults importResults =  addUrlsToImportResults(addPermissions(session, super.uploadXmlFileStream(bytes, uriBuilder)),
                getBaseUrl(request));
        return getResponse(importResults);
    }

    String getBaseUrl(HttpServletRequest request) {
        return getBaseURL(request) + PREVIEW_PATH;
    }

}
