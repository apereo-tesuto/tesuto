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

import org.apache.commons.io.IOUtils;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "qtiResourceLocator")
public class QtiResourceLocator implements ResourceLocator {
    private List<ValidationMessage> errors = new ArrayList<>();

    @Autowired
    QtiImportValidationErrorService qtiImportValidationErrorService;

    public List<ValidationMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationMessage> errors) {
        this.errors = errors;
    }

    public void clearErrors(){
        setErrors(new ArrayList<>());
    }

    @Override
    public InputStream findResource(URI uri) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(uri.getPath());
            if(log.isTraceEnabled()) {
                InputStream is = new FileInputStream(uri.getPath());
                String fileContents = IOUtils.toString(is, StandardCharsets.UTF_8);
                log.trace(fileContents);
            }
        } catch (Exception e) {
            errors.addAll(qtiImportValidationErrorService.getErrors(e));
        }
        return inputStream;
    }
}
