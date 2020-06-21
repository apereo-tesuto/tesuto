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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.util.StaticStorage;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component
public class QtiResourceRelocator {

    StaticStorage staticStorage;

    @Autowired
    ImportService importService;

    public void setStaticStorage(StaticStorage staticStorage) {
        this.staticStorage = staticStorage;
    }

    public void relocateImages(URI resourceURI, String baselocation, String namespace, Integer version)
            throws ParserConfigurationException, SAXException, IOException, TransformerException,
            AmazonServiceException, NoSuchAlgorithmException {
        log.debug("resourceURI: {}", resourceURI);

        File file = new File(resourceURI);
        log.debug("file.canRead(): {}", file.canRead());
        log.debug("file.canExecute(): {}", file.canExecute());
        ValidatedNode<Document> validatedDocument =  importService.getDocument(file);

        if(CollectionUtils.isNotEmpty(validatedDocument.getErrors()) || CollectionUtils.isNotEmpty(validatedDocument.getErrors())) {
            log.error("Failed to parse file {}", file);
            return;
        }
        Document doc = validatedDocument.getValue();
        NodeList nList = doc.getElementsByTagName("img");

        String id = doc.getDocumentElement().getAttribute("identifier");

        String nodeName = doc.getDocumentElement().getNodeName();

        String type;
        switch (nodeName) {
        case "assessmentItem":
            type = StaticStorage.ITEM;
            break;
        case "assessmentTest":
            type = StaticStorage.TEST;
            break;
        default:
            type = StaticStorage.ITEM;
        }
        String keyPrefix = staticStorage.mediaStructure(namespace, type, id, version.toString());

        boolean fileChanged = false;

        for (int i = 0; i < nList.getLength(); i++) {

            Node image = nList.item(i);
            NamedNodeMap nodeMap = image.getAttributes();
            Node source = nodeMap.getNamedItem("src");
            if (source == null) {
                log.debug("src was empty for image from igem {}", id);
                continue;
            }
            String src = source.getNodeValue();
            log.debug("src: {}", src);
            if (StringUtils.isBlank(src) || src.contains("data:") || src.contains("http:") || src.contains("https:")
                    || src.contains("media?path=")) {
                log.debug("Image file was found that was not a relative path {}", src);
                continue;
            }
            String filePath = file.getAbsolutePath().replace(file.getName(), "");
            File imageFile = new File(filePath + src);
            String key = keyPrefix + src;
            String uri = staticStorage.store(key, imageFile, false);
            log.debug("uri: {}", uri);
            fileChanged = true;
            source.setNodeValue(uri);

        }
        if (fileChanged) {
            log.debug("replaceDom started file exists", file.exists());
            replaceDom(doc, file);
            log.debug("replaceDom ended");
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
}
