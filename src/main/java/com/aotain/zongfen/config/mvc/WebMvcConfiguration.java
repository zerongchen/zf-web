package com.aotain.zongfen.config.mvc;

import com.aotain.zongfen.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.aotain.zongfen.interceptor.ExportInterceptor;
import com.aotain.zongfen.interceptor.ImportInterceptor;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	/**
	 * 静态资源访问
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		 //将所有/resources/** 访问都映射到classpath:/resources/ 目录下
		registry
			.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/")
				.setCachePeriod(100000);
		super.addResourceHandlers(registry);
	}
	/**
	 * import function
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链
		registry.addInterceptor(new ImportInterceptor()).addPathPatterns("/**/import.do");
		registry.addInterceptor(new ExportInterceptor()).addPathPatterns("/**/exportTemplate.do");
		registry.addInterceptor(new ExportInterceptor()).addPathPatterns("/**/export.do");
		registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");

		super.addInterceptors(registry);
	}

}