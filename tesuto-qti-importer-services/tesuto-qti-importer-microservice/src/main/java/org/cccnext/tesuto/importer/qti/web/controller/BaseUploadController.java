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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.exception.TesutoException;
import org.cccnext.tesuto.importer.qti.service.AssessmentQtiImportService;
import org.cccnext.tesuto.importer.qti.service.QtiImportValidationErrorService;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.cccnext.tesuto.importer.service.upload.PackageResults;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentItemService;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentItemService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentService;
import org.cccnext.tesuto.service.importer.validate.ValidateItemMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

@Slf4j
public abstract class BaseUploadController extends BaseController {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    @Autowired
    AssessmentQtiImportService assessmentQtiImportService;

    @Autowired
    ImportService importService;

    @Autowired
    ValidateAssessmentService validateAssessmentService;

    @Autowired
    ValidateAssessmentItemService validateAssessmentItemService;

    @Autowired
    NormalizeAssessmentItemService normalizeAssessmentItemService;

    @Autowired
    NormalizeAssessmentMetadataService normalizeAssessmentMetadataService;

    @Autowired
    ValidateAssessmentMetadataService validateAssessmentMetadataService;

    @Autowired
    ValidateItemMetadataService validateItemMetadataService;

    @Autowired
    CompetencyMapDisciplineService competencyMapDisciplineService;

    @Autowired
    QtiImportValidationErrorService qtiImportValidationErrorService;



    protected abstract String getNamespace();

    protected abstract BaseUploadService getUploadService();

    protected ImportResults uploadMultipartFile(Model model, StandaloneRunCommand standaloneRunCommand,
            UriComponentsBuilder uriBuilder) throws IOException, URISyntaxException, TesutoException,
                    BadResourceException, NoSuchAlgorithmException, XmlResourceNotFoundException,
                    AmazonServiceException, ParserConfigurationException, SAXException, TransformerException {
        File uploadDirectory = assessmentQtiImportService.getUploadLocation();

        ValidatedNode<ImportFiles> validatedQtiFiles = getFilesFromStandaloneCommand(standaloneRunCommand, uploadDirectory);

        return processFiles(validatedQtiFiles, uploadDirectory, getNamespace());
    }

    protected ImportResults uploadZipFileStream(byte[] bytes, UriComponentsBuilder uriBuilder)
            throws IOException, URISyntaxException, TesutoException, BadResourceException, NoSuchAlgorithmException,
            AmazonServiceException, TransformerException, SAXException, ParserConfigurationException, XmlResourceNotFoundException {
        File uploadDirectory = assessmentQtiImportService.getUploadLocation();
        ValidatedNode<ImportFiles> validatedQtiFiles = getFilesFromByteStream(bytes, uploadDirectory, false);

        return  processFiles(validatedQtiFiles, uploadDirectory, getNamespace());
    }

    protected ImportResults uploadXmlFileStream(byte[] bytes, UriComponentsBuilder uriBuilder)
            throws IOException, URISyntaxException, TesutoException, BadResourceException,
            XmlResourceNotFoundException, NoSuchAlgorithmException, AmazonServiceException, SAXException,
            TransformerException, ParserConfigurationException {
        File uploadDirectory = assessmentQtiImportService.getUploadLocation();
        ValidatedNode<ImportFiles> validatedQtiFiles = getFilesFromByteStream(bytes, uploadDirectory, true);

        return processFiles(validatedQtiFiles, uploadDirectory, getNamespace());
    }

    private ValidatedNode<ImportFiles> getFilesFromStandaloneCommand(StandaloneRunCommand standaloneRunCommand,
            File uploadDirectory) throws AmazonServiceException, NoSuchAlgorithmException,
            IOException, TransformerException, URISyntaxException {

        File uploadFile = getImportFileFromStandaloneCommand(standaloneRunCommand, uploadDirectory);
        Boolean isXmlFile = isXmlFile(standaloneRunCommand);
        ValidatedNode<ImportFiles> validatedQtiFiles = assessmentQtiImportService.getResourcesURIsFromFile(uploadDirectory,
                uploadFile, isXmlFile);
        return validatedQtiFiles;
    }

    private ValidatedNode<ImportFiles> getFilesFromByteStream(byte[] bytes, File uploadDirectory, Boolean isXmlFile)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException, TransformerException, URISyntaxException {
        File uploadFile = getImportFileFromBytes(bytes, uploadDirectory, isXmlFile);
       ValidatedNode<ImportFiles> validatedQtiFiles = assessmentQtiImportService.getResourcesURIsFromFile(uploadDirectory,
                uploadFile, isXmlFile);
        return validatedQtiFiles;
    }

    protected ImportResults addUrlsToImportResults(ImportResults results, String context) {
        String[] urls = getAssessmentSessionUrls(results.getAssessmentSessionIds(),  context);
        results.setAssessmentSessionUrls(urls);
        return results;
    }

    protected String[] getAssessmentSessionUrls(List<String> assessmentSessions, String context) {
        if(CollectionUtils.isEmpty(assessmentSessions)){
            return null;
        }
        String[] assessmentSessionUrls = new String[assessmentSessions.size()];
        int i = 0;
        for (String assessmentSession : assessmentSessions) {
            assessmentSessionUrls[i++] = context + assessmentSession;
        }
        return assessmentSessionUrls;
    }

    private boolean isXmlFile(StandaloneRunCommand standaloneRunCommand) {
        final String contentType = computeContentType(standaloneRunCommand);
        if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
            return true;
        }
        return false;
    }

    public String computeContentType(final StandaloneRunCommand standaloneRunCommand) {
        String result = standaloneRunCommand.getFile().getContentType();
        if (result == null) {
            result = standaloneRunCommand.getContentType();
            if (result == null)
                result = DEFAULT_CONTENT_TYPE;
        }
        return result;
    }

    private File getImportFileFromStandaloneCommand(StandaloneRunCommand standaloneRunCommand, File uploadDirectory)
            throws IOException, AmazonServiceException, NoSuchAlgorithmException, URISyntaxException {

        MultipartFile upload = standaloneRunCommand.getFile();

        StringBuilder uploadPath = new StringBuilder();
        uploadPath.append(uploadDirectory).append('/').append(upload.getOriginalFilename());
        final File uploadFile = new File(uploadPath.toString());
        FileOutputStream fileOutputStream = new FileOutputStream(uploadFile);
        fileOutputStream.write(upload.getBytes());
        fileOutputStream.close();
        return uploadFile;
    }

    protected File getImportFileFromBytes(byte[] inputStream, File uploadDirectory, Boolean isXmlFile)
            throws IOException, AmazonServiceException, NoSuchAlgorithmException, URISyntaxException {

        StringBuilder uploadPath = new StringBuilder();
        String fileName = "uploadFile";
        if (isXmlFile) {
            fileName += ".xml";
        } else {
            fileName += ".zip";
        }
        uploadPath.append(uploadDirectory).append('/').append(fileName);
        final File uploadFile = new File(uploadPath.toString());
        FileOutputStream fileOutputStream = new FileOutputStream(uploadFile);
        fileOutputStream.write(inputStream);
        fileOutputStream.close();

        return uploadFile;
    }

    protected ImportResults processFiles(ValidatedNode<ImportFiles> validatedImportFiles,
            File uploadDirectory, String namespace) throws AmazonServiceException, NoSuchAlgorithmException,
                    IOException, TransformerException, BadResourceException, URISyntaxException, SAXException,
                    ParserConfigurationException, XmlResourceNotFoundException {
        ImportResults assessmentSessions = null;
        List<ValidationMessage> validationErrors = validatedImportFiles.getErrors();
        List<ValidationMessage> validationWarnings = validatedImportFiles.getWarnings();
        try {
            PackageResults packageResults = importService.processFiles(getUploadService(), validatedImportFiles, uploadDirectory, namespace);
            validationWarnings.addAll(packageResults.getValidationWarnings());
            validationErrors.addAll(packageResults.getValidationErrors());

            if (CollectionUtils.isNotEmpty(validationErrors)) {
                ImportResults results = new ImportResults();
                results.addValidationErrors(validationErrors);
                results.addValidationWarnings(validationWarnings);
                return results;
            }

            List<AssessmentItemDto> storedAssessmentItems = getUploadService().storeAssessmentItems(packageResults.getAssessmentItemDtos());
            getUploadService().storeAssessments(packageResults.getAssessmentDtos());

            String userAccountId = getUser().getUserAccountId().toString();

            assessmentSessions = getUploadService().createAssessmentSessions(packageResults.getAssessmentDtos(), storedAssessmentItems,
                    namespace, userAccountId);
            assessmentSessions.addValidationWarnings(validationWarnings);
        } catch (Exception e){
            e.printStackTrace();
            log.error("Failed to process files {}", e);
            ImportResults results = new ImportResults();
            results.addValidationErrors(validationErrors);
            results.addValidationWarnings(validationWarnings);
            results.addValidationErrors(qtiImportValidationErrorService.getErrors(e));
            return results;
        } finally {
            FileUtils.deleteDirectory(uploadDirectory);
        }

        return assessmentSessions;
    }




    //TODO handle printing from upload/preview controller
    protected ImportResults addPermissions(HttpSession session, ImportResults assessmentSessions) {
        if(assessmentSessions.getAssessmentSessionIds()  != null) {
            assessmentSessions.getAssessmentSessionIds().forEach( assessmentSession ->
                    TesutoUtil.addAssessmentSessionPermission(session, assessmentSession, DeliveryType.ONLINE)
            );
        }
        return assessmentSessions;
    }

    protected ResponseEntity<ImportResults> getResponse(ImportResults importResults){
        if(importResults.hasValidationErrors()){
            return new ResponseEntity<>(importResults, HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(importResults, HttpStatus.CREATED);
        }
    }
}
