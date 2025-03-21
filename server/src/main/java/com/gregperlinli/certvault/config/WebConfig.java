package com.gregperlinli.certvault.config;

import com.gregperlinli.certvault.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
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
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/*/user/**", "/api/*/admin/**", "/api/*/superadmin/**");
    }
}
