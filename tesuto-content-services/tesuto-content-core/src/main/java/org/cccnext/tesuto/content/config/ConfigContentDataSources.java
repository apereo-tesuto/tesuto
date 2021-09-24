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
package org.cccnext.tesuto.content.config;

import java.beans.PropertyVetoException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
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
@EnableJpaRepositories(basePackages={"org.cccnext.tesuto.content.repository.jpa"},
entityManagerFactoryRef="entityManagerFactoryContent",
queryLookupStrategy=Key.CREATE_IF_NOT_FOUND,
transactionManagerRef="transactionManagerContent")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo", "org.cccnext.tesuto.message.repository.mongo"})
public class ConfigContentDataSources {

	@Value("${JDBC_URL_CONTENT}")
	private String jdbcUrl;

	@Value("${JDBC_USER_CONTENT}")
	private String jdbcUser;

	@Value("${JDBC_PASSWORD_CONTENT}")
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
		
	private ComboPooledDataSource contentDataSource;
	
	private EntityManagerFactory entityManagerFactory;
	
	@Bean(name = "sqlDataSourceContent", destroyMethod = "close")
	public DataSource dataSource() throws PropertyVetoException {
		if(contentDataSource != null)
			return contentDataSource;
		contentDataSource = new ComboPooledDataSource();
		contentDataSource.setDriverClass(driverClassName);
		contentDataSource.setJdbcUrl(jdbcUrl);
		contentDataSource.setUser(jdbcUser);
		contentDataSource.setPassword(jdbcPassword);
		contentDataSource.setInitialPoolSize(initialPoolSize);
		contentDataSource.setMinPoolSize(minPoolSize);
		contentDataSource.setMaxPoolSize(maxPoolSize);
		contentDataSource.setAcquireIncrement(1);
		contentDataSource.setAcquireRetryDelay(1000);
		contentDataSource.setAcquireRetryAttempts(30);
		contentDataSource.setAutoCommitOnClose(false);
		contentDataSource.setBreakAfterAcquireFailure(false);
		contentDataSource.setMaxIdleTime(maxIdleTime);
		contentDataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
		contentDataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
		contentDataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
		contentDataSource.setCheckoutTimeout(10000);
		return contentDataSource;
	}

	@Bean(name = "persistentUnitManagerContent")
	public PersistenceUnitManager persistentUnitManagerContent() throws PropertyVetoException {
		DefaultPersistenceUnitManager unitManager = new DefaultPersistenceUnitManager();
		unitManager.setDefaultDataSource(dataSource());
		unitManager.setPackagesToScan("org.cccnext.tesuto.content.model");
		return unitManager;
	}

	@Bean(name = "entityManagerFactoryContent")
	public EntityManagerFactory localContainerEntityManagerFactoryBean() throws PropertyVetoException {
		if(entityManagerFactory != null){
			return entityManagerFactory;
		}
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource());
		entityManagerFactoryBean.setPersistenceUnitManager(persistentUnitManagerContent());
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		entityManagerFactoryBean.afterPropertiesSet();
		entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		entityManagerFactory = entityManagerFactoryBean.getObject();
		return entityManagerFactory;
	}

	private HibernateJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		hibernateJpaVendorAdapter.setShowSql(false);
		return hibernateJpaVendorAdapter;
	}

	@Bean(name = "transactionManagerContent")
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
