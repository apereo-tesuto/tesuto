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
package org.cccnext.tesuto.importer.qti.assembler.assessment;

import uk.ac.ed.ph.jqtiplus.node.item.Stylesheet;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentOutcomeProcessingDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.importer.qti.assembler.shared.AssessmentOutcomeDeclarationQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.service.QtiResourceLocator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentTestQtiDtoAssembler")
public class AssessmentQtiDtoAssemblerImpl implements AssessmentQtiDtoAssembler {

    @Autowired
    AssessmentOutcomeDeclarationQtiDtoAssembler outcomeDeclarationQtiDtoAssembler;
    @Autowired
    AssessmentOutcomeProcessingQtiDtoAssembler outcomeProcessingQtiDtoAssembler;
    @Autowired
    AssessmentPartQtiDtoAssembler assessmentPartQtiDtoAssembler;
    @Autowired
    QtiResourceLocator qtiResourceLocator;

    @Override
    public AssessmentDto assembleDto(AssessmentTest assessmentTest) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentTest == null) {
            return null;
        }

        AssessmentDto assessmentDto = new AssessmentDto();
        List<AssessmentPartDto> assessmentParts = new LinkedList<AssessmentPartDto>();
        for (TestPart testPart : assessmentTest.getTestParts()) {
            AssessmentPartDto assessmentPartDto = assessmentPartQtiDtoAssembler.assembleDto(testPart);
            assessmentParts.add(assessmentPartDto);
        }
        assessmentDto.setAssessmentParts(assessmentParts);
        assessmentDto.setIdentifier(assessmentTest.getIdentifier());
        // Not sure where to get Language. This is only available at the item
        // level. We need it on the assessment level.
        // For now we will default to English.
        assessmentDto.setLanguage("en_US"); // Not sure if this is actually
                                            // going to be the code we use.
        AssessmentOutcomeProcessingDto outcomeProcessingDto = outcomeProcessingQtiDtoAssembler
                .assembleDto(assessmentTest.getOutcomeProcessing());
        assessmentDto.setOutcomeProcessing(outcomeProcessingDto);
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarations = new LinkedList<AssessmentOutcomeDeclarationDto>();
        for (OutcomeDeclaration outcomeDeclaration : assessmentTest.getOutcomeDeclarations()) {
            AssessmentOutcomeDeclarationDto outcomeDeclarationDto = outcomeDeclarationQtiDtoAssembler.assembleDto(outcomeDeclaration);
            outcomeDeclarations.add(outcomeDeclarationDto);
        }
        assessmentDto.setOutcomeDeclarations(outcomeDeclarations);
        assessmentDto.setTitle(assessmentTest.getTitle());
        assessmentDto.setToolName(assessmentTest.getToolName());
        assessmentDto.setToolVersion(assessmentTest.getToolVersion());

        // Handle stylesheets, look this is similar to
        // AssessmentItemQtiDtoAssemblerImpl.java
        List<Stylesheet> stylesheetList = assessmentTest.getNodeGroups().getStylesheetGroup().getStylesheets();
        StringBuilder stringBuilder = new StringBuilder();
        XmlSourceLocationInformation uploadItem = assessmentTest.getSourceLocation();
        File uploadItemFile = new File(uploadItem.getSystemId());
        StringBuilder uploadLocation = new StringBuilder(uploadItemFile.getParent()).append(File.separatorChar);
        for (Stylesheet stylesheet : stylesheetList) {
            StringBuilder cssPath = new StringBuilder(uploadLocation);
            URI cssUri = null;
            try {
                cssUri = new URI(cssPath.append(stylesheet.getHref()).toString());
            } catch (URISyntaxException e) {
                log.error("cssPath is malformed: {}", cssPath);
            }
            InputStream inputStream = qtiResourceLocator.findResource(cssUri);
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    while (bufferedReader.ready()) {
                        stringBuilder.append(bufferedReader.readLine());
                        stringBuilder.append("\n");
                    }
                } catch (IOException ioe) {
                    log.error("The CSS file in QTI package for item is not readable: {}", stylesheet.getHref());
                } finally {
                    try {
                        bufferedReader.close();
                    } catch (IOException ioe) {
                        log.error("Unable to close BufferedReader for the resource.");
                    }
                }
            }
        }
        log.debug(stringBuilder.toString());
        assessmentDto.setStylesheets(stringBuilder.toString());

        return assessmentDto;
    }

    @Override
    public AssessmentTest disassembleDto(AssessmentDto assessmentDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
