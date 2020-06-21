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
package org.cccnext.tesuto.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;


import org.springframework.stereotype.Service;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.util.EC2MetadataUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class VersionService {


    @PostConstruct
    public void init() throws IOException {
        Manifest manifest = getManifest();
        if (manifest != null) {
            String buildNumber = getBuildNumber(manifest);
            if(StringUtils.isBlank(buildNumber)) {
                return;
            }
            String instanceId = EC2MetadataUtils.getInstanceId();
            AmazonCloudWatch cw =
                    AmazonCloudWatchClientBuilder.standard().build();
            Dimension dimension = new Dimension()
                    .withName("Instance")
                    .withValue(instanceId);
            MetricDatum metricDatum = new MetricDatum()
                    .withMetricName("buildNumber")
                    .withValue(Double.valueOf(buildNumber))
                    .withDimensions(dimension);
            PutMetricDataRequest metricRequest = new PutMetricDataRequest()
                    .withNamespace("Assess")
                    .withMetricData(metricDatum);
            PutMetricDataResult result = cw.putMetricData(metricRequest);
            log.info("Put buildNumber (" + buildNumber + ") metric for current instance ( " + instanceId + ") to AWS with result:\n" + result.toString());
        }
    }

    private String getBuildNumber(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        return mainAttributes.getValue("Implementation-Build");
    }

    public Map<String, String> getVersion() {
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
        /*
        Package aPackage = this.getClass().getPackage();
        versionMap.put("Implementation-Title", aPackage.getImplementationTitle());
        versionMap.put("Implementation-Vendor", aPackage.getImplementationVendor());
        versionMap.put("Implementation-Version", aPackage.getImplementationVersion());
        */

        // Not needed right now but could be added in the future.
        /*
        versionMap.put("name", aPackage.getName());
        versionMap.put("specificationTitle", aPackage.getSpecificationTitle());
        versionMap.put("specificationVendor", aPackage.getSpecificationVendor());
        versionMap.put("specificationVersion", aPackage.getSpecificationVersion());
        */
        return versionMap;
    }

    private Manifest getManifest() {
        //String appManifestFileName = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString() + JarFile.MANIFEST_NAME;
        //String appManifestFileName = "jar:file:/home/scott/java-workspace/tesuto/tesuto-web/target/tesuto-ui.jar!/META-INF/MANIFEST.MF";
        String appManifestFileName = "tesuto-ui.jar!/META-INF/MANIFEST.MF";
        /*
        log.info(this.getClass().toString()); // org.cccnext.tesuto.web.controller.ui.MainController
        log.info(this.getClass().getProtectionDomain().toString()); // jar:file:/home/scott/java-workspace/tesuto/tesuto-web/target/tesuto-ui.jar!/BOOT-INF/classes!/ <no signer certificates> ....
        log.info(this.getClass().getProtectionDomain().getCodeSource().toString()); // jar:file:/home/scott/java-workspace/tesuto/tesuto-web/target/tesuto-ui.jar!/BOOT-INF/classes!/ <no signer certificates>
        log.info(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString()); // jar:file:/home/scott/java-workspace/tesuto/tesuto-web/target/tesuto-ui.jar!/BOOT-INF/classes!/
        log.info(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString() + JarFile.MANIFEST_NAME); // jar:file:/home/scott/java-workspace/tesuto/tesuto-web/target/tesuto-ui.jar!/BOOT-INF/classes!/META-INF/MANIFEST.MF
        */

        Enumeration resEnum;
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
