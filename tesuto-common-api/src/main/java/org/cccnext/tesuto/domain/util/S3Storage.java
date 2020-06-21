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
package org.cccnext.tesuto.domain.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class S3Storage extends StaticStorage {

    private AmazonS3Client s3client;

    @Value("${static.storage.s3.key}")
    private String key;

    @Value("${static.storage.s3.secret}")
    private String secret;

    @Value("${static.storage.s3.use.roles}")
    private Boolean useRoles;

    public S3Storage() throws AmazonServiceException {
        if (useRoles) {
            s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
        } else {
            this.s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
        }
    }

    /**
     * Initializes the s3 connection so that we can talk to the storage service.
     * Authenticates from the credentials stored in
     * <code>ccc-assess-config.properties</code>. The bucket access control list
     * (ACL) is set to give all users the read permission.
     * 
     * @throws AmazonServiceException
     *             If there was an initialization failure with S3
     */
    public S3Storage(String key, String secret, Boolean useRoles) throws AmazonServiceException {
        this.key = key;
        this.secret = secret;
        this.useRoles = useRoles;
        if (useRoles) {
            s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
        } else {
            this.s3client = new AmazonS3Client(new BasicAWSCredentials(key, secret));
        }
    }

    /**
     * Stores a file with the specified key. The file will be deleted after it
     * is stored on s3
     *
     * @param key
     *            The full name of the S3 key
     * @param file
     *            The file needs to exist on our file system. The file will be
     *            deleted.
     * @throws AmazonServiceException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String store(String key, File file) throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        return store(key, file, true);
    }

    /**
     * Stores a file with the specified key. The file will be deleted after it
     * is stored on s3
     *
     * @param key
     *            The full name of the S3 key
     * @param file
     *            The file needs to exist on our file system.
     * @param deleteFile
     *            if true, the file will be deleted otherwise it will be left on
     *            the file system.
     * @throws AmazonServiceException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String store(String key, File file, boolean deleteFile)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        log.debug("bucketName: {}", bucketName);
        log.debug("key (full name of the S3 key: {}", key);
        log.debug("file.getAbsolutePath(): {}", file.getAbsolutePath());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        log.debug("putObjectRequest.getBucketName(): {}}", putObjectRequest.getBucketName());
        PutObjectResult result = s3client.putObject(putObjectRequest);
        log.debug("result.getContentMd5(): {}", result.getContentMd5());
        log.debug("result.getETag(): {}", result.getETag());
        log.debug("result.getMetadata().getContentDisposition(): {}", result.getMetadata().getContentDisposition());
        if (deleteFile) {
            file.delete();
        }

        log.debug("key after insert:media?path={}", key);
        return "media?path=" + key;
    }

    /**
     * Delete the object referenced by the key
     * 
     * @param key
     *            object reference
     */
    public void delete(String key) throws AmazonServiceException {
        s3client.deleteObject(bucketName, key);
    }

    /**
     * This allows us to get a raw input stream from S3 file content. We use
     * this in the download controller because the user cannot download from S3
     * directly for security reasons.
     *
     * @param key
     * @return
     * @throws AmazonServiceException
     */
    public InputStream getFile(String key) throws AmazonServiceException, FileNotFoundException {
        if (!verifyPath(key)) {
            throw new FileNotFoundException(String.format("Key not found: %s", key));
        }
        S3Object s3Object = null;
        InputStream inputStream = null;
        try {
            s3Object = s3client.getObject(bucketName, key);
            inputStream = s3Object.getObjectContent();
        } catch (AmazonS3Exception as3e) {
            // If the file doesn't exist I'm saying it's not an error. Not 100%
            // sure this is a good idea.
            if (as3e.getStatusCode() != 404) {
                throw as3e;
            }
            log.error(String.format("Did not find any object at location %s", key), as3e);
        }
        return inputStream;
    }

    /**
     * Rename an S3 File.
     *
     * @param sourceKey
     * @param destinationKey
     * @throws AmazonServiceException
     */
    public void renameObject(String sourceKey, String destinationKey) throws AmazonServiceException {
        s3client.copyObject(bucketName, sourceKey, bucketName, destinationKey);
        s3client.deleteObject(bucketName, sourceKey);
    }
}
