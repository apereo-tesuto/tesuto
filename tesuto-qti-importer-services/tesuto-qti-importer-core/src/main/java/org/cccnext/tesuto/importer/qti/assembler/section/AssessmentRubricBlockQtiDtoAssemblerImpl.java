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
package org.cccnext.tesuto.importer.qti.assembler.section;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.group.NodeGroup;
import uk.ac.ed.ph.jqtiplus.group.NodeGroupList;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractAtomicBlock;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.test.View;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.section.AssessmentRubricBlockDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "rubricBlockQtiDtoAssembler")
public class AssessmentRubricBlockQtiDtoAssemblerImpl implements AssessmentRubricBlockQtiDtoAssembler {
    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentRubricBlockDto assembleDto(RubricBlock rubricBlock) {
        // Drop out immediately if there is nothing to assemble.
        if (rubricBlock == null) {
            return null;
        }

        AssessmentRubricBlockDto rubricBlockDto = new AssessmentRubricBlockDto();
        // TODO: There Rubric Blocks within blocks. So things might need to
        // become a recursive call. rubricBlock.getBlocks();
        // Blocks can have a ton of different manifestations. We need to decide
        // which ones to handle.
        log.debug("Rubric Block: " + rubricBlock.getAttributes()); // Remove
                                                                      // when
                                                                      // all is
                                                                      // determined
                                                                      // ;-)

        // TODO this needs to be wired up as a spring bean so we can get rid of
        // all the other un needed DOM details in the strings.
        final SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
        saxFiringOptions.setOmitSchemaLocation(true);
        final XsltSerializationOptions xsltSerializationOptions = new XsltSerializationOptions();
        xsltSerializationOptions.setIncludingXMLDeclaration(false);
        rubricBlockDto.setContent(qtiSerializer
                .serializeJqtiObject(rubricBlock.getBlocks().get(0), saxFiringOptions, xsltSerializationOptions)
                .toString());

        // TODO: This is OK for FEB release. QTIWorks does not currently support
        // use attribute for rubricblock.
        // https://github.com/davemckain/qtiworks/wiki/QTIWorks-conformance-and-implementation-notes
        rubricBlockDto.setUse("sharedStimulus");
        List<String> viewList = new LinkedList<String>();
        for (View view : rubricBlock.getViews()) {
            viewList.add(view.toString());
        }
        rubricBlockDto.setViews(viewList);
        // TODO: Do we need the stylesheets? We might not be able to get by
        // without them.

        return rubricBlockDto;
    }

    @Override
    public RubricBlock disassembleDto(AssessmentRubricBlockDto rubricBlockDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
