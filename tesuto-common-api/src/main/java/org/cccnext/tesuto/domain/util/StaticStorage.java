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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;


import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.AmazonServiceException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class StaticStorage {
    // These are hard wired storage locations
    public static final String ITEM = "item";
    public static final String TEST = "test";
    public static final String TESTLET = "testlet";
    public static final String ASSESSMENT = "assessment";

    @Value("${static.storage.bucket}")
    String bucketName;

    /*
     * Do not change this unless you know that data won't get overwritten or
     * lost because of the partition size. This is only needed to keep the
     * number of items in a given virtual bucket directly small enough to manage
     * in the case of large growth.
     *
     * Basic sample reference code can be found here:
     * http://docs.aws.amazon.com/AmazonS3/latest/dev/UploadObjSingleOpJava.html
     *
     * The API can be found here:
     * http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?overview-
     * summary.html
     */
    public static final int PARTITION_SIZE = 10000;

    public abstract String store(String key, File file)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException;

    public abstract String store(String key, File file, boolean deleteFile)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException;

    public abstract void delete(String key) throws AmazonServiceException;

    public abstract void renameObject(String sourceKey, String destinationKey) throws AmazonServiceException;

    public abstract InputStream getFile(String key) throws AmazonServiceException, FileNotFoundException;

    public int getPartitionNumber(int id) {
        return (id / PARTITION_SIZE) + 1;
    }

    /**
     * This is not used right now but I thought we would need the filename
     * extension. It turns out we don't. Note the dot (.) is not included in the
     * return value: "whatever.txt" returns "txt".
     * 
     * @param filename
     * @return
     */
    public String getFilenameExtension(String filename) {
        Integer index = filename.lastIndexOf(".");
        String extension = "";
        if (index != null) {
            extension = filename.substring(++index);
        }
        return extension;
    }

    /**
     * This is part of the key, basically the directory not including the
     * filename.
     *
     * These are storage locations on S3. Think carefully and move old
     * information accordingly if you decide to change these APIs.
     *
     * @return
     */
    public String mediaStructure(String namespace, String type, String id, String version) {
        /*
         * namespace/item/itemid/version/filename
         * namespace/testlet/testletid/version/filename
         * namespace/asmt/asmtid/version/filename
         */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(namespace);
        stringBuilder.append('/').append(type);
        // TODO: Perhaps we need a partition number inserted right here.
        stringBuilder.append('/').append(id);
        stringBuilder.append('/').append(version);
        stringBuilder.append('/');
        return stringBuilder.toString();
    }

    /**
     * Verify the S3 object path.
     *
     * @param key
     *            expected namespace/type/identifier/version/uniquename unique
     *            name can be nested in directories (ie resource/image.jpeg)
     */
    protected boolean verifyPath(String key) {
        String path[] = key.split("/");

        StringBuilder mesg = new StringBuilder();

        if (path.length < 5) {
            mesg.append("Invalid key, needs at least 5 parameters you have ").append(path.length).append("\n");
            log.debug("{}", mesg);
            return false; // Only check the other parameters if this is check is
                          // valid
        }

        String namespace = path[0];
        if (StringUtils.isBlank(namespace)) {
            mesg.append("Invalid namespace because it is empty.").append("\n");
        }
        if (NAMESPACE.getType(namespace) == null) {
            log.warn("Names space not in NAMESPCE enum:\n{}", namespace);
        }

        String type = path[1];
        if (TYPE.getType(type) == null) {
            mesg.append("Invalid type ").append(type).append("\n");
        }

        // TODO verify identifier -> currently do not want to limit this

        String version = path[3];
        if (!NumberUtils.isDigits(version) || (Integer.parseInt(version) < 0)) {
            mesg.append("Invalid version ").append(version).append("\n");
        }

        // TODO verify uniqueName -> currently don't want to limit this

        if (mesg.length() > 0) {
            log.error("Invalid key:\n{}", mesg);
            return false;
        }

        return true;
    }

    // TODO right now this is faster, then a query to the database on every
    // image load.
    private enum NAMESPACE {
        LSI("LSI"), DEVELOPER("DEVELOPER");

        private String key;

        NAMESPACE(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }

        public static NAMESPACE getType(String key) {
            for (NAMESPACE namespace : NAMESPACE.values()) {
                if (namespace.toString().equals(key)) {
                    return namespace;
                }
            }
            return null;
        }
    }

    private enum TYPE {
        ITEM("item"), ASSESSMENT("assessment");

        private String key;

        TYPE(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }

        public static TYPE getType(String key) {
            for (TYPE type : TYPE.values()) {
                if (type.toString().equals(key)) {
                    return type;
                }
            }
            return null;
        }
    }
}
