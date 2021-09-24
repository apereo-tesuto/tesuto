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
package org.cccnext.tesuto.delivery.qa;

import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.repository.mongo.AssessmentSessionRepository;
import org.cccnext.tesuto.delivery.service.PostDeliveryAssessmentCompletionService;
import org.cccnext.tesuto.user.service.UserAccountReader;


import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class RequestPlacementsSeedDataService {

    @Autowired
    PostDeliveryAssessmentCompletionService postDeliveryService;

    @Autowired
    AssessmentSessionRepository assessmentSessionRepository;

    @Autowired
    UserAccountReader userAccountService;


    public void seedData() {
            List<String> ccids = Arrays.asList(new String[] {
                    "A123456",
                    "A123457",
                    "A123458",
                    "A123459",
                    "A123460",
                    "AAA9643",
                    "AAA8345",
                    "AAA8346",
                    "AAA8347",
                    "AAA8349",
                    "AAA8350",
                    "AAA8351",
                    "AAA8352",
                    "AAA8353",
                    "AAP2174",
                    "AAP1600",
                    "AAP1601",
                    "AAP1602",
                    "AAP1603",
                    "AAP1604",
                    "AAP1605",
                    "AAP1607",
                    "AAP1608"
                    });
            for (String cccid : ccids) {
                List<AssessmentSession> assessmentSessions = assessmentSessionRepository.findByUserIdIgnoreCase(cccid);
                if (assessmentSessions == null) {
                    log.error("!!!!! Assesssment sessions in null for cccid " + cccid );
                    continue;
                }
                for (AssessmentSession a : assessmentSessions) {
                    if (a == null) {
                        log.error("!!!!! Assesssment in null for cccid " + cccid );
                        continue;
                    }
                    UserAccountDto u = userAccountService.getUserAccount(cccid);
                    if (u == null) {
                        log.error("!!!!! User account in null for cccid " + cccid );
                        continue;
                    }
                    // TODO: Figure out why this sometimes fails.
                    try {
                        postDeliveryService.requestPlacements(a, false);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            }
    }
}
