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

import org.cccnext.tesuto.service.importer.validate.ValidatedNode;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

public interface AssessmentQtiImportService {

    public File getUploadLocation();

    public ValidatedNode<AssessmentDto> parseAssessmentResource(URI assessmentTestURI, String namespace, int version)
            throws BadResourceException;

    public ValidatedNode<AssessmentItemDto> parseAssessmentItemResource(URI assessmentItemUri, String namespace, int version)
            throws AmazonServiceException, NoSuchAlgorithmException, TransformerException, BadResourceException;

    public ValidatedNode<ImportFiles> getResourcesURIsFromFile(File uploadLocation, File uploadFile, Boolean isXmlFile)
            throws URISyntaxException, AmazonServiceException, NoSuchAlgorithmException, IOException;

    public void updateAssessmentItemRefs(AssessmentDto assessmentDto, URI assessmentURI);

    public ValidatedNode<HashMap<String, AssessmentMetadataDto>> getMetadataFromResources(List<URI> assessmentTestFiles) throws SAXException,
            IOException, ParserConfigurationException;

    public ValidatedNode<HashMap<String, ItemMetadataDto>> getItemMetadataFromResources(List<URI> assessmentTestFiles) throws SAXException,
            IOException, ParserConfigurationException;
}
