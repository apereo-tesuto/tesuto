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


public abstract class AbstractMapper<To,From> implements IMapper<To,From> {

	protected abstract To doMapTo(From entity);
    protected abstract From doMapFrom(To dto);

    @Override
    public To mapTo(From entity) {
        if (entity == null) {
            return null;
        } else {
            return doMapTo(entity);
        }
    }

    @Override
    public From mapFrom(To dto) {
        if (dto == null) {
            return null;
        } else {
            return doMapFrom(dto);
        }
    }
}
