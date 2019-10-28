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
package org.cccnext.tesuto.springboot.web.security;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotEquals;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/test-context.xml"})
public class BCryptPasswordEncoderWrapperTest {

    @Resource(name = "bCryptPasswordEncoderWrapper")
    BCryptPasswordEncoderWrapper bCryptPasswordEncoderWrapper;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEncodePassword() throws Exception {
        System.out.println("Test BCryptPasswordEncoderWrapper.encodePassword()");
        System.out.println("This is salted so 2 invocations will yeild a different hash.");
        String password = "uni.dev";
        String encodedPassword1 = bCryptPasswordEncoderWrapper.encode(password);
        String encodedPassword2 = bCryptPasswordEncoderWrapper.encode(password);
        assertNotEquals(encodedPassword1, encodedPassword2);
        System.out.println(password);
        System.out.println(encodedPassword1);
        System.out.println(encodedPassword2);
    }

    @Test
    public void testIsPasswordValid() throws Exception {

    }
}
