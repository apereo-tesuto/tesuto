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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class IntersegmentKeyServiceImpl implements IntersegmentKeyService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public String generateKey(String firstName, String lastName, String gender, String birthDate) {
        byte[] keyBytes = (getStringOfMaxLength(firstName, 3).toUpperCase()
                + getStringOfMaxLength(lastName, 3).toUpperCase()
                + getStringOfMaxLength(gender, 1).toUpperCase()
                + getStringOfMaxLength(birthDate, 8))
                .getBytes(StandardCharsets.UTF_16LE);
        return  DigestUtils.sha512Hex(keyBytes).toUpperCase();
    }
    
    @Override
    public String generateKey(String firstName, String lastName, char gender, String birthDate) {
        return generateKey(firstName, lastName, String.valueOf(gender), birthDate);
    }

    @Override
    public String generateKey(String firstName, String lastName, String gender, Date birthDate) {
        return generateKey(firstName, lastName, gender, DATE_FORMAT.format(birthDate));
    }

    @Override
    public String generateKey(String firstName, String lastName, char gender, Date birthDate) {
        return generateKey(firstName, lastName, String.valueOf(gender), DATE_FORMAT.format(birthDate));
    }

    private String getStringOfMaxLength(String input, int maxLength) {
        if (StringUtils.isBlank(input)) {
            return "";
        } else if (input.length() <= maxLength) {
            return input;
        } else {
            return input.substring(0, maxLength);
        }
    }
}
