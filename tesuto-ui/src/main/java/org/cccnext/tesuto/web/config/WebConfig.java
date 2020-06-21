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
package org.cccnext.tesuto.web.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.cccnext.tesuto.springboot.web.interceptor.GlobalUrlInterceptor;
import org.cccnext.tesuto.web.security.DefaultRolesPrefixPostProcessor;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
   
    @Bean
    @Primary
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setOrder(1);
        resolver.setContentNegotiationManager(contentNegotiationManager());
        
        List<ViewResolver> viewResolvers = new ArrayList<>();
        viewResolvers.add(viewResolver());
        resolver.setViewResolvers(viewResolvers);
        
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        
        view.setPrefixJson(true);
        return resolver;
    }
    
    public ContentNegotiationManager contentNegotiationManager() {
        ContentNegotiationManagerFactoryBean contentNegotiation = new ContentNegotiationManagerFactoryBean();
        contentNegotiation.setFavorPathExtension(false);
        contentNegotiation.setFavorParameter(false);
        contentNegotiation.addMediaType("html", MediaType.TEXT_HTML);
        contentNegotiation.addMediaType("json", MediaType.APPLICATION_JSON);
        //contentNegotiation.setDefaultContentTypeStrategy(contentNegotiationStrategy());
        return contentNegotiation.getObject();
    }
    
    public ContentNegotiationStrategy contentNegotiationStrategy() {
    	return new FixedContentNegotiationStrategy(ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST);
    }
    
    
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        
        configurer.ignoreAcceptHeader(true);
        configurer.defaultContentTypeStrategy(contentNegotiationStrategy());
    }

    @Bean(name = "commonsMultipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(20971520);
        return commonsMultipartResolver;
    }

    @Bean
    public FilterRegistrationBean multipartFilterRegistrationBean() {
        final MultipartFilter multipartFilter = new MultipartFilter();
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(multipartFilter);
        filterRegistrationBean.addInitParameter("multipartResolverBeanName", "commonsMultipartResolver");
        return filterRegistrationBean;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(globalUrlInterceptor());
    }
    
    @Bean
    public GlobalUrlInterceptor globalUrlInterceptor() {
        return new GlobalUrlInterceptor();
    }
    
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(99);
        return viewResolver;
    }
    
    @Bean("messageSource")
    public ReloadableResourceBundleMessageSource messages(){
        ReloadableResourceBundleMessageSource msg = new ReloadableResourceBundleMessageSource();
        msg.setBasename("classpath:messages");
        msg.setDefaultEncoding("UTF-8");
        return msg;
    }
    
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
    
    @Bean
    public DefaultRolesPrefixPostProcessor postProcessor() {
    	return new DefaultRolesPrefixPostProcessor();
    }

    
    @Bean(name = "SpringOpenEntityManagerInViewFilter")
    public Filter SpringOpenEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }
    
    @Bean(name = "hiddenHttpMethodFilter")
    public Filter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    
    @Bean(name = "encoding-filter")
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter =  new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }
 
    
    public void addViewControllers(ViewControllerRegistry registry) {
        
    }


	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		//Auto-generated method stub
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		//Auto-generated method stub
		
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		//Auto-generated method stub
		
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		//Auto-generated method stub
		
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//Auto-generated method stub
		
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//Auto-generated method stub
		
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		//Auto-generated method stub
		
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		//Auto-generated method stub
		
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//Auto-generated method stub
		
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		//Auto-generated method stub
		
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		//Auto-generated method stub
		
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		//Auto-generated method stub
		
	}

	@Override
	public Validator getValidator() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		//Auto-generated method stub
		return null;
	}

}
