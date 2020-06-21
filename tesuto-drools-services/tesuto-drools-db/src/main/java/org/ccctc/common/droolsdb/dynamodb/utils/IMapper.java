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
package org.ccctc.common.droolsdb.dynamodb.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IMapper<To, From> {

	public To mapTo(From from);

    public From mapFrom(To to);
	
	public default Set<To> mapTo(final Set<From> froms) {
        if (froms == null)
            return null;
        if(froms.size() == 0){
        	return new HashSet<>();
        }
        return froms.stream().map(f -> mapTo(f) ).collect(Collectors.toSet());
    }

    public default List<To> mapTo(final List<From> entities) {
        if (entities == null)
            return null;
        
        if(entities.size() == 0){
        	return new ArrayList<>();
        }
        List<To> dtos = entities.stream().map((From from) -> {
            return mapTo(from);
        }).collect(Collectors.toList());
        return dtos;
    }

    public default List<To> mapTo(final Iterable<From> entities) {
        if (entities == null)
            return null;
       if(!entities.iterator().hasNext()){
        	return new ArrayList<>();
        }
        List<To> dtos = new ArrayList<To>();
        for (From from : entities) {
            dtos.add(mapTo(from));
        }
        
        return dtos;
    }

    public default Set<From> mapFrom(final Set<To> dtos) {
        if (dtos == null)
            return null;
        if(dtos.size() == 0){
        	return new HashSet<>();
        }
        return dtos.stream().map( dto -> mapFrom(dto)).collect(Collectors.toSet());
    }

    public default List<From> mapFrom(final List<To> dtos) {
        if (dtos == null)
            return null;
        
        if(dtos.size() == 0){
        	return new ArrayList<>();
        }
        List<From> entities = dtos.stream().map((To dto) -> {
            return mapFrom(dto);
        }).collect(Collectors.toList());

        return entities;
    }

    public default List<From> mapFrom(final Iterable<To> dtos) {
        if (dtos == null)
            return null;
        if(!dtos.iterator().hasNext()){
        	return new ArrayList<>();
        }
        List<From> entities = new ArrayList<From>();
        for (To dto : dtos) {
            entities.add(mapFrom(dto));
        }
        return entities;
    }
}
