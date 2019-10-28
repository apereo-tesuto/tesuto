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

import java.security.SecureRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
@Service
public class BCryptPasswordEncoderWrapper implements PasswordEncoder {

    private BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderWrapper() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public BCryptPasswordEncoderWrapper(int strength) {
        this.passwordEncoder = new BCryptPasswordEncoder(strength);
    }

    public BCryptPasswordEncoderWrapper(int strength, SecureRandom random) {
        this.passwordEncoder = new BCryptPasswordEncoder(strength, random);
    }

	@Override
	public String encode(CharSequence rawPassword) {
		return this.passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
	     return this.passwordEncoder.matches(rawPassword, encodedPassword);
	}

}
