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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.importer.qti.service.AssessmentQtiImportService;
import org.cccnext.tesuto.importer.qti.service.QtiResourceRelocator;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class BaseUploadService {
    @Autowired
    AssessmentQtiImportService assessmentQtiImportService;

    @Autowired
    ImportService importService;

    protected abstract AssessmentItemService getAssessmentItemService();

    protected abstract AssessmentService getAssessmentService();

    protected abstract AssessmentSessionReader getDeliveryService();

    protected abstract QtiResourceRelocator getResourceRelocator();

    public ValidatedNode<List<AssessmentDto>> parseAssessmentFiles(List<URI> assessmentTestURIs, File uploadDirectory,
            String namespace, HashMap<String, AssessmentMetadataDto> assessmentMetadataDtoMap)
            throws AmazonServiceException, NoSuchAlgorithmException, ParserConfigurationException, SAXException,
            IOException, TransformerException, XmlResourceNotFoundException, BadResourceException, URISyntaxException {
        List<AssessmentDto> parsedAssessments = new LinkedList<AssessmentDto>();
        List<ValidationMessage> validationErrors = new ArrayList<>();
        List<ValidationMessage> validationWarnings = new ArrayList<>();
        for (URI assessmentURI : assessmentTestURIs) {
            Path path = Paths.get(assessmentURI);
            ValidatedNode<String> validatedAssessmentIdentifier = importService.getFileIdentifier(path.toFile());
            validationErrors.addAll(validatedAssessmentIdentifier.getErrors());
            int version = getAssessmentService().getNextVersion(namespace, validatedAssessmentIdentifier.getValue());


            getResourceRelocator().relocateImages(assessmentURI, uploadDirectory.toString(), namespace, version);

            ValidatedNode<AssessmentDto> validatedAssessmentDto = assessmentQtiImportService.parseAssessmentResource(assessmentURI, namespace,
                    version);
            AssessmentDto assessmentDto = validatedAssessmentDto.getValue();
            assessmentDto.setAssessmentMetadata(assessmentMetadataDtoMap.get(assessmentDto.getIdentifier()));
            parsedAssessments.add(assessmentDto);
            validationErrors.addAll(validatedAssessmentDto.getErrors());
            validationWarnings.addAll(validatedAssessmentDto.getWarnings());
        }
        return new ValidatedNode<>(validationErrors, validationWarnings, parsedAssessments);
    }

    public ValidatedNode<List<AssessmentItemDto>> parseAssessmentItemFiles(List<URI> assessmentItemURIs, File uploadDirectory,
            String namespace, HashMap<String, ItemMetadataDto> itemMetadataDtoMap)
            throws AmazonServiceException, NoSuchAlgorithmException, ParserConfigurationException, SAXException,
            IOException, TransformerException, XmlResourceNotFoundException, BadResourceException, URISyntaxException {

        log.debug("Start parsing assessmentItemURIs: {}", assessmentItemURIs.size());
        List<AssessmentItemDto> parsedAssessmentItems = new LinkedList<AssessmentItemDto>();
        List<ValidationMessage> validationErrors = new ArrayList<>();
        List<ValidationMessage> validationWarnings = new ArrayList<>();
        for (URI assessmentItemURI : assessmentItemURIs) {
            Path path = Paths.get(assessmentItemURI);
            ValidatedNode<String> validatedAssessmentItemIdentifier = importService.getFileIdentifier(path.toFile());
            validationErrors.addAll(validatedAssessmentItemIdentifier.getErrors());
            int version = getAssessmentItemService().getNextVersion(namespace,
                    validatedAssessmentItemIdentifier.getValue());

            getResourceRelocator().relocateImages(assessmentItemURI, uploadDirectory.toString(), namespace, version);

            ValidatedNode<AssessmentItemDto> validatedAssessmentItemDto = assessmentQtiImportService.parseAssessmentItemResource(assessmentItemURI, namespace, version);
            AssessmentItemDto assessmentItemDto = validatedAssessmentItemDto.getValue();
            assessmentItemDto.setItemMetadata(itemMetadataDtoMap.get(assessmentItemDto.getIdentifier()));
            parsedAssessmentItems.add(assessmentItemDto);
            validationErrors.addAll(validatedAssessmentItemDto.getErrors());
            validationWarnings.addAll(validatedAssessmentItemDto.getWarnings());
        }

        log.debug("Finished  processing assessmentItemURIs: {}", assessmentItemURIs.size());
        return new ValidatedNode<>(validationErrors, validationWarnings, parsedAssessmentItems);
    }

    public List<AssessmentItemDto> storeAssessmentItems(List<AssessmentItemDto> assessmentItemDtos) {
        log.debug("Start storing assessmentItemDtos: {}", assessmentItemDtos.size());
        List<AssessmentItemDto> storedAssessmentItems = getAssessmentItemService().create(assessmentItemDtos);
        log.debug("Finished storing assessmentItemDtos: {}", storedAssessmentItems.size());
        return storedAssessmentItems;
    }

    public void storeAssessments(List<AssessmentDto> assessmentDtos) {
        log.debug("Start storing assessmentDtos: {}", assessmentDtos.size());
        getAssessmentService().create(assessmentDtos);
        log.debug("Finished storing assessmentDtos: {}", assessmentDtos.size());
    }

    public ImportResults createAssessmentSessions(List<AssessmentDto> assessmentDtos,
            List<AssessmentItemDto> assessmentItemDtos, String namespace, String userId) {

        if (CollectionUtils.isEmpty(assessmentDtos)) {
            log.debug("Start creating assessment for empty assessments list.");
            AssessmentDto assessmentDto = getAssessmentService()
                    .generateLinearAssessmentFromAssessmentItems(assessmentItemDtos, namespace);
            this.getAssessmentService().create(assessmentDto);
            assessmentDtos.add(assessmentDto);
            log.debug("Finished creating assessment for empty assessments list.");
        }

        List<String> assessmentSessions = new ArrayList<String>();

        log.debug("Start creating assessmentSessions for assessments size: {}", assessmentDtos.size());
        for (AssessmentDto assessmentDto : assessmentDtos) {
            log.debug("Start creating assessmentSession for assessment: {}", assessmentDto.getIdentifier());
            String savedAssessmentSessionId = getDeliveryService().createUserAssessmentSession(userId,
                    assessmentDto.getScopedIdentifier(), DeliveryType.ONLINE, null);
            log.debug("Finished creating assessmentSession for assessment: {}", assessmentDto.getIdentifier());
            assessmentSessions.add(savedAssessmentSessionId);
        }
        log.debug("Finished creating assessmentSessions for assessments size: {}", assessmentDtos.size());
        ImportResults results = new ImportResults();
        results.setAssessmentSessionIds(assessmentSessions);
        return results;
    }

}
