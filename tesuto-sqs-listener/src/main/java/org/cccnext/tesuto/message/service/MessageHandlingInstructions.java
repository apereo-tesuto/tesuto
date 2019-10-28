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
package org.cccnext.tesuto.message.service;


public class MessageHandlingInstructions {

    private boolean delete = false;
    private Integer maxAge;
    private boolean storeMessage = false;
    private int minRetries = 0;
    private int retryDelay = 120; //in seconds

    public MessageHandlingInstructions() {}

    public MessageHandlingInstructions(boolean delete) {
        this.delete = delete;
    }

    public static MessageHandlingInstructions success() {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        instructions.setDelete(true);
        return instructions;
    }

    public static MessageHandlingInstructions delay(int retryDelay) {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        instructions.setRetryDelay(retryDelay);
        return instructions;
    }

    public static MessageHandlingInstructions delay() {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        instructions.setRetryDelay(120);
        return instructions;
    }

    public static MessageHandlingInstructions failure() {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        instructions.setDelete(false);
        return instructions;
    }

    public static MessageHandlingInstructions store() {
        MessageHandlingInstructions instructions = new MessageHandlingInstructions();
        instructions.setDelete(true);
        instructions.setStoreMessage(true);
        return instructions;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinRetries() {
        return minRetries;
    }

    public void setMinRetries(int minRetries) {
        this.minRetries = minRetries;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public boolean isStoreMessage() {
        return storeMessage;
    }

    public void setStoreMessage(boolean storeMessage) {
        this.storeMessage = storeMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageHandlingInstructions)) return false;

        MessageHandlingInstructions that = (MessageHandlingInstructions) o;

        if (isDelete() != that.isDelete()) return false;
        if (getMinRetries() != that.getMinRetries()) return false;
        if (getRetryDelay() != that.getRetryDelay()) return false;
        if (storeMessage != that.storeMessage) return false;

        return getMaxAge() != null ? getMaxAge().equals(that.getMaxAge()) : that.getMaxAge() == null;

    }

    @Override
    public int hashCode() {
        int result = (isDelete() ? 1 : 0);
        result = 31 * result + (getMaxAge() != null ? getMaxAge().hashCode() : 0);
        result = 31 * result + getMinRetries();
        result = 31 * result + getRetryDelay();
        result = 31 * result + (storeMessage ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        return "MessageHandlingInstructions{" +
                "delete=" + delete +
                ", storeMessage=" + storeMessage +
                ", maxAge=" + maxAge +
                ", minRetries=" + minRetries +
                ", retryDelay=" + retryDelay +
                '}';
    }
}
