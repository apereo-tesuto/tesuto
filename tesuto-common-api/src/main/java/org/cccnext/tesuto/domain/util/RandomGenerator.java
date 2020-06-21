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

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

/**
 * Class to generate random strings of a parameterized length.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Service
public class RandomGenerator {

	private static final String charset = "0123456789abcdefghijklmnopqrstuvwxyz";

	/**
	 * Generate a random String with only digits and lowercase characters.  Suitable
	 * for passwords and URLs.  It is synchronized to ensure that no 2 are the same
	 * because of the same seed.
	 *
	 * @param length Length of the random string
	 * @return A random string of lower case letters and numbers
	 */
	public synchronized String getRandomString(int length) throws InterruptedException {

		Thread.sleep(1);
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int pos = random.nextInt(charset.length());
			sb.append(charset.charAt(pos));
		}
		return sb.toString();
	}

	/**
	 * Generate a random number based on zero through the <code>length</code> parameter.
	 * @param length
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized int getRandomIndex(int length) throws InterruptedException {
		Thread.sleep(1);
		SecureRandom random = new SecureRandom();
		return random.nextInt(length);
	}
}
