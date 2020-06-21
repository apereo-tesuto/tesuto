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
package org.cccnext.tesuto.rules.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.function.Consumer;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;

public abstract class AbstractFactMapper implements FactMapper {

 
    protected void copy(String key, Map<String,Fact> facts, Map<String,Fact> newFacts, Consumer<Fact> transform) {
        Fact old = facts.get(key);
        if (old != null) {
            Fact newFact = new Fact(old);
            transform.accept(newFact);
            if (newFact.getName() != null) {
                newFacts.put(newFact.getName(), newFact);
            }
        }
    }

    protected void mathTransform(Fact courseFact, final Fact scoreFact) {
        courseFact.setName(getMathMap().get(courseFact.getValue()));
        courseFact.setValue(getGradeMap().get(scoreFact.getValue()));
    }

    //Not private -- also used in unit test
    static String getValue(String key, final Map<String,Fact> facts) {
        Fact fact = facts.get(key);
        return (fact == null ? null : fact.getValue());
    }


    protected boolean getCompleted11th(final Map<String,Fact> facts) {
        String value = getValue("completed_eleventh_grade", facts);
        if (value == null) {
            return false;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    protected Calendar getDate(String name, final Map<String,Fact> facts) {
        String value = getValue(name, facts);
        if (value == null) {
            return null;
        } else {
           try {
               Calendar cal = GregorianCalendar.getInstance();
               cal.setTimeInMillis(Long.parseLong(value));
               return cal;
           } catch (NumberFormatException e) {
               return null;
           }
       }
    }

    protected Fact createFact(String name, String value) {
        Fact fact = new Fact();
        fact.setName(name);
        fact.setValue(value);
        fact.setSourceDate(new Date());
        fact.setSource(getSource() + "-Mapping");
        fact.setSourceType(getSourceType() + "-Mapping");
        return fact;
    }

    protected void addFact(String name, String value, Map<String,Fact> facts) {
        facts.put(name,createFact(name, value));
    }

}
