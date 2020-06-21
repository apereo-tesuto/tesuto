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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class IntersegmentKeyServiceImplTest {

    private IntersegmentKeyService keyService;

    private static final String EXPECTED_KEY =
            "2940DD601CA12C1EA3D9F2EDB3A73CE0B9537B6233F711714E89BB69F3323748BE7088D7DF7F987BCE0CC05B2488E00A17E95B0578ADDB8450FC565C166D4C4E";

    @Before
    public void before() {
        keyService = new IntersegmentKeyServiceImpl();
    }

    // main implementation

    @Test
    public void keyServiceProperlyHashesKeyGivenAllStrings() {
        String firstName = "Daniel";
        String lastName = "Lamoree";
        String gender = "M";
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertEquals("The expected key does not match the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceDoesNotExplodeWhenGivenANameLessThanThreeCharactersLong() {
        String firstName = "Da";
        String lastName = "Lamoree";
        String gender = "M";
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertNotEquals("The expected key matches the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceDoesNotExplodeWhenGivenAStringOfZeroLength() {
        String firstName = "Daniel";
        String lastName = "Lamoree";
        String gender = "";
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertNotEquals("The expected key matches the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceDoesNotExplodeWhenGivenANullString() {
        String firstName = null;
        String lastName = "Lamoree";
        String gender = "M";
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertNotEquals("The expected key matches the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceDoesNotExplodeWhenGivenABlankString() {
        String firstName = "Daniel";
        String lastName = "            ";
        String gender = "m";
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertNotEquals("The expected key matches the result key", EXPECTED_KEY, resultKey);
    }

    // convenience alternatives

    @Test
    public void keyServiceProperlyHashesKeyGivenACharGender() {
        String firstName = "Daniel";
        String lastName = "Lamoree";
        char gender = 'm';
        String birthDate = "19440606";

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertEquals("The expected key does not match the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceProperlyHashesKeyGivenAStandardDateObject() {
        String firstName = "Daniel";
        String lastName = "Lamoree";
        String gender = "M";
        Date birthDate = DateTime.now().withYear(1944).withMonthOfYear(6).withDayOfMonth(6).toDate();

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertEquals("The expected key does not match the result key", EXPECTED_KEY, resultKey);
    }

    @Test
    public void keyServiceProperlyHashesKeyWithCharGenderAndStandardDateObject() {
        String firstName = "Daniel";
        String lastName = "Lamoree";
        char gender = 'm';
        Date birthDate = DateTime.now().withYear(1944).withMonthOfYear(6).withDayOfMonth(6).toDate();

        String resultKey = keyService.generateKey(firstName, lastName, gender, birthDate);

        Assert.assertEquals("The expected key does not match the result key", EXPECTED_KEY, resultKey);
    }
}
