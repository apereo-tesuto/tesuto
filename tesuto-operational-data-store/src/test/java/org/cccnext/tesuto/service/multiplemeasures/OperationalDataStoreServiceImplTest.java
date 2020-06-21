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
package  org.cccnext.tesuto.service.multiplemeasures;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OperationalDataStoreServiceImplTest {

	private static AmazonDynamoDB db;
	private OperationalDataStoreServiceImpl ods;
	private static String key = "secret";

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("sqlite4java.library.path", "native-libs");
		db = DynamoDBEmbedded.create().amazonDynamoDB();
	}

	@Before
	public void before() throws Exception {
		ods = new OperationalDataStoreServiceImpl();
		ods.setClient(db);
		ods.setStudentTableName("students", null);
		ods.setVariableSetTableName("variableSets", null);
		ods.createStudentTable();
		ods.createVariableSetTable();
	}

	@AfterClass
	public static void teardown() {
		db.shutdown();
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

	private Student createStudent() {
		Student student = new Student();
		student.setCccId(randomString());
		student.setSsId(randomString());
		return student;
	}

	private Fact createFact() {
		Fact fact = new Fact();
		fact.setName(randomString());
		fact.setValue(randomString());
		fact.setSource(randomString());
		fact.setSourceDate(new Date());
		fact.setSourceType(randomString());
		return fact;
	}

    private VariableSet createVariableSet() {
		VariableSet variableSet = new VariableSet();
		variableSet.setId(randomString());
		variableSet.setMisCode(randomString());
		variableSet.setCreateDate(new Date());
		variableSet.setSource(randomString());
		variableSet.setSourceDate(new Date());
		variableSet.setSourceType(randomString());
		variableSet.setFacts(new HashMap<>());
		Fact fact = createFact();
		variableSet.getFacts().put(fact.getName(), fact);
		return variableSet;
	}

	@Test
	public void createStudentTest() {
		Student student = createStudent();
		ods.createStudent(student);
		Student fetched = ods.fetchStudent(student.getCccId());
		assertEquals(student, fetched);
	}


	@Test
	public void addVariableSetTest() {
		Student student = createStudent();
		ods.createStudent(student);
		VariableSet variableSet = createVariableSet();
		ods.addVariableSet(student.getCccId(),variableSet);
		Student fetched = ods.fetchStudent(student.getCccId());
		assertTrue(fetched.getVariableSets().contains(variableSet));
		ods.addVariableSet(student.getCccId(),createVariableSet());
		fetched = ods.fetchStudent(student.getCccId());
		assertTrue(fetched.getVariableSets().contains(variableSet));
		assertEquals(2, fetched.getVariableSets().size());
		//Facts should not be stored with the student
		fetched.getVariableSets().forEach(vSet -> {
			assertTrue(vSet.getFacts() == null || vSet.getFacts().size() == 0);
		});
		VariableSet fetchedVariables = ods.fetchFacts(student.getCccId(), variableSet);
		assertEquals(variableSet, fetchedVariables);
		assertEquals(variableSet.getFacts(), fetchedVariables.getFacts());
	}

	@Test
	public void fetchStudentFactsTest() {
		Student student = createStudent();
		ods.createStudent(student);
		Set<VariableSet> variableSets = new HashSet<>();
		variableSets.add(createVariableSet());
		variableSets.add(createVariableSet());
		variableSets.forEach(vs -> ods.addVariableSet(student.getCccId(), vs));
		Set<VariableSet> fetched = ods.fetchStudentFacts(student.getCccId());
		assertEquals(variableSets, fetched);
	}
	
	@Test
    public void fetchStudentFactsByIdTest() {
        Student student = createStudent();
        ods.createStudent(student);
        Set<VariableSet> variableSets = new HashSet<>();
        VariableSet v = createVariableSet();
        variableSets.add(v);
        variableSets.forEach(vs -> ods.addVariableSet(student.getCccId(), vs));
        VariableSet fetched = ods.fetchVariableSetById(student.getCccId(), v.getId());
        assertEquals(v, fetched);
    }

}
