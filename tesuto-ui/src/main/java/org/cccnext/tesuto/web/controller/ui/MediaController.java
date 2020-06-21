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
package org.cccnext.tesuto.web.controller.ui;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.cccnext.tesuto.domain.util.StaticStorage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Controller
@RequestMapping(value = "/media")
public class MediaController {

    @Qualifier("staticStorage")
    @Autowired
    StaticStorage staticStorage;

    @PreAuthorize("permitAll()")
    @RequestMapping(method = RequestMethod.GET)
    public void getMedia(HttpServletResponse response, @RequestParam(value = "path", required = true) String path,
            HttpSession session) throws FileNotFoundException {

        //A candidate may only have access to the s3 bucket when they have an activated assessment.
        if (!TesutoUtil.hasAssessmentSessionPermission(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        InputStream inputStream = staticStorage.getFile(path);
        // only process it if file is found
        if (inputStream != null) {
            try {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                response.addIntHeader("Content-Length", bytes.length);
                String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
                if (contentType == null) {
                    contentType = URLConnection.guessContentTypeFromName(path);
                }
                if (contentType != null) {
                    response.addHeader("Content-Type", contentType);
                }
                response.getOutputStream().write(bytes);
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
