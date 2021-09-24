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
package org.cccnext.tesuto.importer.service.upload;

import java.io.InputStream;
import java.net.URI;

/**
 * @author Bill Smith (wsmith@unicon.net)
 * Borrowed from uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator
 */
public interface ResourceLocator {

    /**
     * Implementations should return an {@link InputStream} corresponding to the
     * XML resource having the given System ID, or null if they
     * can't locate the required resource or won't handle the given URI.
     *
     * @param systemId
     */
    InputStream findResource(final String systemId);

}
