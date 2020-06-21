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
import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.importer.service.competency.CompetencyImportService;
import org.cccnext.tesuto.importer.service.upload.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jasonbrown on 6/21/16.
 */
@Service
public class CompetencyMapUploadService {

	@Autowired
    CompetencyImportService competencyImportService;

    @Autowired
    ImportService importService;
    
    public List<CompetencyMapDto> createCompetencyMapFromFile(final MultipartFile file) throws Exception{
        File uploadDirectory = importService.getUploadLocation();
        File convertedFile = new File(String.format("%s/%s",uploadDirectory, file.getOriginalFilename()));
        file.transferTo(convertedFile);
        boolean isXml = importService.isXmlFile(file.getContentType());

        return competencyImportService.createCompetencyMapFromFile(convertedFile, isXml) ;

    }

    


}
