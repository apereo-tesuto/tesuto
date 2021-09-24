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
package org.cccnext.tesuto.placement.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;




import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Converter
public class JsonBDisciplineConverter implements AttributeConverter<Discipline, String> {
    
    private final static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(Discipline discipline) {
        try {
            return objectMapper.writeValueAsString(discipline);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Discipline convertToEntityAttribute(String discipline) {
        try {
            return objectMapper.readValue(discipline, Discipline.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
