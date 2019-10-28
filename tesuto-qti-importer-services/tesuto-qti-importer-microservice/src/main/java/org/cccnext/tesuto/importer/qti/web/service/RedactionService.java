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
package org.cccnext.tesuto.importer.qti.web.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.delivery.service.DeliveryServiceApi;
import org.cccnext.tesuto.importer.qti.service.QtiResourceRelocator;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class RedactionService extends BaseUploadService {

    @Value("${readcted.image.url:http://www.tammystanley.com/images/alfred_e_neuman.jpg}")
    String redactedImageUrl;
        
    @Override
    public ValidatedNode<List<AssessmentItemDto>> parseAssessmentItemFiles(List<URI> assessmentItemURIs, File uploadDirectory, String namespace, HashMap<String, ItemMetadataDto> itemMetadataDtoMap) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        redactFiles(assessmentItemURIs);
        return null;
    }
    
    public void removeResources(URI uri) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        File file = new File(uri);
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        replaceDom(doc, file);
    }
    
    private void redactFiles(List<URI> uris) throws TransformerException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        for(URI uri:uris) {
            File file = new File(uri);
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            cleanNodeType(doc, "div");
            cleanNodeType(doc, "p");
            cleanNodeType(doc, "td");
            cleanNodeType(doc, "prompt");
            handleImages(doc);
            replaceDom(doc, file);
        }
    }
    
    @Override
    public ValidatedNode<List<AssessmentDto>> parseAssessmentFiles(List<URI> assessmentURIs, File uploadDirectory, String namespace, HashMap<String, AssessmentMetadataDto> assessmentMetadataDtoMap)
            throws AmazonServiceException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException,
            TransformerException, XmlResourceNotFoundException, BadResourceException, URISyntaxException {
        redactFiles(assessmentURIs);
        return null;
    }
    
    private void cleanNodeType(Document doc, String tagName) {
        NodeList nList = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nList.getLength(); i++) {
            replaceTextNodes(nList.item(i));
        }
    }
    
    private void replaceTextNodes(Node parent) {
        NodeList nList = parent.getChildNodes();
        for (int i = 0; i < nList.getLength(); i++) {
            Node child = nList.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent("TEXT REDACTED FOR SECURITY");
            }
        }
    }
    
    private void handleImages(Document doc) {
        NodeList nList =  doc.getElementsByTagName("img");
        for (int i = 0; i < nList.getLength(); i++) {

            Node image = nList.item(i);
            NamedNodeMap nodeMap = image.getAttributes();
            Node source = nodeMap.getNamedItem("src");
            Node alt = nodeMap.getNamedItem("alt");
            if(alt != null) {
                alt.setNodeValue("TEXT REDACTED FOR SECURITY");
            }
            
            Node title = nodeMap.getNamedItem("title");
            if(title != null) {
                title.setNodeValue("TEXT REDACTED FOR SECURITY");
            }
            
            if(source == null) {
                log.debug("src was empty for image");
                continue;
            }
            String src = source.getNodeValue();
            log.debug("src: {}", src);
            if(StringUtils.isBlank(src) 
                    || src.contains("data:") 
                    || src.contains("http:") 
                    || src.contains("https:")
                    || src.contains("media?path=")) {
                log.debug("Image file was found that was not a relative path {}", src);
                continue;
            }
           
            source.setNodeValue(redactedImageUrl);
            
            
        }
    }
    
    private void replaceDom(Document document, File file) throws TransformerException, FileNotFoundException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        StreamResult sr = new StreamResult(fileOutputStream);
        transformer.transform(source, sr);
    }

    @Override
    protected AssessmentItemService getAssessmentItemService() {
        //Auto-generated method stub
        return null;
    }

    @Override
    protected AssessmentService getAssessmentService() {
        //Auto-generated method stub
        return null;
    }

    @Override
    protected QtiResourceRelocator getResourceRelocator() {
        //Auto-generated method stub
        return null;
    }

	@Override
	protected AssessmentSessionReader getDeliveryService() {
		//Auto-generated method stub
		return null;
	}
}
