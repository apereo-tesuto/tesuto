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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.domain.util.ZipFileCompressor;
import org.cccnext.tesuto.exception.TesutoException;
import org.cccnext.tesuto.importer.qti.web.service.RedactionService;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.cccnext.tesuto.util.HttpUtils;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.google.common.collect.Lists;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

@Controller
@RequestMapping(value = "/service/v1/redact/upload")
public class RedactionController extends BaseUploadController {
    
    private static final int BUFFER_SIZE = 4096;

    private static final String DOWNLOAD_MIME_TYPE = "application/zip";
    
    @Autowired
    private ZipFileCompressor zipfileCompressor;
    
    @Value("${report.zip.file.directory}") private String zipfileDirectory;

    @Autowired
    private RedactionService redactionService;
    
    
    
    @Override
    protected String getNamespace() {
        return null;
    }

    @Override
    protected BaseUploadService getUploadService() {
        return redactionService;
    }
    
    @Override
    public UserAccountDto getUser() {
        return null;
    }

    @PreAuthorize("hasAuthority('REDACT_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded", "multipart/form-data"}, produces="application/zip")
    public void uploadMultipartFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model, @ModelAttribute("standaloneRunCommand") @Valid StandaloneRunCommand standaloneRunCommand,
        UriComponentsBuilder uriBuilder)
            throws IOException, URISyntaxException, TesutoException, BadResourceException, XmlResourceNotFoundException,
            NoSuchAlgorithmException, AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        downloadFile(super.uploadMultipartFile( model, standaloneRunCommand, uriBuilder).getAssessmentSessionIds(), response);
    }

    @PreAuthorize("hasAuthority('REDACT_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/octet-stream", "application/zip"}, produces="application/zip")
    public void uploadZipFileStream(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestBody byte[] bytes,  UriComponentsBuilder uriBuilder)
            throws IOException, URISyntaxException, TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {        
        downloadFile(super.uploadZipFileStream(bytes, uriBuilder).getAssessmentSessionIds(), response);
    }

    @PreAuthorize("hasAuthority('REDACT_ASSESSMENT_PACKAGE')")
    @RequestMapping(method = RequestMethod.POST, consumes = "application/xml", produces="application/zip")
    public void uploadXmlFileStream(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestBody byte[] bytes,  UriComponentsBuilder uriBuilder)
            throws IOException, URISyntaxException, TesutoException, BadResourceException, XmlResourceNotFoundException, NoSuchAlgorithmException,
            AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        downloadFile(super.uploadXmlFileStream(bytes, uriBuilder).getAssessmentSessionIds(), response);
    }
    
    String getBaseUrl(HttpServletRequest request) {
        return "";
    }
    
    
    protected ImportResults processFiles(ImportFiles importFiles,
            File uploadDirectory, String namespace) throws AmazonServiceException, NoSuchAlgorithmException,
                    ParserConfigurationException, SAXException, IOException, TransformerException,
                    XmlResourceNotFoundException, BadResourceException, URISyntaxException {
        try {
            try {
                getUploadService().parseAssessmentItemFiles(importFiles.getItems(), uploadDirectory, null, null);
                getUploadService().parseAssessmentFiles(importFiles.getTests(), uploadDirectory, null, null);
            
            } catch (Exception e) {
                 throw e;
            } finally {
            }
            ImportResults results = new ImportResults();
            String[] ursl = {uploadDirectory.getAbsolutePath()};
            results.setAssessmentSessionUrls(ursl);
            return results;
        } catch (Exception e) {
             throw e;
        }
    }
 
    private void downloadFile(List<String> filePaths, HttpServletResponse response) throws IOException {
        File resourceToCompress = new File(filePaths.get(0));
        List<File> files = Lists.asList(resourceToCompress, new File[0]);
        cleanRedactedFiles(files);
        files = Arrays.asList(files.get(0).listFiles());
        File zippedFile =  zipfileCompressor.compressFiles(zipfileDirectory, 
                "redactedUpload.zip", 
                files);
        HttpUtils.getInstance().downloadResource(zippedFile, response, DOWNLOAD_MIME_TYPE, BUFFER_SIZE);
        deleteResource(zippedFile);
        deleteResource(resourceToCompress);
    }
    
    private void cleanRedactedFiles(List<File> files) throws IOException {
        for(File file:files) {
            if(file.isHidden()) {
                deleteResource(file);
                continue;
            }
            
            if(file.getName().startsWith("_") || deleteByFileType(file.getName())) {
                deleteResource(file);
                continue;
            }
            if(file.isDirectory()) {
                if(file.listFiles() != null && file.listFiles().length > 0) {
                    cleanRedactedFiles(Arrays.asList(file.listFiles()));
                }
            }
        }
    }
    
    private boolean deleteByFileType(String fileName) {
        String name = fileName.toLowerCase();
        return (name.endsWith(".zip") 
                || name.endsWith(".jpg") 
                || name.endsWith(".jpeg") 
                || name.endsWith(".gif")
                || name.endsWith(".png")) ? true:false;
    }
    
    private void deleteResource(File file) throws IOException {
        if(file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        }else {
            FileUtils.deleteQuietly(file);
        }
    }
}
