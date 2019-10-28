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

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.AmazonServiceException;

public class EncodedStorage extends StaticStorage {

    @Override
    public String store(String key, File file) throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        //Auto-generated method stub
        String encodedImage = encodeFileToBase64Binary(file);
        String[] imagePartNames = file.getName().split("\\.");
        String imageType = imagePartNames[imagePartNames.length - 1];
        String uri = "data:image/" + imageType + ";base64," + encodedImage;
        return uri;
    }

    @Override
    public String store(String key, File file, boolean deleteFile)
            throws AmazonServiceException, NoSuchAlgorithmException, IOException {
        return store(key, file);
    }

    @Override
    public void delete(String key) throws AmazonServiceException {

    }

    @Override
    public void renameObject(String sourceKey, String destinationKey) throws AmazonServiceException {
        //Auto-generated method stub

    }

    @Override
    public InputStream getFile(String key) throws AmazonServiceException, FileNotFoundException {
        //Auto-generated method stub
        return null;
    }

    private String encodeFileToBase64Binary(File file) throws IOException {

        byte[] bytes = loadFile(file);
        String encoded = Base64.encodeBase64String(bytes);
        String encodedString = new String(encoded);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        is.close();

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        return bytes;
    }

}
