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
package org.cccnext.tesuto.domain.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class ZipFileExtractorTest {
    // We can just create this, it doesn't need to be spring managed although we
    // can make it so. -scott smith
    ZipFileExtractor zipFileExtractor = new ZipFileExtractor();

    @Test
    public void testExtract() throws Exception {

    }

    @Test
    public void testEscapeSpaces() throws Exception {
        String filename1 = "items/i1435681229372942/Novice Appt Card.JPG";
        String expResult = "items/i1435681229372942/Novice\\ Appt\\ Card.JPG";
        String result = zipFileExtractor.escapeSpaces(filename1);
        assertEquals(expResult, result);
    }
}
