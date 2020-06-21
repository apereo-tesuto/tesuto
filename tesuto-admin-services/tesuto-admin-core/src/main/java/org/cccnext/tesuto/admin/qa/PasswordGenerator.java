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
package org.cccnext.tesuto.admin.qa;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PasswordGenerator {

    private final List<PasswordCharacterSet> pwSets;
    private final char[] allCharacters;
    private final int minLength;
    private final int maxLength;
    private final int presetCharacterCount;

    public PasswordGenerator() {
        this.minLength = 16;
        this.maxLength = 16;
        Set<PasswordCharacterSet> origPwSets = new HashSet<PasswordCharacterSet>(
                EnumSet.allOf(SummerCharacterSets.class));

        int pwCharacters = 0;
        int preallocatedCharacters = 0;
        List<PasswordCharacterSet> pwSets = new ArrayList<PasswordCharacterSet>(origPwSets.size());
        for (PasswordCharacterSet origpwSet : origPwSets) {
            PasswordCharacterSet newPwSet = new PwSet(origpwSet);
            pwSets.add(newPwSet);
            pwCharacters += newPwSet.getCharacters().length;
            preallocatedCharacters += newPwSet.getMinCharacters();
        }

        this.presetCharacterCount = preallocatedCharacters;
        this.pwSets = Collections.unmodifiableList(pwSets);

        if (minLength < presetCharacterCount) {
            throw new IllegalArgumentException("Combined minimum lengths " + presetCharacterCount
                    + " are greater than the minLength of " + minLength);
        }

        // Copy all characters into single array so we can evenly access all
        // members when accessing this array
        char[] allChars = new char[pwCharacters];
        int currentIndex = 0;
        for (PasswordCharacterSet pwSet : pwSets) {
            char[] chars = pwSet.getCharacters();
            System.arraycopy(chars, 0, allChars, currentIndex, chars.length);
            currentIndex += chars.length;
        }
        this.allCharacters = allChars;
    }

    public PasswordGenerator(Collection<PasswordCharacterSet> origPwSets, int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;

        int pwCharacters = 0;
        int preallocatedCharacters = 0;
        List<PasswordCharacterSet> pwSets = new ArrayList<PasswordCharacterSet>(origPwSets.size());
        for (PasswordCharacterSet origpwSet : origPwSets) {
            PasswordCharacterSet newPwSet = new PwSet(origpwSet);
            pwSets.add(newPwSet);
            pwCharacters += newPwSet.getCharacters().length;
            preallocatedCharacters += newPwSet.getMinCharacters();
        }

        this.presetCharacterCount = preallocatedCharacters;
        this.pwSets = Collections.unmodifiableList(pwSets);

        if (minLength < presetCharacterCount) {
            throw new IllegalArgumentException("Combined minimum lengths " + presetCharacterCount
                    + " are greater than the minLength of " + minLength);
        }

        // Copy all characters into single array so we can evenly access all
        // members when accessing this array
        char[] allChars = new char[pwCharacters];
        int currentIndex = 0;
        for (PasswordCharacterSet pwSet : pwSets) {
            char[] chars = pwSet.getCharacters();
            System.arraycopy(chars, 0, allChars, currentIndex, chars.length);
            currentIndex += chars.length;
        }
        this.allCharacters = allChars;
    }

    public char[] generatePassword() {
        SecureRandom rand = new SecureRandom();

        // Set pw length to minLength <= pwLength <= maxLength
        int pwLength = minLength + rand.nextInt(maxLength - minLength + 1);
        int randomCharacterCount = pwLength - presetCharacterCount;

        // Place each index in an array then remove them randomly to assign
        // positions in the pw array
        List<Integer> remainingIndexes = new ArrayList<Integer>(pwLength);
        for (int i = 0; i < pwLength; ++i) {
            remainingIndexes.add(i);
        }

        // Fill pw array
        char[] pw = new char[pwLength];
        for (PasswordCharacterSet pwSet : pwSets) {
            addRandomCharacters(pw, pwSet.getCharacters(), pwSet.getMinCharacters(), remainingIndexes, rand);
        }
        addRandomCharacters(pw, allCharacters, randomCharacterCount, remainingIndexes, rand);
        return pw;
    }

    private static void addRandomCharacters(char[] pw, char[] characterSet, int numCharacters,
            List<Integer> remainingIndexes, Random rand) {
        for (int i = 0; i < numCharacters; ++i) {
            // Get and remove random index from the remaining indexes
            int pwIndex = remainingIndexes.remove(rand.nextInt(remainingIndexes.size()));

            // Set random character from character index to pwIndex
            int randCharIndex = rand.nextInt(characterSet.length);
            pw[pwIndex] = characterSet[randCharIndex];
        }
    }

    public static interface PasswordCharacterSet {
        char[] getCharacters();

        int getMinCharacters();
    }

    /**
     * Defensive copy of a passed-in PasswordCharacterSet
     */
    private static final class PwSet implements PasswordCharacterSet {
        private final char[] chars;
        private final int minChars;

        public PwSet(PasswordCharacterSet pwSet) {
            this.minChars = pwSet.getMinCharacters();
            char[] pwSetChars = pwSet.getCharacters();
            // Defensive copy
            this.chars = Arrays.copyOf(pwSetChars, pwSetChars.length);
        }

        @Override
        public char[] getCharacters() {
            return chars;
        }

        @Override
        public int getMinCharacters() {
            return minChars;
        }
    }

    private static final char[] ALPHA_UPPER_CHARACTERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] ALPHA_LOWER_CHARACTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] NUMERIC_CHARACTERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final char[] SPECIAL_CHARACTERS = { '~', '`', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-',
            '_', '=', '+', '[', '{', ']', '}', '\\', '|', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?' };

    private enum SummerCharacterSets implements PasswordCharacterSet {
        ALPHA_UPPER(ALPHA_UPPER_CHARACTERS, 1), ALPHA_LOWER(ALPHA_LOWER_CHARACTERS, 1), NUMERIC(NUMERIC_CHARACTERS, 1), SPECIAL(
                SPECIAL_CHARACTERS, 1);

        private final char[] chars;
        private final int minUsage;

        private SummerCharacterSets(char[] chars, int minUsage) {
            this.chars = chars;
            this.minUsage = minUsage;
        }

        @Override
        public char[] getCharacters() {
            return chars;
        }

        @Override
        public int getMinCharacters() {
            return minUsage;
        }
    }
}
