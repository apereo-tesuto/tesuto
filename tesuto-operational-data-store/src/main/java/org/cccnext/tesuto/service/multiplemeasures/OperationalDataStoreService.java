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
package org.cccnext.tesuto.service.multiplemeasures;

import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;

import java.util.Set;

public interface OperationalDataStoreService {

    public static String SELF_REPORTED = "Self Reported";
    public static String VERIFIED = "Verified";
    public static String APPLY_SOURCE = "CCCApply";
    public static String CALPAS_SOURCE = "Calpass";
    public static String PLACEMENT = "Placement";
    
    void addVariableSet(String cccId, VariableSet variableSet);  //facts must be populated
    void createStudent(Student student);
    void createStudentTable();
    void createVariableSetTable();
    Student fetchStudent(String cccId);
    VariableSet fetchVariableSetById(String cccId, String variableSetId);
    Set<VariableSet> fetchStudentFacts(String cccId);
    VariableSet fetchFacts(String cccId, VariableSet variableSet);
    void setRegionByName(String regionName);
}
