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
package org.ccctc.droolseditor;

import java.util.ArrayList;
import java.util.List;

import org.mitre.oauth2.introspectingfilter.service.IntrospectionAuthorityGranter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by andrew on 10/26/16.
 */
public class RoleClaimAuthorityGranter implements IntrospectionAuthorityGranter {
    @Override
    public List<GrantedAuthority> getAuthorities(JsonObject introspectionResponse) {
        final ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        final JsonArray roles = introspectionResponse.getAsJsonArray("roles");
        if (roles != null) {
            for (JsonElement ele: roles) {
                authorities.add(new SimpleGrantedAuthority(ele.getAsString()));
            }
        }
        return authorities;
    }
}
