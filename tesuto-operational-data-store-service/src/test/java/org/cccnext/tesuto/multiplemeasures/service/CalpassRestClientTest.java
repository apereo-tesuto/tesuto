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
package  org.cccnext.tesuto.multiplemeasures.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

//Tests are currently disabled because they require passwords that shouldn't be checked in with other source
public class CalpassRestClientTest {

  CalpassRestClient client;

  @Before
  public void setup() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<HashMap<String, String>> mapTypeRef = new TypeReference<HashMap<String, String>>() {};
    client = new CalpassRestClient();
    //The parameter file is not included in the source code because it requires passwords that should be protected
    try (InputStream is = getClass().getResourceAsStream("/calpass-test.json")) {
      client.setProperties(mapper.readValue(is, mapTypeRef));
    }
  }

	@Test
  @Ignore
	public void testClientHappyPath() throws ClientProtocolException, URISyntaxException, IOException, UnirestException {
    //String intersegmentKey = "000023BCACC740C4F1E015768A753DED202897C961092E2A99EF514C86AF9BC1439E91ADDCB94DA9ADF9DC7EC2949FE9A4F917CC3A433125594C07B8D3BFCC30";
    String ssid =  "1001006764";
		Map<String,Boolean> calpassResponse = client.getCalpasData(ssid, 0);
		System.out.println(calpassResponse);
		//assertTrue(calpassResponse.size() == 35);
	}

	@Test
  @Ignore
	public void testUnitRestToken() throws UnirestException {
    String response = client.executeUniRestTokenRequest();
    assertTrue(StringUtils.isNotBlank(response));
  }

}
