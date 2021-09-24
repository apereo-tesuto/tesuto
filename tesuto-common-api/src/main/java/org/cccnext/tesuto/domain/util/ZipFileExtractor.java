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

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ZipFileExtractor {

    /**
     * Extract a zip file to the file system and return a list of URLs that are
     * associated with the extracted contents.
     *
     * @param importSandboxDirectory
     *            extraction directory
     * @param inputStream
     *            input stream in zip file format
     * @return A list of file URLs.
     */
    public List<URI> extract(final File importSandboxDirectory, InputStream inputStream)
            throws IOException, EOFException, ZipException {
        /* Extract ZIP contents */
        ZipEntry zipEntry = null;
        ZipInputStream zipInputStream = null;
        List<URI> uploadFiles = new LinkedList<URI>();
        zipInputStream = new ZipInputStream(inputStream);
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String zipEntrySpacesCleansed = escapeSpaces(zipEntry.getName());
            final File destFile = new File(importSandboxDirectory, zipEntrySpacesCleansed);
            if (zipEntry.isDirectory()) {
                destFile.mkdir();
            } else {
                final FileOutputStream destOutputStream = new FileOutputStream(destFile);
                try {
                    IOUtils.copy(zipInputStream, destOutputStream);
                    uploadFiles.add(destFile.toURI());
                } finally {
                    destOutputStream.close();
                }
                zipInputStream.closeEntry();
            }
        }
        /*
         * Note: I'm throwing IOException all the way out. It means something
         * bad is configured on the server like permissions, disk space issues,
         * etc. and we want to know about it.
         */

        return uploadFiles;
    }

    /**
     * This method can be optimized a bit more. This was a quick and dirty
     * implementation. -scott smith
     *
     * @param filename
     * @return
     */
    public String escapeSpaces(String filename) {
        return filename.replaceAll(" ", "\\\\ ");
    }
}
