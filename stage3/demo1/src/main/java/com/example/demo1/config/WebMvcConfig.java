package com.example.demo1.config;

import com.example.demo1.interceptor.UserTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fyb
 * @since 2022/1/26
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Bean
    public UserTokenInterceptor userTokenInterceptor() {

        return new UserTokenInterceptor();

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(userTokenInterceptor()).addPathPatterns("/api/**").excludePathPatterns("/login1");

    }


}
