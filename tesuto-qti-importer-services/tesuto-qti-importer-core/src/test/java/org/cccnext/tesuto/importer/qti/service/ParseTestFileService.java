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
package org.cccnext.tesuto.importer.qti.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.cccnext.tesuto.importer.service.upload.PackageResults;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentItemService;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentItemService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ParseTestFileService extends BaseUploadService {
    @Resource(name = "assessmentQtiImportService")
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
    QtiImportValidationErrorService qtiImportValidationErrorService;

    @Autowired
    @Qualifier("assessmentCacheService")
    private AssessmentService assessmentService;

    @Autowired
    @Qualifier("assessmentItemCacheService")
    private AssessmentItemService assessmentItemService;

    //Do not autowire.
    private AssessmentSessionReader deliveryService;

    @Autowired
    @Qualifier("resourceRelocatorEncoded")
    private QtiResourceRelocator resourceRelocator;


    private String rootDir = "src/test/resources/";

    @Override
    protected AssessmentItemService getAssessmentItemService() {
        return assessmentItemService;
    }

    @Override
    protected AssessmentService getAssessmentService() {
        return assessmentService;
    }

    @Override
    protected AssessmentSessionReader getDeliveryService() {
        return deliveryService;
    }

    @Override
    protected QtiResourceRelocator getResourceRelocator() {
        return resourceRelocator;
    }

    private PackageResults packageResults;

    final private String fileName;
    final private String parentFolder;
    final private Boolean isXmlFile;

    public ParseTestFileService(String parentFolder,String fileName, Boolean isXmlFile) {
        this.fileName = fileName;
        this.parentFolder = parentFolder;
        this.isXmlFile = isXmlFile;
    }

    public void parseTestFile() throws IOException {
        List<ValidationMessage> errors = new ArrayList<>();
        List<ValidationMessage> warnings = new ArrayList<>();
        String filePath = rootDir + parentFolder + "/" +fileName;
        packageResults = new PackageResults();

        File uploadDirectory = assessmentQtiImportService.getUploadLocation();
        try {
            File sampleQtiFile = moveFileToUniqueDirectory(uploadDirectory, new File(filePath));
            ValidatedNode<ImportFiles> qtiFileSet = assessmentQtiImportService.getResourcesURIsFromFile(uploadDirectory, sampleQtiFile,
                    isXmlFile);
            errors.addAll(qtiFileSet.getErrors());
            warnings.addAll(qtiFileSet.getWarnings());

            packageResults = importService.processFiles(this, qtiFileSet, uploadDirectory, "TEST");
            packageResults.addValidationErrors(errors);
            packageResults.addValidationWarnings(warnings);
        } catch (Exception e){
            packageResults.addValidationErrors(qtiImportValidationErrorService.getErrors(e));
            packageResults.addValidationErrors(errors);
            packageResults.addValidationWarnings(warnings);
        } finally {
            FileUtils.deleteDirectory(uploadDirectory);
        }
    }

    public PackageResults getPackageResults() {
        return packageResults;
    }

    public void setPackageResults(PackageResults packageResults) {
        this.packageResults = packageResults;
    }

    private File moveFileToUniqueDirectory(File uploadDirectory, File source) throws IOException {
        StringBuilder uploadPath = new StringBuilder();
        uploadPath.append(uploadDirectory).append('/').append(fileName.toString());
        final File destination = new File(uploadPath.toString());

        FileUtils.copyFile(source, destination);

        return destination;
    }
}
