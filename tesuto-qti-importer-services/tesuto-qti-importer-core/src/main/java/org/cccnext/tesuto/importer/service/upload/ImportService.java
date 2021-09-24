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
package org.cccnext.tesuto.importer.service.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.cccnext.tesuto.domain.util.StaticStorage;
import org.cccnext.tesuto.importer.qti.service.AssessmentQtiImportService;
import org.cccnext.tesuto.importer.qti.service.QtiImportValidationErrorService;
import org.cccnext.tesuto.importer.qti.service.QtiResourceLocator;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentItemService;
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentItemService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentService;
import org.cccnext.tesuto.service.importer.validate.ValidateItemMetadataService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ImportService {

    private final String FILE_TYPE_XML = ".xml";
    private final String MANIFEST_FILE_NAME = "imsmanifest";
    private final String ITEM_METADATA = "itemmetadata";
    private final String ASSESSMENT_METADATA = "assessmentmetadata";
    private final String ASSESSMENT_TEST = "assessmentTest";
    private final String ASSESSMENT_ITEM = "assessmentItem";
    private final String IDENTIFIER = "identifier";
    private final String COMPETENCY = "competency";
    private final String COMPETENCY_MAP = "competencymap";

    @Value("${namespace.xsd.competencymap}")
    String competencyMapNamespaceXSD;
    @Value("${namespace.xsd.competency}")
    String competencyNamespaceXSD;
    @Value("${upload.dir}")
    String uploadDirectory;

    @Autowired
    StaticStorage staticStorage;

    @Autowired
    QtiImportValidationErrorService qtiImportValidationErrorService;

    @Autowired
    AssessmentQtiImportService assessmentQtiImportService;

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
    CompetencyMapDisciplineReader competencyMapDisciplineService;

    @Autowired
    QtiResourceLocator qtiResourceLocator;

    @Autowired
    ClasspathHttpResourceLocator classpathHttpResourceLocator;

    @Autowired
    NetworkHttpResourceLocator networkHttpResourceLocator;

    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    @PostConstruct
    private void setupDocumentBuilderFactory(){
        dbFactory.setNamespaceAware(true);
    }

    public File getUploadLocation() {
        StringBuilder uniqueUploadDirectory = new StringBuilder(uploadDirectory);
        uniqueUploadDirectory.append(UUID.randomUUID()).append('/');
        File uploadDirectory = new File(uniqueUploadDirectory.toString());
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        return uploadDirectory;

    }


    public ValidatedNode<ImportFiles> searchForXmlFiles(List<URI> extractedFileUris, ImportFiles importFiles) {
        List<ValidationMessage> validationMessages = new ArrayList<>();
        for (URI extractedFileUri : extractedFileUris) {
            if (extractedFileUri.getPath().endsWith(FILE_TYPE_XML)
                    && !extractedFileUri.getPath().contains(MANIFEST_FILE_NAME)) {
                File file = new File(extractedFileUri);
                if (!file.isHidden()) {
                    ValidatedNode<String> validatedFileType = getFileType(file);

                    validationMessages.addAll(validatedFileType.getErrors());

                    if (validatedFileType.getValue().equals(COMPETENCY)) {
                        importFiles.getCompetencies().add(extractedFileUri);
                    } else if (validatedFileType.getValue().equals(COMPETENCY_MAP)) {
                        importFiles.getCompetencyMaps().add(extractedFileUri);
                    } else if (validatedFileType.getValue().equals(ASSESSMENT_ITEM)) {
                        importFiles.getItems().add(extractedFileUri);
                    } else if (validatedFileType.getValue().equals(ASSESSMENT_TEST)) {
                        importFiles.getTests().add(extractedFileUri);
                    } else if (validatedFileType.getValue().equals(ASSESSMENT_METADATA)) {
                        importFiles.getTestsMetadata().add(extractedFileUri);
                    } else if (validatedFileType.getValue().equals(ITEM_METADATA)) {
                        importFiles.getItemsMetadata().add(extractedFileUri);
                    }
                }
            }
        }
        return new ValidatedNode<>(validationMessages, importFiles);
    }

    public ValidatedNode<String> getFileType(File file) {
        ValidatedNode<Document> documentValidatedNode = getDocument(file);
        if(CollectionUtils.isNotEmpty(documentValidatedNode.getErrors())){
            return new ValidatedNode<>(documentValidatedNode.getErrors(), "");
        }
        Document doc = documentValidatedNode.getValue();
        String nodeName = doc.getDocumentElement().getNodeName();
        return new ValidatedNode<>(documentValidatedNode.getErrors(), nodeName);
    }

    public ValidatedNode<String> getFileIdentifier(File file){
        ValidatedNode<Document> documentValidatedNode = getDocument(file);
        Document doc = documentValidatedNode.getValue();
        if(CollectionUtils.isNotEmpty(documentValidatedNode.getErrors())){
            return new ValidatedNode<>(documentValidatedNode.getErrors(), "");
        }
        String id = doc.getDocumentElement().getAttribute(IDENTIFIER);
        return new ValidatedNode<>(documentValidatedNode.getErrors(), id);
    }

    public ValidatedNode<List<String>> getNamespace(File file){
        ValidatedNode<Document> documentValidatedNode = getDocument(file);
        List<ValidationMessage> errors = new ArrayList<>();
        Document doc = documentValidatedNode.getValue();
        if(CollectionUtils.isNotEmpty(documentValidatedNode.getErrors())){
            return new ValidatedNode<>(documentValidatedNode.getErrors(), new ArrayList<>());
        }
        errors.addAll(documentValidatedNode.getErrors());
        Element rootElement = doc.getDocumentElement();
        String schemaLocation = rootElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
        String[] schemaData = schemaLocation.trim().split("\\s+");
        List<String> schemaUris = new ArrayList<>();

        for (int i = 1; i < schemaData.length; i += 2) { /* (ns1 uri1 ns2 uri2 ...) */
            schemaUris.add(schemaData[i]);
        }


        if(schemaUris.isEmpty()){
            ValidationMessage validationMessage = new ValidationMessage();
            validationMessage.setNode("root");
            validationMessage.setFile(file.toString());
            validationMessage.setMessage("schemaLocation is not valid.");
            errors.add(validationMessage);
        }
        return new ValidatedNode<>(errors, schemaUris);
    }

    public ValidatedNode<Document> getDocument(File file){
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            return new ValidatedNode<Document>(doc);
        }catch (ParserConfigurationException | SAXException | IOException e){
            log.error(String.format("Failed to get file type for %s", file.toString()), e); // Don't stop processing
            List<ValidationMessage> errors = qtiImportValidationErrorService.getErrors(e);
            return new ValidatedNode<Document>(errors, null);
        }
    }

    //TODO Add validation here if necessary.
    public String getTextContentByTagName(File file, String tagName)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        if (doc.getDocumentElement().getElementsByTagName(tagName) != null
                && doc.getDocumentElement().getElementsByTagName(tagName).item(0) != null) {
            return doc.getDocumentElement().getElementsByTagName(tagName).item(0).getTextContent();
        }
        return null;
    }

    public boolean isXmlFile(String contentType) {
        if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
            return true;
        }
        return false;
    }

    //TODO Add validation here if necessary.
    public ValidatedNode<Boolean> verifySchema(String fileToVerify) {
        List<ValidationMessage> warnings = new ArrayList<>();
        List<ValidationMessage> errors = new ArrayList<>();
        DocumentBuilderFactory validationDBFactory = DocumentBuilderFactory.newInstance();
        validationDBFactory.setNamespaceAware(true);
        validationDBFactory.setValidating(true);

        File file = new File(fileToVerify);
        List<String> namespaces = new ArrayList<>();
        ValidatedNode<String> validatedFileType = getFileType(file);
        String fileType = validatedFileType.getValue();

        if(fileType.equals(COMPETENCY_MAP)){
            namespaces.add(competencyMapNamespaceXSD);
        }else if (fileType.equals(COMPETENCY)){
            namespaces.add(competencyNamespaceXSD);
        }else{
            ValidatedNode<List<String>> validatedNamespace = getNamespace(file);
            namespaces.addAll(validatedNamespace.getValue());
            warnings.addAll(validatedNamespace.getWarnings());
            errors.addAll(validatedNamespace.getErrors());
        }

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Source[] schemaSources = new Source[namespaces.size()];
            for(int i=0; i< namespaces.size(); i++){
                InputStream input = classpathHttpResourceLocator.findResource(namespaces.get(i));
                if (input == null) {
                    input = networkHttpResourceLocator.findResource(namespaces.get(i));
                }
                Source schemaFile = new StreamSource(input);
                schemaSources[i] = schemaFile;
            }
            Schema schema = factory.newSchema(schemaSources);
            Validator validator = schema.newValidator();
            Source xmlFile = new StreamSource(file);
            validator.validate(xmlFile);
        }catch(Exception e){
            errors.addAll(qtiImportValidationErrorService.getErrors(e));

        } finally {
            return new ValidatedNode<>(errors, warnings, errors.isEmpty());
        }

    }

    public void verifySchema(List<URI> uris) throws SAXException{
        if(CollectionUtils.isNotEmpty(uris)) {
            for (URI uri : uris) {
                verifySchema(uri.getPath());
            }
        }
    }

    public synchronized PackageResults processFiles(BaseUploadService baseUploadService, ValidatedNode<ImportFiles> validatedImportFiles, File uploadDirectory, String namespace)
            throws IOException, SAXException, ParserConfigurationException, NoSuchAlgorithmException,
            BadResourceException, TransformerException, XmlResourceNotFoundException, URISyntaxException {
        ImportFiles importFiles = validatedImportFiles.getValue();
        PackageResults packageResults = new PackageResults();

        ValidatedNode<HashMap<String, AssessmentMetadataDto>> validatedMetadataDtoMap = assessmentQtiImportService.getMetadataFromResources(importFiles.getTestsMetadata());
        HashMap<String, AssessmentMetadataDto> metadataDtoMap = validatedMetadataDtoMap.getValue();
        packageResults.addValidationErrors(validatedMetadataDtoMap.getErrors()); //metadata parsing errors

        ValidatedNode<List<AssessmentDto>> validatedAssessmentDtos = baseUploadService.parseAssessmentFiles(importFiles.getTests(), uploadDirectory, namespace, metadataDtoMap);
        List<AssessmentDto> assessmentDtos = validatedAssessmentDtos.getValue();
        packageResults.setAssessmentDtos(assessmentDtos);
        packageResults.addValidationErrors(validatedAssessmentDtos.getErrors()); //qti works and parsing errors
        packageResults.addValidationWarnings(validatedAssessmentDtos.getWarnings());

        ValidatedNode<HashMap<String, ItemMetadataDto>> validatedItemmetadataDtoMap = assessmentQtiImportService.getItemMetadataFromResources(importFiles.getItemsMetadata());
        HashMap<String, ItemMetadataDto> itemmetadataDtoMap = validatedItemmetadataDtoMap.getValue();
        packageResults.addValidationErrors(validatedItemmetadataDtoMap.getErrors()); //metadata parsing errors

        ValidatedNode<List<AssessmentItemDto>> validatedAssessmentItemDtos = baseUploadService.parseAssessmentItemFiles(importFiles.getItems(), uploadDirectory, namespace, itemmetadataDtoMap);
        List<AssessmentItemDto> assessmentItemDtos = validatedAssessmentItemDtos.getValue();
        packageResults.setAssessmentItemDtos(assessmentItemDtos);
        packageResults.addValidationErrors(validatedAssessmentItemDtos.getErrors()); //qti works and parsing errors
        packageResults.addValidationWarnings(validatedAssessmentItemDtos.getWarnings());

        ValidatedNode<HashMap<String, SortedSet<Double>>> validatedMap = validateDeliveryRules(metadataDtoMap, itemmetadataDtoMap, assessmentDtos, assessmentItemDtos);
        packageResults.addValidationErrors(validatedMap.getErrors()); // assess requirement errors
        packageResults.addValidationWarnings(validatedMap.getWarnings()); //assess requirement warnings

        normalizePackage(assessmentDtos, assessmentItemDtos, validatedMap.getValue());

        packageResults.addValidationErrors(qtiResourceLocator.getErrors());
        qtiResourceLocator.clearErrors();
        return packageResults;
    }

    public ValidatedNode<HashMap<String, SortedSet<Double>>> validateDeliveryRules(HashMap<String, AssessmentMetadataDto> metadataDtoMap,
            HashMap<String, ItemMetadataDto> itemmetadataDtoMap,
            List<AssessmentDto> assessmentDtos,
            List<AssessmentItemDto> assessmentItemDtos){

        List<ValidationMessage> errors = new ArrayList<>();
        List<ValidationMessage> warnings = new ArrayList<>();

        // Add errors
        errors.addAll(validateAssessmentMetadataService.processMetadataMap(metadataDtoMap, competencyMapDisciplineService.read()));
        HashMap<String, SortedSet<Double>> sortedSetHashMap = validateAssessmentItemService.processAssessmentItems(assessmentItemDtos);
        errors.addAll(validateAssessmentService.processAssessments(assessmentDtos, sortedSetHashMap));  //Check to make sure there are no cycles and targets exist.

        //Add warnings
        warnings.addAll(validateAssessmentMetadataService.validateMetadataMapKeysMatchAssessmentIdentifiers(metadataDtoMap, assessmentDtos));
        warnings.addAll(validateItemMetadataService.validateMetadataMapKeysMatchItemIdentifiers(itemmetadataDtoMap, assessmentItemDtos));

        return new ValidatedNode<>(errors, warnings, sortedSetHashMap);
    }

    public void normalizePackage(List<AssessmentDto> assessmentDtos, List<AssessmentItemDto> assessmentItemDtos, HashMap<String, SortedSet<Double>> sortedSetHashMap){
        //Normalize
        normalizeAssessmentItemService.normalizeOutcomeMaxMin(assessmentItemDtos, sortedSetHashMap);
        normalizeAssessmentMetadataService.normalizeMetadata(assessmentDtos, assessmentItemDtos);
    }
}
