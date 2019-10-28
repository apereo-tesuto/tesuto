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
package org.cccnext.tesuto.importer.service.competency;

import com.amazonaws.AmazonServiceException;

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.importer.structs.ImportFiles;
import org.xml.sax.SAXException;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.net.URI;
/**
 * Created by jasonbrown on 6/22/16.
 */
public interface CompetencyImportService {

    public File getUploadLocation();

    public CompetencyMapDto parseCompetencyMapResources(URI uri, int version, String discipline)
            throws IOException;

    public CompetencyMapDto parseCompetencyMapResources(InputStream inputStream, int version, String discipline)
            throws IOException;

    public CompetencyDto parseCompetencyResources(URI uri, int version, String discipline)
            throws IOException;

    public CompetencyDto parseCompetencyResources(InputStream inputStream, int version, String discipline)
            throws IOException;

    public ImportFiles getResourcesURIsFromFile(File uploadLocation, File uploadFile, Boolean isXmlFile)
            throws IOException, XmlResourceNotFoundException, ImsManifestException, URISyntaxException,
            AmazonServiceException, NoSuchAlgorithmException, SAXException, ParserConfigurationException;
    
    public ImportFiles getResourcesURIsFromPath(File uploadLocation, String uploadFile, Boolean isXmlFile)
            throws IOException, XmlResourceNotFoundException, ImsManifestException, URISyntaxException,
            AmazonServiceException, NoSuchAlgorithmException, SAXException, ParserConfigurationException;

    public List<CompetencyMapDto> createCompetencyMapFromFile(final File convertedFile, boolean isXml) throws Exception;
}
