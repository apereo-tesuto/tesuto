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

import java.util.Date;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface IntersegmentKeyService {
    /**
     * Generate a key, given the inputs. In order to match the key on the CalPass side, we need to encode the input
     * string to byes in the UTF_16LE character set before hashing it with SHA_512. Note that only the first 3
     * characters of the first and last names are used in this input string.
     *<br><br>
     * For reference, here is the Transact-SQL hashing scheme we are attempting to match:
     *<br>
     *<pre>
     * SELECT
     *   convert(varchar(1000),
     *     HASHBYTES(
     *       'SHA2_512', -- hash algorithm
     *       upper(convert(nchar(3), 'Daniel')) + -- first name
     *       upper(convert(nchar(3), 'Lamoree')) + -- last name
     *       upper(convert(nchar(1), 'M')) + -- gender
     *       convert(nchar(8), '19440606') -- birth date
     *     )
     *   , 2);
     * </pre>
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param gender a single character, in string format, that represents the user's gender
     * @param birthDate a string representation of the the user's birthdate in yyyyMMdd format
     * @return a SHA_512 intersegment key representation of the input
     */
    String generateKey(String firstName, String lastName, String gender, String birthDate);

    /**
     * @see IntersegmentKeyService#generateKey(String, String, String, String)
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param gender a char representation of the user's gender
     * @param birthDate a string representation of the the user's birthdate in yyyyMMdd format
     * @return
     */
    String generateKey(String firstName, String lastName, char gender, String birthDate);

    /**
     * @see IntersegmentKeyService#generateKey(String, String, String, String)
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param gender a single character, in string format, that represents the user's gender
     * @param birthDate a standard java.util.Date representation of the user's birthdate
     * @return
     */
    String generateKey(String firstName, String lastName, String gender, Date birthDate);

    /**
     * @see IntersegmentKeyService#generateKey(String, String, String, String)
     * 
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param gender a char representation of the user's gender
     * @param birthDate a standard java.util.Date representation of the user's birthdate
     * @return
     */
    String generateKey(String firstName, String lastName, char gender, Date birthDate);
}
