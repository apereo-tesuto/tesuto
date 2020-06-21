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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class NetworkHttpResourceLocator implements ResourceLocator {

    @Override
    public InputStream findResource(String systemId) {
        InputStream inputStream = null;
        try {
            URI uri = URI.create(systemId);
            inputStream = uri.toURL().openStream();
        } catch (MalformedURLException e) {
            log.error("Could not generate a URL for systemId [" + systemId + "]", e);
        } catch (IOException e) {
            log.error("IO exception occurred while attempting to get input stream for systemId [" + systemId + "]", e);
        }
        return inputStream;
    }
}
