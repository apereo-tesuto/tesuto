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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;


import org.springframework.stereotype.Service;
import org.xml.sax.SAXParseException;

import uk.ac.ed.ph.jqtiplus.notification.Notification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "qtiImportValidationErrorService")
public class QtiImportValidationErrorServiceImpl implements QtiImportValidationErrorService {

    private HashMap<URI, ItemValidationResult> itemValidationResultHashMap = new HashMap<>();
    @Override
    public ValidatedNode<AssessmentObjectValidationResult> getErrorsForValidationResult(AssessmentObjectValidationResult<?> assessmentObjectValidationResult) {
        List<ValidationMessage> validationErrors = new ArrayList<>();
        List<ValidationMessage> validationMessages = new ArrayList<>();
        //Add all model validation errors
        for(NotificationLevel notificationLevel: NotificationLevel.values()){
            if (!notificationLevel.equals(NotificationLevel.INFO)) {
                for (Notification notification : assessmentObjectValidationResult.getNotificationsAtLevel(notificationLevel)) {
                    if(isValidNotification(notification, assessmentObjectValidationResult)) {
                        ValidationMessage validationMessage = new ValidationMessage();
                        if(notification.getQtiNode() != null) {
                            validationMessage.setColumn(notification.getQtiNode().getSourceLocation().getColumnNumber());
                            validationMessage.setLine(notification.getQtiNode().getSourceLocation().getLineNumber());
                            validationMessage.setFile(notification.getQtiNode().getSourceLocation().getSystemId());
                            validationMessage.setNode(notification.getQtiNode().getQtiClassName());
                        }
                        validationMessage.setMessage(notification.getMessage());

                        if (notificationLevel.equals(NotificationLevel.ERROR)) {
                            validationErrors.add(validationMessage);
                        } else if (notificationLevel.equals(NotificationLevel.WARNING)) {
                            validationMessages.add(validationMessage);
                        }
                    }
                }
            }
        }
        return new ValidatedNode<>(validationErrors, validationMessages, assessmentObjectValidationResult);
    }

    private boolean isValidNotification(Notification notification, AssessmentObjectValidationResult<?> assessmentObjectValidationResult ){
        log.debug("Checking notification {}", notification);
        boolean isValid = true;
        if(notification.getMessage().contains("Referenced item at System ID")){
            isValid = false;
        }else  if(notification.getMessage().matches("Test or referenced item variable referenced by identifier.*has not been declared")){
            isValid = false;
        }else if("Item variable referenced by identifier 'RESPONSE' has not been declared".equals(notification.getMessage())){
            isValid = false;
        }
        log.debug("Notification valid: {}", isValid);
        return isValid;
    }


    @Override
    public List<ValidationMessage> getErrors(Exception e) {
        List<ValidationMessage> errors = new ArrayList<>();
        if(e instanceof SAXParseException) {
            errors.add(getError((SAXParseException) e));
        }else if(e instanceof ImsManifestException) {
            errors.addAll(getErrors((ImsManifestException) e));
        }else if(e instanceof QtiXmlInterpretationException) {
            errors.addAll(getErrors((QtiXmlInterpretationException) e));
        }
        else if (e instanceof XmlResourceNotFoundException) {
            errors.add(getError((XmlResourceNotFoundException) e));
        }else if (e instanceof FileNotFoundException){
            errors.addAll(getErrors((FileNotFoundException) e));
        }else{
            ValidationMessage validationMessage = new ValidationMessage();
            validationMessage.setMessage(e.getMessage());
            errors.add(validationMessage);
        }
        return errors;
    }

    private ValidationMessage getError(XmlResourceNotFoundException xmlResourceNotFoundException){
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setMessage("IMS Manifest file was not found. Ensure imsmanifest.xml is at root and compress package again.");
        validationMessage.setFile(xmlResourceNotFoundException.getSystemId().toString());
        return validationMessage;
    }

    private ValidationMessage getError(SAXParseException saxParseException){
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setMessage(saxParseException.getMessage());
        validationMessage.setFile(saxParseException.getSystemId());
        validationMessage.setLine(saxParseException.getLineNumber());
        validationMessage.setColumn(saxParseException.getColumnNumber());
        if(StringUtils.isNotEmpty(saxParseException.getPublicId())){
            validationMessage.setNode(saxParseException.getPublicId());
        }
        return validationMessage;
    }

    private List<ValidationMessage> getErrors(QtiXmlInterpretationException qtiXmlInterpretationException){
        return getErrors(qtiXmlInterpretationException.getXmlParseResult());
    }

    private List<ValidationMessage> getErrors(ImsManifestException imsManifestException){
        return getErrors(imsManifestException.getXmlParseResult());
    }

    private List<ValidationMessage> getErrors(FileNotFoundException fileNotfoundException){
        List<ValidationMessage> errors = new ArrayList<>();
        ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setMessage("File not found.");
        validationMessage.setFile(fileNotfoundException.getMessage());
        errors.add(validationMessage);
        return errors;
    }

    private List<ValidationMessage> getErrors(XmlParseResult xmlParseResult){
        List<ValidationMessage> errors = new ArrayList<>();

        for(SAXParseException saxParseException: xmlParseResult.getFatalErrors()){
            errors.add(getError(saxParseException));
        }
        for(SAXParseException saxParseException: xmlParseResult.getErrors()){
            errors.add(getError(saxParseException));
        }

        //Schema validation failure
        for(String unsupportedNamespace: xmlParseResult.getUnsupportedSchemaNamespaces()){
            ValidationMessage validationMessage = new ValidationMessage();
            if(xmlParseResult.getSystemId() != null){
                validationMessage.setFile(xmlParseResult.getSystemId().toString());
            }
            validationMessage.setMessage(String.format("Unsupported schema location %s", unsupportedNamespace));
            errors.add(validationMessage);
        }

        return errors;
    }
}
