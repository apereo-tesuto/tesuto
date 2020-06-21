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
package org.cccnext.tesuto.reports.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class LocalReportStorage implements ReportStorage {

    @Value("${static.storage.local.store.directory}")
    private String localStorefileDirectory;

    @Value("${report.s3.bucket.name}")
    private String reportS3BucketName;

    @Override
    public void storeFile(String reportPath, File file) {
        File directoryBucket = new File(localStorefileDirectory + "/" + reportS3BucketName + "/" + reportPath);
        if (!directoryBucket.exists()) {
            directoryBucket.mkdirs();
        }

        File store = new File(directoryBucket, file.getName());

        try {
            FileUtils.copyFile(file, store);
        } catch (IOException exception) {
            log.error("Unable to store file to path {} in bucket {}", reportPath, directoryBucket);
            throw new ReportIOException("Unable to store file to path", exception);
        }

        file.delete();
    }

}
