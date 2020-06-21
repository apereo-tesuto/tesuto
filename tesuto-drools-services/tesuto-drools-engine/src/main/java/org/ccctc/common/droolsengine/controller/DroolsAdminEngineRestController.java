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
package org.ccctc.common.droolsengine.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ccctc.common.droolscommon.exceptions.DrlInvalidSyntaxException;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;
import org.ccctc.common.droolsengine.config.service.IDroolsRulesServiceFactory;
import org.ccctc.common.droolsengine.config.service.IServiceConfiguratorFactory;
import org.ccctc.common.droolsengine.dto.EngineConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = DroolsAdminEngineRestController.REQUEST_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
public class DroolsAdminEngineRestController {
    static final String REQUEST_ROOT = "/ccc/api/drools/v1/engine";

    @Autowired
    private IFamilyConfiguratorFactory familyConfiguratorFactory;

    @Autowired
    private IServiceConfiguratorFactory serviceConfiguratorFactory;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getEngineStatus() {
        Map<String, Object> results = new HashMap<>();

        IDroolsRulesServiceFactory serviceFactory = serviceConfiguratorFactory.getDroolsRulesServiceFactory();
        List<EngineConfigDTO> engineConfigs = serviceFactory.getEngineConfigurations(new ArrayList<String>());
        results.put("engines", engineConfigs);

        IFamilyConfigurator familyConfigurator = familyConfiguratorFactory.getConfigurator();
        List<FamilyDTO> families = familyConfigurator.getFamilies(true);
        results.put("colleges", families);

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(results, status);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping(method = RequestMethod.POST, value = "/loadRules")
    public ResponseEntity<DrlValidationResults> loadRules() {
        DrlValidationResults results = new DrlValidationResults();
        try {
            // TODO need to handle containerService.loadRules()
            // droolsRuleService.loadAllRules();
            results.setIsValid(true);
            return new ResponseEntity(results, HttpStatus.OK);
        }
        catch (DrlInvalidSyntaxException exception) {
            results.setIsValid(false);
            results.setDrl(exception.getDrl());
            results.setExceptionMessage(exception.getMessage());
            results.setExceptionTrace(ExceptionUtils.getStackTrace(exception));
            results.setErrors(exception.getErrors());
            return new ResponseEntity(results, HttpStatus.ACCEPTED);
        }
        catch (Exception exception) {
            results.setIsValid(false);
            results.setExceptionMessage(exception.getMessage());
            results.setExceptionTrace(ExceptionUtils.getStackTrace(exception));
            return new ResponseEntity(results, HttpStatus.ACCEPTED);
        }
    }
}
