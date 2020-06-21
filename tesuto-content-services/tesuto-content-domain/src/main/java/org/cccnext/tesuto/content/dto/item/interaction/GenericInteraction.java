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
package org.cccnext.tesuto.content.dto.item.interaction;

import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.running.InteractionBindingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * Created this class strictly for interaction serialization for JQTI Plus
 * nodes. This may or may not be useful in other contexts.
 *
 * TODO: This should be moved to a more suitable location like JQTI Works
 * extensions or something. -scott smith This is not part of the modelDtos.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class GenericInteraction extends Interaction {
    public static final String QTI_CLASS_NAME = "interaction";

    public GenericInteraction(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
    }

    public String getUiid() {
        return (String) this.getAttributes().getStringAttribute("uiid").getComputedValue();
    }

    public void setUiid(String uiid) {
        Attribute<String> uuidAttribute = new StringAttribute(this, "uiid", false);
        uuidAttribute.setValue(uiid);
        this.getAttributes().add(uuidAttribute);
    }

    @Override
    protected void validateThis(ValidationContext validationContext, ResponseDeclaration responseDeclaration) {
        // Should not be used -scott smith
    }

    @Override
    public boolean validateResponse(InteractionBindingContext interactionBindingContext, Value value) {
        // Should not be used.
        return false;
    }

}
