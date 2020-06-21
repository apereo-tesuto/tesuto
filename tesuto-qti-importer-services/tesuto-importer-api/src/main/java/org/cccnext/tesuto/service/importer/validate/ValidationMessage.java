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
package org.cccnext.tesuto.service.importer.validate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Jason Brown jbrown@unicon.net on 12/15/16.
 */
public class ValidationMessage {

    public enum FileType{
        ASSESSMENT,
        ASSESSMENT_METADATA,
        ITEM,
        ITEM_METADATA,
        IMS_MANIFEST,
        NA
    }

    private static final String NA = "NA";
    private String file = NA;
    private String node = NA;
    private String message = NA;
    private FileType fileType = FileType.NA;
    private String line = NA;
    private String column = NA;

    public ValidationMessage() {
	}

    public ValidationMessage(String message) {
    	this.message = message;
	}

    public ValidationMessage(int line, int column, String message) {
    	this.setColumn(column);
    	this.setLine(line);
    	this.message = message;
	}

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = removeDirectoriesThatWillConfuseEndUser(file);
    }

    /**
     * Useful for testing.
     * @param file
     */
    public void setExpectedFile(String file){
        this.file = file;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = String.valueOf(line);
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = String.valueOf(column);
    }

    //Remove all file://tmp/tesuto-uploads/UUID/
    private String removeDirectoriesThatWillConfuseEndUser(String file){
        String[] split = file.split("/");

        if(split.length>3){
            StringBuilder sb = new StringBuilder();
            for(int i=4; i<split.length; i++){
                sb.append("/").append(split[i]);
            }
            return sb.toString();
        }else{
            return file;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
