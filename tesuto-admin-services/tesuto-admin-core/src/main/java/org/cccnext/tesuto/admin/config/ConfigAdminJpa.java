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
package org.cccnext.tesuto.admin.config;

import java.beans.PropertyVetoException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableJpaRepositories(basePackages={"org.cccnext.tesuto.admin.repository","org.cccnext.tesuto.user.repository"},
entityManagerFactoryRef="entityManagerFactoryAdmin",
queryLookupStrategy=Key.CREATE_IF_NOT_FOUND,
transactionManagerRef="transactionManagerAdmin")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class ConfigAdminJpa {

	@Value("${JDBC_URL_ADMIN}")
	private String jdbcUrl;

	@Value("${JDBC_USER_ADMIN}")
	private String jdbcUser;

	@Value("${JDBC_PASSWORD_ADMIN}")
	String jdbcPassword;

	@Value("${database.driverClassName}")
	private String driverClassName;

	@Value("${database.maxIdleTime}")
	private int maxIdleTime;

	@Value("${database.initialPoolSize}")
	private int initialPoolSize;

	@Value("${database.minPoolSize}")
	private int minPoolSize;

	@Value("${database.maxPoolSize}")
	private int maxPoolSize;

	@Value("${database.testConnectionOnCheckin}")
	private Boolean testConnectionOnCheckin;

	@Value("${database.testConnectionOnCheckout}")
	private Boolean testConnectionOnCheckout;

	@Value("${database.idleConnectionTestPeriod}")
	private int idleConnectionTestPeriod;
		

	@Bean(name = "sqlDataSourceAdmin", destroyMethod = "close")
	public DataSource dataSource() throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(driverClassName);
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(jdbcUser);
		dataSource.setPassword(jdbcPassword);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setAcquireIncrement(1);
		dataSource.setAcquireRetryDelay(1000);
		dataSource.setAcquireRetryAttempts(30);
		dataSource.setAutoCommitOnClose(false);
		dataSource.setBreakAfterAcquireFailure(false);
		dataSource.setMaxIdleTime(maxIdleTime);
		dataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
		dataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
		dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
		dataSource.setCheckoutTimeout(10000);
		return dataSource;
	}

	@Bean(name = "persistentUnitManagerAdmin")
	public PersistenceUnitManager persistentUnitManagerAdmin() throws PropertyVetoException {
		DefaultPersistenceUnitManager unitManager = new DefaultPersistenceUnitManager();
		unitManager.setDefaultDataSource(dataSource());
		unitManager.setPackagesToScan("org.cccnext.tesuto.admin.model", "org.cccnext.tesuto.user.model");
		return unitManager;
	}

	@Bean(name = "entityManagerFactoryAdmin")
	public EntityManagerFactory localContainerEntityManagerFactoryBean() throws PropertyVetoException {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPersistenceUnitManager(persistentUnitManagerAdmin());
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
		entityManagerFactory.afterPropertiesSet();
		entityManagerFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

		return entityManagerFactory.getObject();
	}

	private HibernateJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		hibernateJpaVendorAdapter.setShowSql(false);
		return hibernateJpaVendorAdapter;
	}

	@Bean(name = "transactionManagerAdmin")
	public PlatformTransactionManager transactionManager() throws PropertyVetoException {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean());
		txManager.setDataSource(dataSource());
		return txManager;
	}
	
	@Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

}
