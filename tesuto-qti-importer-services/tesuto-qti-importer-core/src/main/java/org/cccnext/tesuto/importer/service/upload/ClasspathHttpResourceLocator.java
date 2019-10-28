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

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class ClasspathHttpResourceLocator implements ResourceLocator {

    private final String basePath = "cccnext/tesuto/xml-catalog";

    @Override
    public InputStream findResource(final String systemId) {
        URI uri = URI.create(systemId);
        final String scheme = uri.getScheme();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            final String relativeSystemId = uri.getSchemeSpecificPart().substring(2); // Get bit after http:// or https://
            final String resultingPath = basePath != null ? basePath + "/" + relativeSystemId : relativeSystemId;
            return getClass().getClassLoader().getResourceAsStream(resultingPath);
        }
        return null;
    }
}
