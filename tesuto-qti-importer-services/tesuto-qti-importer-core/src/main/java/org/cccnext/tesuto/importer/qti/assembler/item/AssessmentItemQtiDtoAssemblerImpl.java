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
package org.cccnext.tesuto.importer.qti.assembler.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateDeclarationDto;
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateProcessingDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.GenericInteraction;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.importer.qti.assembler.item.interaction.AssessmentInteractionQtiDtoFactory;
import org.cccnext.tesuto.importer.qti.assembler.shared.AssessmentOutcomeDeclarationQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.service.QtiResourceLocator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ed.ph.jqtiplus.exception.QtiNodeGroupException;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.Stylesheet;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ExtendedTextInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.InlineChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.TextEntryInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentItemQtiDtoAssembler")
public class AssessmentItemQtiDtoAssemblerImpl implements AssessmentItemQtiDtoAssembler {
    // This is cop out code. I can't think of another way to make it simpler. I
    // apologize for inflicting it. -scott smith
    // Okay, we're going to need to pass this around down below, so I'm
    // declaring it here.
    // And we need to support multiple interactions per page. I feel like stuff
    // is getting slightly crusty.
    private static final ThreadLocal<ArrayList<String>> sequentialArrayListOfGuids = new ThreadLocal<ArrayList<String>>() {
        @Override
        protected ArrayList<String> initialValue() {
            return new ArrayList<String>();
        }
    };
    private static final ThreadLocal<Integer> threadedStackFrameGuidIndex = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(-1);
        }
    };

    @Autowired
    AssessmentInteractionQtiDtoFactory assessmentInteractionFactory;
    @Autowired
    AssessmentResponseVarQtiDtoAssembler responseVarQtiDtoAssembler;
    @Autowired
    AssessmentOutcomeDeclarationQtiDtoAssembler outcomeDeclarationQtiDtoAssembler;
    @Autowired
    AssessmentTemplateDeclarationQtiDtoAssembler templateDeclarationQtiDtoAssembler;
    @Autowired
    AssessmentTemplateProcessingQtiDtoAssembler templateProcessingQtiDtoAssembler;
    @Autowired
    AssessmentStimulusRefQtiDtoAssembler assessmentStimulusRefQtiDtoAssembler;
    @Autowired
    AssessmentResponseProcessingQtiDtoAssembler responseProcessingQtiDtoAssembler;
    @Autowired
    QtiSerializer qtiSerializer;
    @Autowired
    QtiResourceLocator qtiResourceLocator;

    @Override
    public AssessmentItemDto assembleDto(AssessmentItem assessmentItem) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItem == null) {
            return null;
        }

        AssessmentItemDto assessmentItemDto = new AssessmentItemDto();
        assessmentItemDto.setIdentifier(assessmentItem.getIdentifier());

        ItemBody itemBody = assessmentItem.getItemBody();
        log.debug("itemBody interactions: {}", itemBody.findInteractions());
        // TODO: Figure out how to filter out the feedback elements!!
        List<AssessmentInteractionDto> interactions = new LinkedList<AssessmentInteractionDto>();
        List<Interaction> interactionList = itemBody.findInteractions();
        for (Interaction interaction : interactionList) {
            AssessmentInteractionDto interactionDto = assessmentInteractionFactory.createAssessment(interaction);
            sequentialArrayListOfGuids.get().add(interactionDto.getUiid());
            interactions.add(interactionDto);
        }
        assessmentItemDto.setInteractions(interactions);
        // We need to grab pieces out of the item body before we remove them, so
        // keep this after the choice interactions and feedback!
        // TODO: There is room for performance improvements here because of
        // walking the tree.
        filterInteractions(itemBody); // miserable recursive call
        assessmentItemDto.setBody(qtiSerializer.serializeJqtiObject(itemBody));

        assessmentItemDto.setLabel(assessmentItem.getLabel());
        // TODO: This lang thing is still broken and I have idea why right now.
        /*
         * if (assessmentItem.getLang() != null) {
         * assessmentItemDto.setLanguage(assessmentItem.getLang()); }
         */
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarations = new LinkedList<AssessmentOutcomeDeclarationDto>();
        for (OutcomeDeclaration outcomeDeclaration : assessmentItem.getOutcomeDeclarations()) {
            AssessmentOutcomeDeclarationDto outcomeDeclarationDto = outcomeDeclarationQtiDtoAssembler.assembleDto(outcomeDeclaration);
            outcomeDeclarations.add(outcomeDeclarationDto);
        }
        assessmentItemDto.setOutcomeDeclarationDtos(outcomeDeclarations);
        AssessmentResponseProcessingDto responseProcessingDto = responseProcessingQtiDtoAssembler
                .assembleDto(assessmentItem.getResponseProcessing());
        assessmentItemDto.setResponseProcessing(responseProcessingDto);
        List<AssessmentResponseVarDto> responseVars = new LinkedList<AssessmentResponseVarDto>();
        for (ResponseDeclaration responseDeclaration : assessmentItem.getResponseDeclarations()) {
            AssessmentResponseVarDto responseVarDto = responseVarQtiDtoAssembler.assembleDto(responseDeclaration);
            responseVars.add(responseVarDto);
        }
        assessmentItemDto.setResponseVars(responseVars);
        // TODO: Figure out where to get this! Not for Pilot. Potential
        // recursive or circular object graphs.
        assessmentItemDto.setStimulusRef(null);
        AssessmentTemplateProcessingDto templateProcessingDto = templateProcessingQtiDtoAssembler
                .assembleDto(assessmentItem.getTemplateProcessing());
        log.debug("TemplateProcessingDto: {}", templateProcessingDto);
        assessmentItemDto.setTemplateProcessing(templateProcessingDto);
        List<AssessmentTemplateDeclarationDto> templateDeclarations = new LinkedList<AssessmentTemplateDeclarationDto>();
        for (TemplateDeclaration templateDeclaration : assessmentItem.getTemplateDeclarations()) {
            AssessmentTemplateDeclarationDto templateDeclarationDto = templateDeclarationQtiDtoAssembler
                    .assembleDto(templateDeclaration);
            templateDeclarations.add(templateDeclarationDto);
        }
        assessmentItemDto.setTemplateVars(templateDeclarations);
        assessmentItemDto.setTitle(assessmentItem.getTitle());
        assessmentItemDto.setToolName(assessmentItem.getToolName());
        assessmentItemDto.setToolVersion(assessmentItem.getToolVersion());

        // Handle stylesheets, look this is similar to
        // AssessmentQtiDtoAssemblerImpl.java
        List<Stylesheet> stylesheetList = assessmentItem.getNodeGroups().getStylesheetGroup().getStylesheets();
        StringBuilder stringBuilder = new StringBuilder();
        XmlSourceLocationInformation uploadItem = assessmentItem.getSourceLocation();
        File uploadItemFile = new File(uploadItem.getSystemId());
        StringBuilder uploadLocation = new StringBuilder(uploadItemFile.getParent()).append(File.separatorChar);
        for (Stylesheet stylesheet : stylesheetList) {
            StringBuilder cssPath = new StringBuilder(uploadLocation);
            URI cssUri = null;
            try {
                cssUri = new URI(cssPath.append(stylesheet.getHref()).toString());
            } catch (URISyntaxException e) {
                // TODO: Consider a throwing a RuntimeException here.
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
        assessmentItemDto.setStylesheets(stringBuilder.toString());

        return assessmentItemDto;
    }

    @Override
    public AssessmentItem disassembleDto(AssessmentItemDto assessmentItemDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }

    /**
     * I hate this method. And maybe it should be public for testing.
     *
     * @param abstractNode
     */
    private void filterInteractions(AbstractNode abstractNode) {
        // What a nightmare, recursive until there are no more child nodes.
        if (abstractNode.hasChildNodes()) {
            try {
                for (NodeGroup nodeGroup : abstractNode.getNodeGroups()) {
                    List<QtiNode> groupList = nodeGroup.getChildren();
                    for (int i = 0; i < groupList.size(); ++i) {
                        QtiNode qtiNode = groupList.get(i);
                        log.debug("qtiNode: {}", qtiNode.toString());
                        QtiNode parent = qtiNode.getParent();
                        // Do stuff to all nodes of proper type.
                        if (qtiNode instanceof Interaction) { // TODO: A simple
                                                              // choice is not
                                                              // an interaction!
                            // There are many different types of interactions
                            if (qtiNode instanceof ChoiceInteraction) {
                                groupList.remove(i);
                                GenericInteraction genericInteraction = getGenericInteraction(qtiNode,
                                        ChoiceInteraction.class);
                                groupList.add(i, genericInteraction);
                            } else if (qtiNode instanceof TextEntryInteraction) {
                                groupList.remove(i);
                                GenericInteraction genericInteraction = getGenericInteraction(qtiNode,
                                        TextEntryInteraction.class);
                                groupList.add(i, genericInteraction);
                            } else if (qtiNode instanceof ExtendedTextInteraction) {
                                groupList.remove(i);
                                GenericInteraction genericInteraction = getGenericInteraction(qtiNode,
                                        ExtendedTextInteraction.class);
                                groupList.add(i, genericInteraction);
                            } else if (qtiNode instanceof InlineChoiceInteraction) {
                                groupList.remove(i);
                                GenericInteraction genericInteraction = getGenericInteraction(qtiNode,
                                        InlineChoiceInteraction.class);
                                groupList.add(i, genericInteraction);
                            } else if (qtiNode instanceof MatchInteraction) {
                                groupList.remove(i);
                                GenericInteraction genericInteraction = getGenericInteraction(qtiNode,
                                        MatchInteraction.class);
                                groupList.add(i, genericInteraction);
                            } else {
                                // An interaction that is not supported.
                            }
                        }
                        // Recursion, infinitely if needed, by calling yourself
                        // here.
                        if (qtiNode.hasChildNodes() && qtiNode instanceof AbstractNode) {
                            filterInteractions((AbstractNode) qtiNode);
                        }
                    }
                }
            } catch (QtiNodeGroupException qnge) {
                // This is bogus, it just means there are no node groups. Really
                // an exception should not have been
                // programmed to be thrown here.
            }
        }
    }

    private GenericInteraction getGenericInteraction(QtiNode qtiNode, Class<? extends Interaction> clazz) {
        int tsfgi = threadedStackFrameGuidIndex.get().intValue();
        threadedStackFrameGuidIndex.set(++tsfgi);
        GenericInteraction genericInteraction = new GenericInteraction(qtiNode.getParent());
        genericInteraction.setResponseIdentifier((clazz.cast(qtiNode)).getResponseIdentifier());
        genericInteraction.setUiid(sequentialArrayListOfGuids.get().get(tsfgi));
        return genericInteraction;
    }
}
