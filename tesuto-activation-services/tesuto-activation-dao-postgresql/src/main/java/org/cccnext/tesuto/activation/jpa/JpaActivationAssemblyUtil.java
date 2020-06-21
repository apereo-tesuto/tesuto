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
package org.cccnext.tesuto.activation.jpa;

import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

import java.util.stream.Collectors;

/**
 * Created by bruce on 12/16/16.
 */
public interface JpaActivationAssemblyUtil {

    //mixin methods for more specific assemblers

    default  void assembly(Activation activation, JpaActivation jpa) {
        activation.setAssessmentScopedIdentifier(new ScopedIdentifierDto(jpa.getNamespace(), jpa.getAssessmentIdentifier()));
        activation.setAllAttributes(null); //these are currently hidden from the end use
        activation.setStatusChangeHistory(
                jpa.getStatusChangeHistory().stream().map(change -> change.getActivationStatusChange()).collect(Collectors.toList())
        );
    }

    default void disassembly(JpaActivation jpa, Activation activation) {
        jpa.setStatusForSearch(activation.getStatus());
        jpa.setAssessmentIdentifier(activation.getAssessmentScopedIdentifier().getIdentifier());
        jpa.setNamespace(activation.getAssessmentScopedIdentifier().getNamespace());
        jpa.setAttributes(activation.getAttributes());
        jpa.setActivationStatusChangeHistory(activation.getStatusChangeHistory());
    }
}
