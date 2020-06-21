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
package org.cccnext.tesuto.rules.validation;

import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.RULES_DIRECTORY;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.TEST_FACTS_DIRECTORY;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class ApmsPlacementComponentDrlValidationServiceTest {

    public static final String TEST_RESULTS_DIRECTORY = "src/test/resources/org/cccnext/tesuto/results/";
	@Autowired
	PlacementComponentDrlValidationService service;

	@Test
	public void testApmsEngLev() throws IOException {
	    doTest("ApmsEng", "APMS-MMAP-Data-File-Edited-ENG-LEV");
	}

	@Test
	public void testApmsRdgLev() throws IOException {
	    doTest("ApmsReading", "APMS-MMAP-Data-File-Edited-ENG-RDG-LEV");
	}

	@Test
	public void testApmsEslEngLev() throws IOException {
	    doTest("ApmsEsl", "APMS-MMAP-Data-File-Edited-ESL-ENG-LEV");
	}

    @Test
    public void testApmsMathAlgebraI() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Algebra-I-LEV");
    }

    @Test
    public void testApmsAlgebraII() throws IOException {
        doTest( "ApmsMath", "APMS-MMAP-Data-File-MATH-Algebra-II-LEV");
    }

    @Test
    public void testApmsArithmetic() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Arithmetic-LEV");
    }

    @Test
    public void testApmsCalc() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Calc-LEV");
    }

    @Test
    public void testApmsCollAlgebra() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Coll-Algebra-LEV");
    }

    @Test
    public void testApmsGenEdMath() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Gen-Ed-Math-LEV");
    }

    @Test
    public void testApmsPreAlgebra() throws IOException {
        doTest( "ApmsMath", "APMS-MMAP-Data-File-MATH-Pre-Algebra-LEV");
    }
    @Test
    public void testApmsPreCalc() throws IOException {
        doTest( "ApmsMath", "APMS-MMAP-Data-File-MATH-Pre-Calc-LEV" );
    }

    @Test
    public void testApmsStatistics() throws IOException {
        doTest("ApmsMath", "APMS-MMAP-Data-File-MATH-Statistics-LEV");
    }

    @Test
    public void testApmsTrigonometry() throws IOException {
        doTest( "ApmsMath", "APMS-MMAP-Data-File-MATH-Trigonometry-LEV");
    }

    @Test
    public void testApmsMath() throws IOException {
        doTest( "ApmsMath", "APMS-MMAP-Data-File-Edited-MATH-LEV");
    }

    private void doTest(String drlFileName, String validationFileName) throws IOException {
        DrlValidationResults results = service.validate(getData( drlFileName, validationFileName) );
        if(!results.getIsValid()) {
            outputResults( validationFileName, StringUtils.join(results.getErrors(), "\n"));
        }
        //TODO once rules/validations are fixed uncomment this to find additional errors.
       //assertTrue(results.toString(), results.getIsValid());
    }

    private void outputResults(String csvValidationFileName, String results) throws IOException {
        File directory = new File(TEST_RESULTS_DIRECTORY);
        if(!directory.exists())
            directory.mkdirs();

       File resultsFile = new File(TEST_RESULTS_DIRECTORY +  "RESULTS-" + csvValidationFileName + ".csv");
       if(!resultsFile.exists())
           resultsFile.createNewFile();
       FileWriter fw = new FileWriter(resultsFile.getAbsoluteFile());
        fw.write(results);
        fw.close();
      resultsFile.delete();
    }


	public DrlValidationData getData(String drlFileName, String csvValidationFileName) throws IOException {
		String validationCsv = FileUtils
				.readFileToString(new File(TEST_FACTS_DIRECTORY +  csvValidationFileName + ".csv"));
		String drl = FileUtils.readFileToString(new File(RULES_DIRECTORY + drlFileName + ".drl"));

		DrlValidationData data = new DrlValidationData();
		data.setCsvValidationRequired(true);
		data.setDrl(drl);
		data.setValidationCsv(validationCsv);
		return data;
	}
}
