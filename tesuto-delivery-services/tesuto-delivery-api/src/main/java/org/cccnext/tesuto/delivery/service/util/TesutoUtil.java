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
package org.cccnext.tesuto.delivery.service.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.content.model.DeliveryType;
//TODO  SECURITY ISSUE!!! Need to remove this from session and add to user JWT or add to assessment user session 
public class TesutoUtil {

    private static final String TESUTO_DELIVERY_PERMISSIONS = "tesuto-delivery-permissions";

    static public boolean hasAssessmentSessionPermission(HttpSession session, String assessmentSessionId, DeliveryType type) {
        //Map<String, DeliveryType> permissions =  (Map<String, DeliveryType>) session.getAttribute(TESUTO_DELIVERY_PERMISSIONS);
        //return permissions != null && type.equals(permissions.get(assessmentSessionId));
    	return true;
    }


    static public boolean hasAssessmentSessionPermission(HttpSession session, String assessmentSessionId) {
        //Map<String, DeliveryType> permissions =  (Map<String, DeliveryType>) session.getAttribute(TESUTO_DELIVERY_PERMISSIONS);
        //return permissions != null && permissions.get(assessmentSessionId) != null;
    	return true;
    }


    static public boolean hasAssessmentSessionPermission(HttpSession session) {
        //Map<String, DeliveryType> permissions =  (Map<String, DeliveryType>) session.getAttribute(TESUTO_DELIVERY_PERMISSIONS);
        //return permissions != null && permissions.size() > 0;
    	return true;
    }


    static public void resetAssessmentSessionPermissions(HttpSession session) {
        session.setAttribute(TESUTO_DELIVERY_PERMISSIONS, new HashMap<String,DeliveryType>());
    }

    static public void addAssessmentSessionPermission(HttpSession session, String assessmentSessionId, DeliveryType type) {
        if (session.getAttribute(TESUTO_DELIVERY_PERMISSIONS) == null) {
            resetAssessmentSessionPermissions(session);
        }
        ((Map<String,DeliveryType>)session.getAttribute(TESUTO_DELIVERY_PERMISSIONS)).put(assessmentSessionId, type);
    }
}
