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
package org.cccnext.tesuto.springboot.web.security;

import com.nimbusds.jwt.SignedJWT;


import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Total hack to keep the Redis from puking on the Open ID token when that is used to authenticate a service.
 * The session goes into Redis automatically, even though this is a stateless operation, and that Oauth 2 token
 * is not serializable and causes an exception.  This simply eliminates it.
 *
 * I lost a small piece of my soul writing this.
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JwtRedisSerializer implements RedisSerializer<Object> {

	private Converter<Object, byte[]> serializer = new SerializingConverter();
	private Converter<byte[], Object> deserializer = new DeserializingConverter();

	static final byte[] EMPTY_ARRAY = new byte[0];

	public Object deserialize(byte[] bytes) {
		if (isEmpty(bytes)) {
			return null;
		}

		try {
			return SignedJWT.parse(String.valueOf(bytes));
		} catch (Exception ex) {
			log.debug("Cannot deserialize SignedJWT Object, which is okay for non SignedJWT objects: {}", ex);
		}
		return deserializer.convert(bytes);
	}

	public byte[] serialize(Object object) {
		if (object == null) {
			return EMPTY_ARRAY;
		}
		try {
			return serializer.convert(object);
		} catch (SerializationFailedException ex) {
			log.debug("Cannot deserialize Object, which is okay for non SignedJWT objects: {}", ex);

			// Rather than swallow all future exceptions, this is the best hack we could come up with for not worrying
			// about JWT serialization exceptions while still preventing future error-swallowing.
			if(ex.getMessage().endsWith("com.nimbusds.jwt.SignedJWT")) {
				return EMPTY_ARRAY;
			}

			throw ex;
		}
	}

	private boolean isEmpty(byte[] data) {
		return (data == null || data.length == 0);
	}
}
