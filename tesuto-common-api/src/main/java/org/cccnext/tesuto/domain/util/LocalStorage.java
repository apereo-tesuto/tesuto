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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.amazonaws.AmazonServiceException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class LocalStorage extends StaticStorage {

    @Value("${static.storage.local.store.directory}")
    String localDirectory;

    public LocalStorage() {

    }

    public LocalStorage(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    @Override
    public String store(String key, File file) throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        store(key, file, true);
        return key;
    }

    @Override
    public String store(String key, File file, boolean deleteFile)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        String directory = key;
        if (key.endsWith(file.getName())) {
            directory = key.substring(0, key.length() - file.getName().length());
        }
        File store = new File(getBucket(directory), file.getName());
        FileUtils.copyFile(file, store);

        if (deleteFile) {
            file.delete();
        }
        return "media?path=" + key;
    }

    @Override
    public void delete(String key) throws AmazonServiceException {
        getBucket(key).delete();
    }

    @Override
    public void renameObject(String sourceKey, String destinationKey) throws AmazonServiceException {
        File source = getFileFromBucket(sourceKey);
        File destination = getFileFromBucket(destinationKey);
        source.renameTo(destination);
    }

    @Override
    public InputStream getFile(String uri) throws AmazonServiceException, FileNotFoundException {
        if (!verifyPath(uri)) {
            throw new FileNotFoundException(String.format("Key not found: %s", uri));
        }
        File file = getFileFromBucket(uri);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return fileInputStream;
        } catch (Exception exception) {
            log.error("Unable to find file {}", uri, exception);
        }
        return null;
    }

    private File getBucket(String key) {
        StringBuilder location = new StringBuilder(localDirectory).append('/').append(bucketName);
        location.append('/').append(key);
        File directoryBucket = new File(location.toString());
        if (!directoryBucket.exists()) {
            directoryBucket.mkdirs();
        }
        return directoryBucket;
    }

    private File getFileFromBucket(String key) {
        StringBuilder location = new StringBuilder(localDirectory).append('/').append(bucketName);
        location.append('/').append(key);
        File directoryBucket = new File(location.toString());
        return directoryBucket;
    }
}
