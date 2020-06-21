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
package org.cccnext.tesuto.rules.drl;

import org.junit.Before;
import org.junit.Test;

public class ReadingEslPlacementRulesTest extends DecisionTreeTestBed {
    
	private static final String READING_FILE_SUFFIX = "ReadingPlacement";
	
	private static final String ESL_SUFFFIX = "EslPlacement";

    private static final String  HEADER_FOOTER_FILE_NAME = "MultipleMeasurePlacement";
    
    @Before
    public void setUp() {
       
    }
    
	@Test
	public void testRangeOfReadingTransferLevelFacts() throws Exception {
		testRangeOfFacts( READING_FILE_SUFFIX,READING_FILE_SUFFIX,  HEADER_FOOTER_FILE_NAME);
	}
	
	@Test
	public void testRangeOfESLTransferLevelFacts() throws Exception {
		testRangeOfFacts( ESL_SUFFFIX,ESL_SUFFFIX,  HEADER_FOOTER_FILE_NAME);
	}

}
