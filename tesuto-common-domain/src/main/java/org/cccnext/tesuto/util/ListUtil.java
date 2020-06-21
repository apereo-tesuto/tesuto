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
package org.cccnext.tesuto.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 8/26/16.
 */
public class ListUtil {

    static public boolean equals(List<Double> doubleList1, List<Double> doubleList2){
        if(doubleList1.size() != doubleList2.size()){
            return false;
        }

        Object doubles1[] = doubleList1.toArray();
        Object doubles2[] = doubleList2.toArray();

        Arrays.sort(doubles1);
        Arrays.sort(doubles2);

        return Arrays.equals(doubles1, doubles2);
    }
}
