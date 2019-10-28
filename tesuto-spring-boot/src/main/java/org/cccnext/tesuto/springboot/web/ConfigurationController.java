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
package org.cccnext.tesuto.springboot.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.springboot.service.ConfigConfirmationService;
import org.cccnext.tesuto.springboot.service.RestClientConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/")
public class ConfigurationController {
    
    @Autowired
    private ConfigConfirmationService configConfirmationService;
    
    @Autowired
    private RestClientConfirmationService restClientConfirmation;
    @Autowired
    protected ServletContext servletContext;
    
    @PreAuthorize("hasAnyAuthority('VIEW_SERVER_CONFIG', 'API')")
    @RequestMapping(value = "configuration", method = RequestMethod.GET)
    public @ResponseBody String logConfiguration() throws IOException {
        return configConfirmationService.logProperties();
    }

    @PreAuthorize("hasAnyAuthority('VIEW_CODE_VERSION', 'API')")
    @RequestMapping(value = "version", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Map<String, String> appVersionInformation() throws IOException {
        Manifest manifest = getManifest();
        Map<String, String> versionMap = new TreeMap<>();
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            System.out.println("attributes.keySet: " + attributes.keySet());
            for (Object key : attributes.keySet()) {
                String keyString = key.toString();
                String value = attributes.getValue(keyString);
                versionMap.put(keyString, value);
            }
        } else {
            versionMap.put("No version information.", "Local Dev? MANIFEST.MF only exists when running a jar file.");
        }
        return versionMap;
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_SERVER_CONFIG', 'API')")
    @RequestMapping(value = "configuration/clients/status", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<RestClientTestResult> restClientStatus() throws IOException {
        return restClientConfirmation.validateEndpoints();
    }

    private Manifest getManifest() {
        String appManifestFileName = "tesuto-placement.jar!/META-INF/MANIFEST.MF";

        Enumeration<?> resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    if (url.toString().contains(appManifestFileName)) {
                        InputStream is = url.openStream();
                        if (is != null) {
                            Manifest manifest = new Manifest(is);
                            return manifest;
                        }
                    }
                }
                catch (Exception e) {
                }
            }
        } catch (IOException e1) {
        }
        return null;
    }

}
