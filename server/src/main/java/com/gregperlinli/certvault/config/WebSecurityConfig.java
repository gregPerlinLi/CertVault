package com.gregperlinli.certvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Config
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code WebSecurityConfig}
 * @date 2025/3/10 16:15
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 允许所有请求通过
        http
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll() // 全局开放所有路径
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
