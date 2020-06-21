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
package org.cccnext.tesuto.content.dto;

import org.cccnext.tesuto.domain.dto.Dto;

/**
 * Marker Interface for Data Transfer Objects used for data storage. These DTOs
 * are datbase objects that are persisted. We may transform these to a regular
 * DTO (or what *we* refer to as a regular DTO) later before passing up to the
 * user interface for example. The reason this exists
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AbstractAssessmentDto extends Dto {
}
