package com.gregperlinli.certvault.config;

import com.gregperlinli.certvault.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configurator
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code WebMvcConfigurer}
 * @date 2025/3/3 15:55
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Deprecated: Old login verification method with traditional session.
        // registry.addInterceptor(new LoginInterceptor())
        //        .addPathPatterns("/api/*/user/**", "/api/*/admin/**", "/api/*/superadmin/**");
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
