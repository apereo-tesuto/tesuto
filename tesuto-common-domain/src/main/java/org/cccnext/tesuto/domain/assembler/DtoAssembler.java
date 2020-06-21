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
package org.cccnext.tesuto.domain.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assemblers are simply adapters to help with translating Domain Objects to
 * Database Objects and back. These adapters simplify the translation but do
 * nothing else. This idea is modeled after Martin Fowler's Assembler Adaptors.
 * While simple, these adapters mitigate a whole host of issues when Entity
 * Beans are attempted to be used directly as domain objects. This is an
 * effective technique to properly and completely abstract the database objects
 * away from the rest of the system.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface DtoAssembler<Dto, Entity> {

    public Dto assembleDto(Entity entity);

    public Entity disassembleDto(Dto dto);

    public default Set<Dto> assembleDto(final Set<Entity> entities) {
        if (entities == null)
            return null;
        Set<Dto> dtos = entities.stream().map((Entity entity) -> {
            return assembleDto(entity);
        }).collect(Collectors.toSet());
        return dtos;
    }

    public default List<Dto> assembleDto(final List<Entity> entities) {
        if (entities == null)
            return null;
        List<Dto> dtos = entities.stream().map((Entity entity) -> {
            return assembleDto(entity);
        }).collect(Collectors.toList());
        return dtos;
    }

    public default List<Dto> assembleDto(final Iterable<Entity> entities) {
        if (entities == null)
            return null;
        List<Dto> dtos = new ArrayList<Dto>();
        for (Entity entity : entities) {
            dtos.add(assembleDto(entity));
        }
        return dtos;
    }

    public default Set<Entity> disassembleDto(final Set<Dto> dtos) {
        if (dtos == null)
            return null;
        Set<Entity> entities = dtos.stream().map((Dto dto) -> {
            return disassembleDto(dto);
        }).collect(Collectors.toSet());
        return entities;
    }

    public default List<Entity> disassembleDto(final List<Dto> dtos) {
        if (dtos == null)
            return null;
        List<Entity> entities = dtos.stream().map((Dto dto) -> {
            return disassembleDto(dto);
        }).collect(Collectors.toList());

        return entities;
    }

    public default List<Entity> disassembleDto(final Iterable<Dto> dtos) {
        if (dtos == null)
            return null;
        List<Entity> entities = new ArrayList<Entity>();
        for (Dto dto : dtos) {
            entities.add(disassembleDto(dto));
        }
        return entities;
    }

}
