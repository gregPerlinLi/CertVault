package com.gregperlinli.certvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CreateUserDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.security.SessionAuthFilter;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spring Security Config
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code WebSecurityConfig}
 * @date 2025/3/10 16:15
 */
@EnableMethodSecurity
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

    @Resource
    IUserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> {
                    sess.sessionCreationPolicy(SessionCreationPolicy.NEVER);
                    sess.sessionFixation().none();
                    sess.maximumSessions(3);
                })
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
//                            .requestMatchers("/api/*/superadmin/**").hasRole("SUPERADMIN")
//                            .requestMatchers("/api/*/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
//                            .requestMatchers("/api/*/user/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                            .anyRequest().permitAll();
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/v1/auth/oauth/login")
                        .defaultSuccessUrl("/api/v1/auth/oauth/success")
                        .failureUrl("/api/v1/auth/oauth/failure")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(this.userAuthoritiesMapper())
                        )
                )
                // 异常处理配置
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling
                            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
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

//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//        return request -> {
//            OAuth2User oAuth2User = delegate.loadUser(request);
//
//            String clientName = request.getClientRegistration().getClientName();
//            if ("oidc".equals(clientName)) {
//                try {
//                    return processOidcUser(oAuth2User);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            return oAuth2User;
//        };
//    }

    private OAuth2User processOidcUser(OAuth2User oAuth2User) throws Exception {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        // 查找用户，如果不存在则创建
        UserProfileDTO userProfileDTO = userService.findByEmail(email);
        if ( userProfileDTO == null ) {
            userService.createUser(new CreateUserDTO((String) attributes.get("preferred_username"),
                    (String) attributes.get("name"),
                    email,
                    null,
                    AccountTypeConstant.USER.getAccountType()));
        }

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + AuthUtils.roleIdToRoleName(userProfileDTO.getRole()));
        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    private void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, java.io.IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        // 设置session
        request.getSession().setAttribute("account", userService.findByEmail(email));

        // 重定向到主页
        response.sendRedirect("/");
    }

    @Component
    static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException, java.io.IOException {
            log.warn("Unauthenticated request URI: {}", request.getRequestURI());
            response.setContentType("application/json");
            if ( authException instanceof SessionAuthenticationException) {
                response.getWriter().write(new ObjectMapper().writeValueAsString(new ResultVO<Void>(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "Exceeds the maximum allowed number of sessions.")));
            }
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
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResultVO<Void>(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "Insufficient privileges.")));
        }
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            List<GrantedAuthority> mappedAuthorities = new ArrayList<>();
            authorities.forEach(authority -> {
                if (authority.getAuthority().startsWith("ROLE_")) {
                    mappedAuthorities.add(authority);
                } else {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
            });
            return mappedAuthorities;
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
