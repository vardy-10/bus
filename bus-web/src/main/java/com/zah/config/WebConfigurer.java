package com.zah.config;

import com.zah.config.intercepors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
	@Autowired
	private LoginInterceptor loginInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCorsMappings(CorsRegistry arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFormatters(FormatterRegistry arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInterceptors(InterceptorRegistry arg0) {
		arg0.addInterceptor(loginInterceptor).addPathPatterns("/manage/**").excludePathPatterns("/manage/login")
				.excludePathPatterns("/manage").excludePathPatterns("/weixin/**").excludePathPatterns("/menu");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry arg0) {
		arg0.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		arg0.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");

	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addViewControllers(ViewControllerRegistry arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configurePathMatch(PathMatchConfigurer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
