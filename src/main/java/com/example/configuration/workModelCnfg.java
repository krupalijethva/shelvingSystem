package com.example.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.repository2", entityManagerFactoryRef = "t360entityManagerFactory", transactionManagerRef = "t360TransactionManager")
@EnableWebMvc
@PropertySource("classpath:jpa.properties")
public class workModelCnfg {

	@Autowired
	private Environment environment;

	@Bean
	public LocalContainerEntityManagerFactoryBean t360entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(t360dataSource());
		em.setPackagesToScan("com.example");
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		properties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		properties.setProperty("hibernate.current_session_context_class",
				environment.getProperty("hibernate.current_session_context_class"));
		return properties;
	}

	@Bean
	public DataSource t360dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getProperty("hibernate.connection.driver_class"));
		dataSource.setUrl(environment.getProperty("hibernate.t360connection.url"));
		dataSource.setUsername(environment.getProperty("hibernate.connection.username"));
		dataSource.setPassword(environment.getProperty("hibernate.connection.password"));
		return dataSource;
	}

	@Bean
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/jsp/");
		bean.setSuffix(".jsp");
		return bean;
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**", "/img/**", "/css/**", "/js/**", "/jsp/**", "/dist/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/", "classpath:/static/img/",
						"classpath:/static/css/", "classpath:/static/js/", "classpath:/static/dist/angular4-client/",
						"classpath:/WEB-INF/jsp/");
	}

	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public PlatformTransactionManager t360TransactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(t360entityManagerFactory().getObject());
		return transactionManager;
	}

}
