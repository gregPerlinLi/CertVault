package com.gregperlinli.certvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.security.SessionAuthFilter;
import com.gregperlinli.certvault.utils.AuthUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

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
@Slf4j
public class WebSecurityConfig {

// Deprecated: Old login verification method with traditional session.
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // 允许所有请求通过
//        http
//                .authorizeHttpRequests(authorize ->
//                        authorize.anyRequest().permitAll() // 全局开放所有路径
//                )
//                .csrf(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> {
                    sess.sessionCreationPolicy(SessionCreationPolicy.NEVER);
                    sess.sessionFixation().none();
                    sess.maximumSessions(1);
                })
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
                            .requestMatchers("/api/*/superadmin/**").hasRole("SUPERADMIN")
                            .requestMatchers("/api/*/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                            .requestMatchers("/api/*/user/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                            .anyRequest().permitAll();
                })
                // 异常处理配置
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling
                            .defaultAuthenticationEntryPointFor(
                                    new CustomAuthenticationEntryPoint(),
                                    new AntPathRequestMatcher("/**")
                            )
                            .accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                // 添加自定义过滤器
                .addFilterAfter(sessionAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                // 禁用内置认证
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public SessionAuthFilter sessionAuthFilter() {
        return new SessionAuthFilter();
    }

    @Component
    static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException, java.io.IOException {
            log.warn("Unauthenticated request URI: {}", request.getRequestURI());
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResultVO<Void>(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "No valid session found.")));
        }
    }

    @Component
    static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws java.io.IOException, ServletException {
            log.warn("User [{}|{}] try to access unauthorized URI: {}",
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                    AuthUtils.roleIdToRoleName(((UserProfileDTO) request.getSession().getAttribute("account")).getRole()),
                    request.getRequestURI());
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResultVO<Void>(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "Insufficient permissions.")));
        }
    }

}
