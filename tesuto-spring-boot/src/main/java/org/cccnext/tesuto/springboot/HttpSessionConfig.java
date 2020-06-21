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
package org.cccnext.tesuto.springboot;

import org.cccnext.tesuto.springboot.web.security.JwtRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(redisNamespace = "${spring.session.redis.namespace}", maxInactiveIntervalInSeconds = 2400, redisFlushMode = RedisFlushMode.IMMEDIATE)
public class HttpSessionConfig {

	@Value("${spring.session.redis.namespace}")
	private String redisNamespace;

	@Value("${REDIS_HOST_NAME}")
	private String redisHostName;

	@Value("${REDIS_PORT}")
	private Integer redisPort;

	LettuceConnectionFactory lettuceConnectionFactory() {

		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHostName);
		redisStandaloneConfiguration.setPort(redisPort);
		redisStandaloneConfiguration.setDatabase(0);
		redisStandaloneConfiguration.setPassword(RedisPassword.of("password"));

		LettuceConnectionFactory lettuceConFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);

		return lettuceConFactory;
	}

	@Bean
	RedisTemplate<String, String> redisTemplate() {
		final RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory());
		redisTemplate.setDefaultSerializer(getJwtRedisSerializer());
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}

	public JwtRedisSerializer getJwtRedisSerializer() {
		return new JwtRedisSerializer();
	}
}
