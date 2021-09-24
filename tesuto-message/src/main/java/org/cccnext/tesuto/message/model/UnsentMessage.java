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
package org.cccnext.tesuto.message.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema="public", name = "unsent_message")

public class UnsentMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id; // primary key

  @Column(name="queue_url")
  private String queueUrl;

  @Column(name="last_sent_on")
  private Date lastSentOn;

  @Column(name="message")
  private String message;

  public String getId() {
        return id;
    }

  public void setId(String id) {
        this.id = id;
    }

  public Date getLastSentOn() {
        return lastSentOn;
    }

  public void setLastSentOn(Date lastSentOn) {
        this.lastSentOn = lastSentOn;
    }

  public String getMessage() {
        return message;
    }

  public void setMessage(String message) {
        this.message = message;
    }

  public String getQueueUrl() {
        return queueUrl;
    }

  public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

}
