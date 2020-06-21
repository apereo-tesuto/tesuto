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



import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class S3ReportStorage implements ReportStorage {


    private AmazonS3 s3client;

    @Value("${report.s3.bucket.name}")
    private String reportS3BucketName;

    public S3ReportStorage() {
        try {
            s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
        } catch (Exception ex) {
            log.error("ERROR_REPORT_SERVICE_FAILED_TO_LOAD!!!!!", ex);
        }
    }

    @Override
    public void storeFile(String reportPath, File file) {
        try {
            log.debug("bucketName: {}", reportS3BucketName);
            log.debug("key (full name of the S3 key: {}", reportPath);
            log.debug("file.getAbsolutePath(): {}", file.getAbsolutePath());
            String pathToFile = reportPath + "/" + file.getName();
            log.debug("pathToFileInAWS(): {}", pathToFile);
            PutObjectRequest putObjectRequest = new PutObjectRequest(reportS3BucketName, pathToFile, file);

            log.debug("putObjectRequest.getBucketName(): {}}", putObjectRequest.getBucketName());
            PutObjectResult result = s3client.putObject(putObjectRequest);
            log.debug("result.getContentMd5(): {}", result.getContentMd5());
            log.debug("result.getETag(): {}", result.getETag());
            log.debug("result.getMetadata().getContentDisposition(): {}",
                    result.getMetadata().getContentDisposition());
            file.delete();
        } catch (Exception ex) {
            log.error("Unable to store file to path {} in bucket {}", reportPath, reportS3BucketName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(reportS3BucketName, reportPath, file);
            throw new ReportIOException("Unable to store file to path", ex);
        }
    }

}
