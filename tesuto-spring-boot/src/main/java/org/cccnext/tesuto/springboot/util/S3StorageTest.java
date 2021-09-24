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
package org.cccnext.tesuto.springboot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.cccnext.tesuto.domain.util.S3Storage;
import org.cccnext.tesuto.domain.util.StaticStorage;
import org.cccnext.tesuto.springboot.servlet.ProfilesInitializer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class is for S3 testing. Right now the context is configured to bring in
 * a context that configures and tests an S3 connection. It can be switched in
 * properties/test-config.propertes to local. The rename command will fail but
 * the rest of the tests will run. The store and delete operations fail
 * silently.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/S3tImportContext.xml" }, initializers = ProfilesInitializer.class)
public class S3StorageTest {

    Random rg = new Random(System.currentTimeMillis());
    @Resource(name = "staticStorage")
    StaticStorage s3Storage;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStore() throws Exception {
        System.out.println("S3Storage.store(String key, File file)");
        String filename = "temp1.txt";
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String testContent = "This is test content\nAnother line of nonsense.";
        fileOutputStream.write(testContent.getBytes());
        s3Storage.store(filename, file);
        assertFalse(file.exists());

        // Clean up S3
        s3Storage.delete("temp1.txt");
    }

    @Test
    public void testStore1() throws Exception {
        System.out.println("S3Storage.store(String key, File file, boolean deleteFile)");
        String filename = "temp2.txt";
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String testContent = "This is test content\nAnother line of nonsense.";
        fileOutputStream.write(testContent.getBytes());
        s3Storage.store(filename, file, false); // The true case is tested
                                                // above.
        assertTrue(file.exists()); // The local file should still exist at this
                                   // point.
        file.delete(); // Clean up resources and remove the temporary file.

        // Clean up S3
        s3Storage.delete("temp2.txt");
    }

    @Test
    public void testDelete() throws Exception {
        System.out.println("S3Storage.delete(String key)");
        String validKey = createValidKey();
        String filename = "temp3.txt";
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String testContent = "This is test content\nAnother line of nonsense.";
        fileOutputStream.write(testContent.getBytes());
        s3Storage.store(String.format("%s%s", validKey, filename), file);
        InputStream inputStream = s3Storage.getFile(String.format("%s%s", validKey, filename));
        assertNotNull(inputStream);
        s3Storage.delete(String.format("%s%s", validKey, filename));
        inputStream = s3Storage.getFile(String.format("%s%s", validKey, filename));
        assertNull(inputStream);
    }

    @Test
    public void testRenameObject() throws Exception {
        System.out.println("S3Storage.renameObject(String sourceKey, String destinationKey)");
        String validKey = createValidKey();
        String filename = "temp4.txt";
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        String testContent = "This is test content\nAnother line of nonsense.";
        fileOutputStream.write(testContent.getBytes());
        s3Storage.store(String.format("%s%s", validKey, filename), file);

        String filename1 = "temp5.txt";
        s3Storage.renameObject(String.format("%s%s", validKey, filename), String.format("%s%s", validKey, filename1));
        InputStream inputStream = s3Storage.getFile(String.format("%s%s", validKey, filename));
        assertNull(inputStream);
        inputStream = s3Storage.getFile(String.format("%s%s", validKey, filename1));
        if (s3Storage instanceof S3Storage) {
            // Only check this if right class is being injected. This is
            // controlled by Spring Profiles.
            assertNotNull(inputStream);
        }

        // Clean up S3
        s3Storage.delete(String.format("%s%s", validKey, filename1));
    }

    String createValidKey() {
        StringBuilder validKey = new StringBuilder();
        validKey.append("DEVELOPER").append("/").append("item").append("/").append(UUID.randomUUID().toString())
                .append("/").append("0").append("/");
        return validKey.toString();
    }

    @Test
    public void testGetFilenameExtension() throws Exception {
        System.out.println("S3Storage.getFilenameExtension(String filename)");
        String filename = "temp.txt";
        String result = s3Storage.getFilenameExtension(filename);
        assertEquals("txt", result);
    }

    @Test
    public void testGetPartitionNumber() throws Exception {
        System.out.println("S3Storage.getPartitionNumber(int id)");
        int result = s3Storage.getPartitionNumber(9999);
        assertEquals(1, result);
        result = s3Storage.getPartitionNumber(10000);
        assertEquals(2, result);
        result = s3Storage.getPartitionNumber(10001);
        assertEquals(2, result);
        result = s3Storage.getPartitionNumber(20000);
        assertEquals(3, result);
        result = s3Storage.getPartitionNumber(20001);
        assertEquals(3, result);
    }

    /**
     * String namespace, String type, String id, String version
     * 
     * @throws Exception
     */
    @Test
    public void testMediaStructure() throws Exception {
        System.out.println("S3Storage.mediaStructure(String namespace, String type, String id, String version)");
        String expectedResult = "lsi/item/123/1/";
        String actualResult = s3Storage.mediaStructure("lsi", "item", "123", "1");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetFileWhenGivenAnExpectedPathPatternWillNotThrowException() throws Exception {
        StringBuilder key = new StringBuilder();
        key.append(getRandomValidNamespace()).append("/").append(getRandomValidType()).append("/")
                .append(UUID.randomUUID().toString()).append("/").append(getRandomValidVersion()).append("/")
                .append(UUID.randomUUID().toString()).append("/").append(UUID.randomUUID().toString());

        boolean exceptionThrown = false;
        try {
            s3Storage.getFile(key.toString());
        } catch (FileNotFoundException e) {
            exceptionThrown = true;
        } finally {
            assertFalse(exceptionThrown);
        }
    }

    @Test
    public void testGetFileWhenGivenRandomInvalidPathWillThrowException() throws Exception {
        try {
            s3Storage.getFile(createPathWithCorrectVariablesInTheWrongOrder());
            assertFalse("A FileNotFoundException was expected", true);
        } catch (FileNotFoundException e) {
            //This is expected
        }
    }

    public String createPathWithCorrectVariablesInTheWrongOrder() {
        StringBuilder invalidPath = new StringBuilder();
        List<String> keys = new ArrayList<>();
        keys.add("DEVELOPER");
        keys.add("item");
        keys.add(UUID.randomUUID().toString());
        keys.add("0");
        keys.add(UUID.randomUUID().toString());
        keys.add(UUID.randomUUID().toString());

        int numberOfPathVariables = keys.size();

        int missingPathVariable = rg.nextInt(numberOfPathVariables) + 1;

        // Path variable will be in wrong order or duplicated
        keys.forEach(aKey -> invalidPath.append(keys.get(rg.nextInt(missingPathVariable))).append("/"));

        StringBuilder validKey = new StringBuilder();
        validKey.append("DEVELOPER").append("/").append("item").append("/").append(UUID.randomUUID().toString())
                .append("/").append("0").append("/").append(UUID.randomUUID().toString()).append("/")
                .append(UUID.randomUUID().toString()).append("/");

        if (validKey.toString().equals(invalidPath.toString())) {
            return createPathWithCorrectVariablesInTheWrongOrder().toString();
        }

        return invalidPath.toString();
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetFileWhenGivenInvalideNameSpaceWillThrowException() throws Exception {
        StringBuilder key = new StringBuilder();
        key.append("    ").append("/") // invalid namespace
                .append(getRandomValidType()).append("/").append(UUID.randomUUID().toString()).append("/")
                .append(getRandomValidVersion()).append("/").append(UUID.randomUUID().toString()).append("/")
                .append(UUID.randomUUID().toString());

        s3Storage.getFile(key.toString());
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetFileWhenGivenInvalidTypeWillThrowException() throws Exception {
        StringBuilder key = new StringBuilder();
        key.append(getRandomValidNamespace()).append("/").append(UUID.randomUUID().toString()).append("/") // invalid
                                                                                                           // type
                .append(UUID.randomUUID().toString()).append("/").append(getRandomValidVersion()).append("/")
                .append(UUID.randomUUID().toString()).append("/").append(UUID.randomUUID().toString());

        s3Storage.getFile(key.toString());
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetFileWhenGivenInvalidVersionWillThrowException() throws Exception {
        StringBuilder key = new StringBuilder();
        key.append(getRandomValidNamespace()).append("/").append(UUID.randomUUID().toString()).append("/")
                .append(UUID.randomUUID().toString()).append("/").append(getRandomInvalidVersionNumber()).append("/") // invalid
                                                                                                                      // version
                .append(UUID.randomUUID().toString()).append("/").append(UUID.randomUUID().toString());

        s3Storage.getFile(key.toString());
    }

    public String getRandomValidNamespace() throws Exception {
        return rg.nextBoolean() ? "DEVELOPER" : "LSI";
    }

    public String getRandomValidType() throws Exception {
        return rg.nextBoolean() ? "assessment" : "item";
    }

    public String getRandomValidVersion() {
        return String.valueOf(rg.nextInt(10000));
    }

    public String getRandomInvalidVersionNumber() {
        if (rg.nextBoolean())
            return String.valueOf((0 - rg.nextInt(10000)));
        return UUID.randomUUID().toString();
    }

}
