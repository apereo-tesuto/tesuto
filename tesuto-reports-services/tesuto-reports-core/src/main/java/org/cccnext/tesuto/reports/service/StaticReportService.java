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
package org.cccnext.tesuto.reports.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.enums.MetadataType;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.domain.util.NumberedAscii;
import org.cccnext.tesuto.domain.util.ZipFileCompressor;
import org.cccnext.tesuto.reports.model.ItemDescription;
import org.cccnext.tesuto.reports.model.StructureDescription;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class StaticReportService {

    private AssessmentReader assessmentService;

    private AssessmentItemReader assessmentItemService;
    
    @Autowired
    private ZipFileCompressor zipfileCompressor;


    @Value("${report.directory}")
    String reportDirectory;
    

    @Value("${report.zip.file.password}") private String password;

    @Value("${report.zip.file.directory}") private String zipfileDirectory;

    public AssessmentReader getAssessmentService() {
        return assessmentService;
    }

    public void setAssessmentService(AssessmentReader assessmentService) {
        this.assessmentService = assessmentService;
    }

    public AssessmentItemReader getAssessmentItemService() {
        return assessmentItemService;
    }

    public void setAssessmentItemService(AssessmentItemReader assessmentItemService) {
        this.assessmentItemService = assessmentItemService;
    }
    
    public FileSystemResource processResults(String namespace, String assessmentIdentifier) throws IOException {
        
        ScopedIdentifier scopedIdentifier = new ScopedIdentifier(namespace, assessmentIdentifier);
        AssessmentDto assessment = assessmentService.readLatestPublishedVersion(scopedIdentifier);
        File itemDescriptions = processItemDescriptions(assessment);
        File structureDescriptions = processStructure(assessment);
        
        FileSystemResource resource =  new FileSystemResource(buildEncryptedZip(assessment.scopedId("_"), itemDescriptions, structureDescriptions));
        deleteResource(itemDescriptions);
        deleteResource(structureDescriptions);
        return resource;
    }
    
    private void deleteResource(File file) throws IOException {
        if(file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        }else {
            FileUtils.deleteQuietly(file);
        }
    }

    public File processItemDescriptions( AssessmentDto assessment) {
       
        List<ItemDescription> descriptions = processAssessment(assessment);
        File fileDirectory = getDirectory();
        String fileName = assessment.scopedId("_") + "_item_description.csv";
        File file = new File(fileDirectory, fileName);
        try {
            BufferedWriter writer = getFileWriter(file);
            writer.append(itemDescriptionReportHeaders());
            writer.newLine();
            writer.flush();
            for (ItemDescription description : descriptions) {
                String row = printDescriptionRow(description);
                writer.append(row);
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    
    public File processStructure(AssessmentDto assessment) {

        List<StructureDescription> descriptions = processAssessmentForStructure(assessment);
        File fileDirectory = getDirectory();
        String fileName = assessment.scopedId("_") + "_structure_description.csv";
        File file = new File(fileDirectory, fileName);
        StructureDescription maxStructure = descriptions.stream().max((d1, d2) -> Integer.compare(d1.getItemsInStructure().size(),
                d2.getItemsInStructure().size())).get();
        int maxItems = maxStructure.getItemsInStructure().size();
        try {
            BufferedWriter writer = getFileWriter(file);
            writer.append(structureDescriptionReportHeaders(maxItems));
            writer.newLine();
            writer.flush();
            for (StructureDescription description : descriptions) {
                StringBuilder row = new StringBuilder();
                row.append(description.getStructureId()).append(",");
                row.append(description.getStructureType()).append(",");
                description.getItemsInStructure().forEach(s ->  row.append(s).append(","));
                for(int i = description.getItemsInStructure().size();i < maxItems ; i++) {
                    row.append(",");
                }
                writer.append(row.toString());
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    
    private List<StructureDescription> processAssessmentForStructure(AssessmentDto assessment) {
        String namespace = assessment.getNamespace();
        List<StructureDescription> structureDescriptions = new ArrayList<StructureDescription>();
        Map<String, MetadataType> sectionMetadata = assessment.getAssessmentMetadata().getSectionMetadata();
        for (AssessmentPartDto assessmentPart : assessment.getAssessmentParts()) {
            for (AssessmentSectionDto assessmentSection : assessmentPart.getAssessmentSections()) {
                processSectionForStructure(assessmentSection, namespace, structureDescriptions, sectionMetadata);
            }
        }
        return structureDescriptions;
    }

    private void processSectionForStructure(AssessmentSectionDto assessmentSection, String namespace,
            List<StructureDescription> structureDescriptions, Map<String, MetadataType> sectionMetadata ) {
        StructureDescription structureDescription = new StructureDescription();
        List<AssessmentItemRefDto> refItems = assessmentSection.getAssessmentItemRefs();
        MetadataType metadataType = sectionMetadata.get(assessmentSection.getId());
        if(metadataType != null) {
            structureDescription.setStructureType(metadataType.name());
        }
        structureDescription.setStructureId(assessmentSection.getId());
        for (AssessmentItemRefDto refItem : refItems) {
        	// TODO: Why is this not using the Assessment Item ID GUID?
            AssessmentItemDto item = assessmentItemService.readLatestPublishedVersion(namespace, refItem.getItemIdentifier());
            if (item == null) {
                log.warn("REPORTS_SERVICE: No item found for expected item in " + refItem.getItemIdentifier());
                continue;
            }
            
            structureDescription.getItemsInStructure().add(item.getScopedIdTag());
        }
        structureDescriptions.add(structureDescription);
        for (AssessmentSectionDto assessmentSubsection : assessmentSection.getAssessmentSections()) {
            processSectionForStructure(assessmentSubsection, namespace, structureDescriptions, sectionMetadata);
        }
    }
    
    private String structureDescriptionReportHeaders(int maxItems) {
        StringBuilder headers = new StringBuilder();
        headers.append("Section Identifier").append(",")
        .append("Section Type").append(",");
        for(int i = 1; i < maxItems; i++)
            headers.append("Item Identifier " + i).append(",");
        headers.append("Item Identifier " + maxItems);
        return headers.toString();
    }

    private String itemDescriptionReportHeaders() {
        StringBuilder headers = new StringBuilder();
        headers.append("Item Identifier").append(",")
        .append("Item Body").append(",")
        .append("Item Stimulus").append(",")
        .append("CommonCore").append(",")
        .append("Competencies").append(",")
        .append("Max Score").append(",")
        .append("Min Score").append(",")
        .append("Correct Response").append(",")
        .append("Mapped Response Keys").append(",")
        .append("Mapped Response Values").append(",");
        int order = 0;
        do {
            headers.append(NumberedAscii.getSingleCharacter(order)).append(",");
            order++;
        } while (NumberedAscii.getSingleCharacter(order) != '!');
        return headers.toString();
    }

    private String printDescriptionRow(ItemDescription description) {
        StringBuilder row = new StringBuilder();
        row.append(description.getScopedId()).append(",")
                .append(cleanHtmlCSVCharacters(description.getBody())).append(",")
                .append(cleanHtmlCSVCharacters(description.getStimulus())).append(",")
                .append(cleanHtmlCSVCharacters(description.getCommonCore())).append(",")
                .append(cleanHtmlCSVCharacters(description.getCompetencies())).append(",")
                .append(description.getMaxScore() == null? "": description.getMaxScore()).append(",")
                .append(description.getMinScore() == null? "": description.getMaxScore()).append(",")
                .append(description.getCorrectResponse() == null? "": description.getMaxScore()).append(",")
                .append(description.getMappedResponse() == null? "": description.getMaxScore()).append(",")
                .append(description.getMappedResponseValue() == null? "": description.getMaxScore()).append(",");
        int order = 0;
        for (String responseContent : description.getResponseContent()) {
            row.append(cleanHtmlCSVCharacters(responseContent)).append(",");
            order++;
        }
        if (NumberedAscii.getSingleCharacter(order) == '!') {
            return row.toString();
        }
        do {
            row.append(",");
            order++;
        } while (NumberedAscii.getSingleCharacter(order) != '!');
        return row.toString();
    }
    
    private String cleanHtmlCSVCharacters(String str) {
        return StringEscapeUtils.escapeCsv(StringEscapeUtils.unescapeHtml(str));
    }

    private List<ItemDescription> processAssessment(AssessmentDto assessment) {

        String namespace = assessment.getNamespace();
        List<ItemDescription> itemDescriptions = new ArrayList<ItemDescription>();
        for (AssessmentPartDto assessmentPart : assessment.getAssessmentParts()) {
            for (AssessmentSectionDto assessmentSection : assessmentPart.getAssessmentSections()) {
                processSection(assessmentSection, namespace, itemDescriptions);
            }
        }
        return itemDescriptions;
    }

    private void processSection(AssessmentSectionDto assessmentSection, String namespace,
            List<ItemDescription> itemDescriptions) {

        List<AssessmentItemRefDto> refItems = assessmentSection.getAssessmentItemRefs();
        for (AssessmentItemRefDto refItem : refItems) {
            // TODO: Why is this not using the Assesment Item ID GUID?
            AssessmentItemDto item = assessmentItemService.readLatestPublishedVersion(namespace, refItem.getItemIdentifier());
            if (item == null) {
                log.warn("REPORTS_SERVICE: No item found for expected item in " + refItem.getItemIdentifier());
                continue;
            }
            ItemDescription itemDescription = new ItemDescription();
            itemDescription.setBody(processItemBody(item.getBody()));
            itemDescription.setScopedId(item.getScopedIdTag());
            if(item.getStimulusRef() != null) {
                itemDescription.setStimulus(item.getStimulusRef().getId());
            } else {
                itemDescription.setStimulus("");
            }
            processMetadata( item.getItemMetadata(), itemDescription);
            
            getItemReponseStructure(item, itemDescription);
            processOutcomeDeclarations(item.getOutcomeDeclarationDtos(), itemDescription);
            itemDescriptions.add(itemDescription);
        }
        for (AssessmentSectionDto assessmentSubsection : assessmentSection.getAssessmentSections()) {
            processSection(assessmentSubsection, namespace, itemDescriptions);
        }
    }
    
    private void processMetadata(ItemMetadataDto itemMetadataDto, ItemDescription itemDescription) {
        if(itemMetadataDto == null)
        {
            itemDescription.setCommonCore("");
            itemDescription.setCompetencies("");
            return;
        }
        StringBuilder commonCore = new StringBuilder("");
        if( itemMetadataDto.getCommonCoreRef() != null) {
            for(String cc:itemMetadataDto.getCommonCoreRef()) {
                if(StringUtils.isNotBlank(cc)) {
                    commonCore.append(cc);
                }
            }
        }
        itemDescription.setCommonCore(commonCore.toString());
        
        StringBuilder competencies = new StringBuilder("");
        if(itemMetadataDto.getCompetencies() != null 
                && itemMetadataDto.getCompetencies().getCompetencyRef() != null) {
            for(CompetencyRefItemMetadataDto cc:itemMetadataDto.getCompetencies().getCompetencyRef()) {
                competencies.append(cc.getCompetencyRefId()).append(":");
                competencies.append(cc.getMapDiscipline()).append(" ");
            }
        }
        itemDescription.setCompetencies(competencies.toString());
        
    }
    
    private void processOutcomeDeclarations(List<AssessmentOutcomeDeclarationDto> outcomeDeclarations, ItemDescription itemDescription) {
        for(AssessmentOutcomeDeclarationDto outcomeDeclaration:outcomeDeclarations) {
            if(outcomeDeclaration.getIdentifier().equals("SCORE")) {
                String maxScore = outcomeDeclaration.getNormalMaximum() != null ? outcomeDeclaration.getNormalMaximum().toString() : "";
                String minScore = outcomeDeclaration.getNormalMinimum() != null ? outcomeDeclaration.getNormalMinimum().toString() : "";
                itemDescription.setMaxScore(maxScore);
                itemDescription.setMinScore(minScore);
            }
        }
    }

    private String processPrompt(String prompt) {
        prompt = prompt.replaceAll("\n", "");
        prompt = processPromptTitle(prompt);
        prompt = prompt.replaceAll("<div.*?>", "");
        prompt = prompt.replaceAll("</div>", "");
        prompt = prompt.replaceAll("<math.*?>", "");
        prompt = prompt.replaceAll("</math>", "");
        prompt = prompt.replaceAll("<span.*?>", "");
        prompt = prompt.replaceAll("</span>", "");
        prompt = prompt.replaceAll("  ", " ");
        prompt = prompt.replaceAll("src=\".*?\"", "");
        prompt = prompt.replaceAll("<br/>", "");
        prompt = prompt.replaceAll("xmlns:xsi=\".*?\"", "");
        prompt = prompt.replaceAll("xmlns=\".*?\"", "");
        prompt = prompt.replaceAll("xsi:schemaLocation=\".*?\"", "");
        return prompt;
    }
    
    private String processPromptTitle(String prompt) {
       Pattern pattern = Pattern.compile("(<div[^>]+?title=['\"])([^'\"]*?)(.*?)(['\"][^>]+?>)(.*?)");
       Matcher m = pattern.matcher(prompt);
       if(m.matches()) {
           return m.replaceAll("CAPTION:$3 - $5");
       }
       return prompt;
    }

    private String processItemBody(String itemBody) {
        if (StringUtils.isBlank(itemBody))
            return "";
        itemBody = itemBody.replaceAll("<itemBody.*?>", "");
        itemBody = itemBody.replaceAll("</itemBody>", "");
        itemBody = itemBody.replaceAll("\n", "");
        itemBody = itemBody.replaceAll("<div.*?>", "");
        itemBody = itemBody.replaceAll("</div>", "");
        itemBody = itemBody.replaceAll("<math.*?>", "");
        itemBody = itemBody.replaceAll("</math>", "");
        itemBody = itemBody.replaceAll("uiid=\".*?\"", "");
        itemBody = itemBody.replaceAll("<span.*?>", "");
        itemBody = itemBody.replaceAll("</span>", "");
        itemBody = itemBody.replaceAll("  ", " ");
        itemBody = itemBody.replaceAll("<p>", "");
        itemBody = itemBody.replaceAll("</p>", "");
        itemBody = itemBody.replaceAll("src=\".*?\"", "");
        itemBody = itemBody.replaceAll("<br/>", "");
        itemBody = itemBody.replaceAll("xmlns:xsi=\".*?\"", "");
        itemBody = itemBody.replaceAll("xmlns=\".*?\"", "");
        itemBody = itemBody.replaceAll("xsi:schemaLocation=\".*?\"", "");
        return itemBody;

    }

    private void getItemReponseStructure(AssessmentItemDto item, ItemDescription itemDescription) {
        int order = 0;
        CorrectResponses correctResponses = new CorrectResponses();
        for (AssessmentInteractionDto interaction : item.getInteractions()) {
            AssessmentResponseVarDto responseVar = getResponseVar(item, interaction.getResponseIdentifier());
            List<String> correctValues = null;
            Map<String,Double> correctMap = null;
           
            if(responseVar.getCorrectResponse() != null) {
                correctValues = responseVar.getCorrectResponse().getValues();
            }
            if(responseVar.getMapping() != null) {
                correctMap = responseVar.getMapping().getMapping();
            }
            
            switch (interaction.getType()) {
            case CHOICE_INTERACTION:
                AssessmentChoiceInteractionDto choice = (AssessmentChoiceInteractionDto) interaction;
                for (AssessmentSimpleChoiceDto value : choice.getChoices()) {
                    itemDescription.getResponseContent().add(processPrompt(value.getContent()));
                    getCorrectResponses(correctValues, correctMap,  
                            value.getIdentifier(), correctResponses, order);
                    order++;
                }
                
               
                break;
            case EXTENDED_TEXT_INTERACTION:
                break;
            case INLINE_CHOICE_INTERACTION:
                AssessmentInlineChoiceInteractionDto inlineChoice = (AssessmentInlineChoiceInteractionDto) interaction;
                for (AssessmentInlineChoiceDto value : inlineChoice.getInlineChoices()) {
                    itemDescription.getResponseContent().add(processPrompt(value.getContent()));
                    getCorrectResponses(correctValues, correctMap,  
                            value.getIdentifier(), correctResponses, order);
                    order++;
                }
                break;
            case MATCH_INTERACTION:
                AssessmentMatchInteractionDto match = (AssessmentMatchInteractionDto) interaction;

                AssessmentSimpleMatchSetDto rows = match.getMatchSets().get(0);
                AssessmentSimpleMatchSetDto columns = match.getMatchSets().get(1);
                for (AssessmentSimpleAssociableChoiceDto row : rows.getMatchSet()) {
                    for (AssessmentSimpleAssociableChoiceDto column : columns.getMatchSet()) {
                        String value = row.getContent() + " " + column.getContent();
                        itemDescription.getResponseContent().add(processPrompt(value));
                        order++;
                    }
                   
                }
                break;
            case NULL_INTERACTION:
                break;
            case TEXT_ENTRY_INTERACTION:

                break;
            default:
                break;

            }
        }
        itemDescription.setCorrectResponse(correctResponses.correctResponse);
        itemDescription.setMappedResponse(correctResponses.mappedResponse);
        itemDescription.setMappedResponseValue(correctResponses.mappdResponseValues);
    }

    private void getCorrectResponses(List<String> correctValues, Map<String,Double> correctMap, String identifier, CorrectResponses correctResponses, int order) {
        if(correctValues != null && correctValues.contains(identifier)) {
            correctResponses.correctResponse += NumberedAscii.getSingleCharacter(order);
        }
        if(correctMap != null && correctMap.containsKey(identifier)) {
            correctResponses.mappedResponse += NumberedAscii.getSingleCharacter(order);
            correctResponses.mappdResponseValues += correctMap.get(identifier).toString();
        }
    }


    private AssessmentResponseVarDto getResponseVar(AssessmentItemDto item, String responseIdentifier) {
        return item.getResponseVars().stream().filter(rv -> rv.getIdentifier().equals(responseIdentifier)).findFirst()
                .get();
    }

    

    private File getDirectory() {
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyy_hh_mm_ss");
        File fileDirectory = new File(reportDirectory.toString() + "/static/" + sdf.format(new Date()));
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        return fileDirectory;
    }

    private BufferedWriter getFileWriter(File writeFile) throws IOException {
        writeFile.createNewFile();
        FileWriter fileWriter = new FileWriter(writeFile.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        return bufferedWriter;

    }
    
    class CorrectResponses {
        private String correctResponse = "";
        private String mappedResponse = "";
        private String  mappdResponseValues = "";
    }
    
    private File buildEncryptedZip(String scopedIdentifierTag, File itemDescriptions, File structureDescriptions) throws IOException {
        List<File> files = Lists.asList(itemDescriptions, new File[]{structureDescriptions});
        
        return zipfileCompressor.compressFiles(zipfileDirectory, 
                getZipFileName(scopedIdentifierTag), 
                files, 
                password);
    }

    private String getZipFileName(String tag) {
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_hh_mm_ss");
        return String.format("%s_%s.zip", "tesuto_static_file_" + tag + "_", sdf.format(new Date()));
    }

}
