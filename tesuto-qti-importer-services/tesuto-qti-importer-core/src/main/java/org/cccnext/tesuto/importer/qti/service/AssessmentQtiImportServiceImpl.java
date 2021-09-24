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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.domain.util.ZipFileExtractor;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;
import org.cccnext.tesuto.util.TesutoUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ContentPackageResource;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageSummary;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "assessmentQtiImportService")
public class AssessmentQtiImportServiceImpl implements AssessmentQtiImportService {

    @Autowired
    private QtiXmlReader qtiXmlReader;
    @Autowired
    private QtiResourceLocator qtiResourceLocator;

    @Autowired
    private QtiAssessmentTestService qtiAssessmentService;
    @Autowired
    private QtiAssessmentItemService qtiaAssessmentItemService;

    @Autowired
    private ImportService importService;

    @Autowired
    ZipFileExtractor zipFileExtractor;

    @Autowired
    private QtiImportValidationErrorService qtiImportValidationErrorService;


    private static final String FILE_TYPE_XML = ".xml";
    private static final String MANIFEST_FILE_NAME = "imsmanifest";
    private static final String ITEM_METADATA = "itemmetadata";
    private static final String ASSESSMENT_METADATA = "assessmentmetadata";
    private static final String IDENTIFIER = "identifier";

    @Override
    public File getUploadLocation() {
        return importService.getUploadLocation();
    }

    @Override
    public ValidatedNode<AssessmentDto> parseAssessmentResource(URI assessmentTestSystemId, String namespace, int version)
            throws BadResourceException {
        List<ValidationMessage> errors = new ArrayList<>();
        AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader,
                qtiResourceLocator);

        AssessmentObjectValidationResult<AssessmentTest> assessmentObjectValidationResult = assessmentObjectXmlLoader
                .loadResolveAndValidateTest(assessmentTestSystemId);

        ValidatedNode<AssessmentObjectValidationResult> validatedAssessmentObjectValidationResult = qtiImportValidationErrorService.getErrorsForValidationResult(((TestValidationResult) assessmentObjectValidationResult));

        ResolvedAssessmentObject<AssessmentTest> resolvedAssessmentObject = assessmentObjectValidationResult
                .getResolvedAssessmentObject();
        if(assessmentObjectValidationResult.getResolvedAssessmentObject().getRootNodeLookup().getRootNodeHolder() == null){
            BadResourceException badResourceException = assessmentObjectValidationResult.getResolvedAssessmentObject().getRootNodeLookup().getBadResourceException();
            log.error("Failed to parse URI {} ", assessmentTestSystemId);
            throw badResourceException;
        }
        ResolvedAssessmentTest resolvedAssessmentTest = (ResolvedAssessmentTest) resolvedAssessmentObject;
        AssessmentTest assessmentTest = resolvedAssessmentTest.getRootNodeLookup().extractIfSuccessful();
        AssessmentDto assessmentDto = qtiAssessmentService.parse(assessmentTest);

        assessmentDto.setNamespace(namespace);
        assessmentDto.setVersion(version);
        assessmentDto.setId(TesutoUtils.newId());
        updateAssessmentItemRefs(assessmentDto, assessmentTestSystemId);

        errors.addAll(validatedAssessmentObjectValidationResult.getErrors());

        return new ValidatedNode<>(errors, validatedAssessmentObjectValidationResult.getWarnings(), assessmentDto);
    }

    @Override
    public ValidatedNode<AssessmentItemDto>parseAssessmentItemResource(URI assessmentObjectSystemId, String namespace, int version)
            throws AmazonServiceException, NoSuchAlgorithmException, TransformerException, BadResourceException {
        AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader,
                qtiResourceLocator);

        AssessmentObjectValidationResult<AssessmentItem> assessmentObjectValidationResult = assessmentObjectXmlLoader
                .loadResolveAndValidateItem(assessmentObjectSystemId);
        ValidatedNode<AssessmentObjectValidationResult> validatedAssessmentObjectValidationResult = qtiImportValidationErrorService.getErrorsForValidationResult(((ItemValidationResult) assessmentObjectValidationResult));

        if(assessmentObjectValidationResult.getResolvedAssessmentObject().getRootNodeLookup().getRootNodeHolder() == null){
            BadResourceException badResourceException = assessmentObjectValidationResult.getResolvedAssessmentObject().getRootNodeLookup().getBadResourceException();
            log.error("Failed to parse URI {} ", assessmentObjectSystemId);
            throw badResourceException;
        }
        AssessmentItem assessmentItem = assessmentObjectValidationResult.getResolvedAssessmentObject()
                .getRootNodeLookup().getRootNodeHolder().getRootNode();
        AssessmentItemDto assessmentItemDto = qtiaAssessmentItemService.parse(assessmentItem);
        assessmentItemDto.setNamespace(namespace);
        assessmentItemDto.setVersion(version);
        return new ValidatedNode<>(validatedAssessmentObjectValidationResult.getErrors(), validatedAssessmentObjectValidationResult.getWarnings(), assessmentItemDto);

    }



    @Override
    public ValidatedNode<ImportFiles> getResourcesURIsFromFile(final File uploadLocation, File uploadFile, Boolean isXmlFile)
            throws URISyntaxException, AmazonServiceException, NoSuchAlgorithmException, IOException {

        ImportFiles importFiles = new ImportFiles();
        List<ValidationMessage> errors = new ArrayList<>();
        List<ValidationMessage> warnings = new ArrayList<>();

        List<URI> unzippedFiles = null;
        if (!isXmlFile) {
            FileInputStream fileInputStream = new FileInputStream(uploadFile);
            unzippedFiles = zipFileExtractor.extract(uploadLocation, fileInputStream);
        } else {
            unzippedFiles = Arrays.asList(uploadFile.toURI());
        }

        if (hasIMSManifest(unzippedFiles)) {
            try {
                URI imsManifestFile = getIMSManifest(unzippedFiles);

                ValidatedNode<Boolean> validateIMSManifestSchema = importService.verifySchema(imsManifestFile.getPath());
                if(!validateIMSManifestSchema.getValue()) {
                    errors.addAll(validateIMSManifestSchema.getErrors());
                    warnings.addAll(validateIMSManifestSchema.getWarnings());
                }

                final QtiContentPackageExtractor contentPackageExtractor = new QtiContentPackageExtractor(uploadFile);
                // This is where the schema for the ims manifest file should be checked.
                QtiContentPackageSummary contentPackageSummary = contentPackageExtractor.parse();
                importFiles.addTests(
                        getURIsContentPackageResources(contentPackageSummary.getTestResources(), uploadLocation));
                importFiles.addItems(
                        getURIsContentPackageResources(contentPackageSummary.getItemResources(), uploadLocation));
                errors.addAll(getMetadataResources(uploadLocation, contentPackageExtractor, importFiles));
            } catch (Exception exception) {
                errors.addAll(qtiImportValidationErrorService.getErrors(exception));
                ValidatedNode<ImportFiles> validatedImportFiles = importService.searchForXmlFiles(unzippedFiles, importFiles);
                errors.addAll(validatedImportFiles.getErrors());
                importFiles = validatedImportFiles.getValue();

            }
        } else {
            // TODO may want to remove this functionality from the persisted upload. We really just want to all single item upload as a preview option
            // resources that do not XML parse in the absence of the imsmanifest.xml file.
            ValidatedNode<ImportFiles> validatedImportFiles  = importService.searchForXmlFiles(unzippedFiles, importFiles);
            errors.addAll(validatedImportFiles.getErrors());
            importFiles = validatedImportFiles.getValue();
        }

        return new ValidatedNode<>(errors, warnings, importFiles);

    }

    @Override
    public ValidatedNode<HashMap<String, AssessmentMetadataDto>> getMetadataFromResources(List<URI> metaDataUris) throws SAXException,
            IOException, ParserConfigurationException {
        HashMap<String, AssessmentMetadataDto> map = new HashMap<String, AssessmentMetadataDto>();
        List<ValidationMessage> errors = new ArrayList<>();
        for (URI metadataURI : metaDataUris) {
            File file = new File(metadataURI);
            ValidatedNode<String> validatedType = importService.getFileType(file);
            errors.addAll(validatedType.getErrors());
            String id = importService.getTextContentByTagName(file, IDENTIFIER);
            if(!map.containsKey(id)) {
                map.put(id, getAssessmentMetadata(file, validatedType.getValue()));
            }else{
                log.warn(String.format("The file %s contains a duplicate AssessmentMetadata identifier: %s", file, id));
            }
        }

        return new ValidatedNode<>(errors, map);
    }

    @Override
    public ValidatedNode<HashMap<String, ItemMetadataDto>> getItemMetadataFromResources(List<URI> metaDataUris) throws SAXException,
            IOException, ParserConfigurationException {
        HashMap<String, ItemMetadataDto> map = new HashMap<String, ItemMetadataDto>();
        List<ValidationMessage> errors = new ArrayList<>();
        for (URI metadataURI : metaDataUris) {
            File file = new File(metadataURI);
            ValidatedNode<String> validatedType = importService.getFileType(file);
            errors.addAll(validatedType.getErrors());
            String id = importService.getTextContentByTagName(file, IDENTIFIER);
            if(!map.containsKey(id)) {
                map.put(id, getItemMetadata(file, validatedType.getValue()));
            }else{
                log.warn(String.format("The file %s contains a duplicate ItemMetadata identifier: %s", file, id));
            }
        }

        return new ValidatedNode<>(errors, map);
    }

    private ItemMetadataDto getItemMetadata(final File uploadLocation, String type) throws IOException {
        ItemMetadataDto itemMetadata = new ItemMetadataDto();
        InputStream inputStream = new FileInputStream(uploadLocation);
        XmlMapper xmlMapper = initializeXmlMapper();
        if (ITEM_METADATA.equals(type)) {
            itemMetadata = xmlMapper.readValue(inputStream, ItemMetadataDto.class);
            itemMetadata.setType(ITEM_METADATA);
        }
        return itemMetadata;
    }

    private AssessmentMetadataDto getAssessmentMetadata(final File uploadLocation, String type) throws IOException {
        AssessmentMetadataDto assessmentMetadata = new AssessmentMetadataDto();
        InputStream inputStream = new FileInputStream(uploadLocation);
        XmlMapper xmlMapper = initializeXmlMapper();
        if (ASSESSMENT_METADATA.equals(type)) {
            assessmentMetadata = xmlMapper.readValue(inputStream, AssessmentMetadataDto.class);
            assessmentMetadata.setType(ASSESSMENT_METADATA);
        }
        return assessmentMetadata;
    }

    private XmlMapper initializeXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        xmlMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return xmlMapper;
    }

    private List<ValidationMessage> getMetadataResources(File uploadLocation, QtiContentPackageExtractor contentPackageExtractor,
            ImportFiles importFiles) {
        List<ValidationMessage> validationMessages = new ArrayList<>();
        try {
            QtiContentPackageSummary contentPackageSummary = contentPackageExtractor.parse();
            for (ContentPackageResource contentPackageResource : contentPackageSummary.getPackageManifestDetails().getResourceList()) {
                for (URI uri : contentPackageResource.getFileHrefs()) {
                    if (uri.getPath().endsWith(FILE_TYPE_XML) && !uri.getPath().contains(MANIFEST_FILE_NAME)) {
                        StringBuilder absolutePath = new StringBuilder(uploadLocation.toURI().toString());
                        absolutePath.append(uri.getPath());
                        URI pathToFile = URI.create(absolutePath.toString());
                        File file = new File(pathToFile);
                        ValidatedNode<String> validatedType = importService.getFileType(file);
                        validationMessages.addAll(validatedType.getErrors());
                        if (ITEM_METADATA.equals(validatedType.getValue())) {
                            importFiles.getItemsMetadata().add(pathToFile);
                        } else if (ASSESSMENT_METADATA.equals(validatedType.getValue())) {
                            importFiles.getTestsMetadata().add(pathToFile);
                        }
                    }
                }
            }
        }catch(Exception e){
            validationMessages.addAll(qtiImportValidationErrorService.getErrors(e));
        }
        return validationMessages;
    }

    @Override
    public void updateAssessmentItemRefs(AssessmentDto assessmentDto, URI assessmentURI) {
        for (AssessmentPartDto part : assessmentDto.getAssessmentParts()) {
            Path pathToAssessment = Paths.get(assessmentURI);
            Path pathToAssessmentDirectory = Paths.get(pathToAssessment.toFile().getParentFile().toURI());
            if (CollectionUtils.isNotEmpty(part.getAssessmentSections())) {
                for (AssessmentSectionDto section : part.getAssessmentSections()) {
                    updateAssessmentItemsRefsForSection(section, pathToAssessmentDirectory);
                }
            }
        }
    }

    private void updateAssessmentItemsRefsForSection(AssessmentSectionDto section, Path pathToAssessmentDirectory) {
        if (section == null) {
            return;
        }
        for (AssessmentItemRefDto assessmentItemRef : section.getAssessmentItemRefs()) {
            String href = assessmentItemRef.getItemIdentifier();
            Path pathToItemXml = pathToAssessmentDirectory.resolve(href);
            File assessmentItemFile = pathToItemXml.toFile();
            String itemIdentifier = importService.getFileIdentifier(assessmentItemFile).getValue();  //already validated
            assessmentItemRef.setItemIdentifier(itemIdentifier);
        }

        if (CollectionUtils.isNotEmpty(section.getAssessmentSections())) {
            for (AssessmentSectionDto section1 : section.getAssessmentSections()) {
                updateAssessmentItemsRefsForSection(section1, pathToAssessmentDirectory);
            }
        }
    }

    private List<URI> getURIsContentPackageResources(List<ContentPackageResource> resources, File uniqueUploadDirectory)
            throws URISyntaxException {
        List<URI> uris = new ArrayList<URI>();
        if (resources != null) {
            for (ContentPackageResource contentPackageResource : resources) {
                StringBuilder absolutePath = new StringBuilder(uniqueUploadDirectory.toURI().toString());
                absolutePath.append(contentPackageResource.getHref());
                uris.add(URI.create(absolutePath.toString()));
            }
        }
        return uris;
    }

    URI getIMSManifest(List<URI> unzippedFilesURI) {
        if (CollectionUtils.isNotEmpty(unzippedFilesURI)) {
            for (URI unzippedFileURI : unzippedFilesURI) {
                File unzippedFile = new File(unzippedFileURI);
                if (!unzippedFile.isDirectory()) {
                    if (unzippedFile.getName().endsWith(FILE_TYPE_XML) && unzippedFile.getName().contains(
                            MANIFEST_FILE_NAME))
                        return unzippedFileURI;
                }
            }
        }
        return null; //Not found
    }

    boolean hasIMSManifest(List<URI> unzippedFilesURI) {
        return (getIMSManifest(unzippedFilesURI) != null);
    }
}
