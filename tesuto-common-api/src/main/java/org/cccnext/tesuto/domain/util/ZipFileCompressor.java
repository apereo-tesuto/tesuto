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
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


import org.springframework.stereotype.Service;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ZipFileCompressor {
        
    public File compressFiles(String zipFilePath, String zipFileName, List<File> files, String password) throws IOException {
        
        ZipParameters parameters = getInitalParameters();
        if(StringUtils.isNotBlank(password)) {
        	parameters.setEncryptFiles(true);
        	parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        	parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        	parameters.setPassword(password);
        }
        return compressFiles(zipFilePath,  zipFileName,  files,  parameters);
    }
    
    public File compressFiles(String zipFilePath, String zipFileName, List<File> files) throws IOException {
        return compressFiles(zipFilePath,  zipFileName,  files,  getInitalParameters());
    }
    
    private ZipParameters getInitalParameters() {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        
        return parameters;
    }
    
    private File compressFiles(String zipFilePath, String zipFileName, List<File> files, ZipParameters parameters) throws IOException {
        try {
            try {
                File zippedFile = new File(getZipFileDirectory(zipFilePath), zipFileName);
                ZipFile zipFile = new ZipFile(zippedFile);
                for(File file:files) {
                    if(file.isDirectory()) {
                        zipFile.addFolder(file, parameters);
                    } else {
                        zipFile.addFile(file, parameters);
                    }
                }
                return zippedFile;
            }
            catch (ZipException e)
            {
               log.error("Unable to zip and encrypt Files", e);
            }
            }   catch(Exception e) {
                log.error("Unable to zip and encrypt Files", e);
            } finally {
                for(File file:files) {
                    file.delete();
                }
            }
        return null;
    }
    
    private File getZipFileDirectory(String zipFilePath) {
        File fileDirectory = new File(zipFilePath);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        return fileDirectory;
    }

}
