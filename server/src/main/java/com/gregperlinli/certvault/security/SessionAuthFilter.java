package com.gregperlinli.certvault.security;

import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Session Auth Filter
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SessionAuthFilter}
 * @date 2025/3/27 10:02
 */
@Component
@Slf4j
public class SessionAuthFilter extends OncePerRequestFilter {

    @Resource
    IUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取现有Session
        HttpSession session = request.getSession();
        if (session != null) {
            String sessionId = session.getId();
            UserProfileDTO redisProfile = userService.loginVerify(sessionId);
            UserProfileDTO sessionProfile = (UserProfileDTO) session.getAttribute("account");

            if ( redisProfile != null && sessionProfile != null ) {
                if (
                        redisProfile.getUsername().equals(sessionProfile.getUsername()) &&
                        redisProfile.getEmail().equals(sessionProfile.getEmail()) &&
                        redisProfile.getRole().equals(sessionProfile.getRole())
                ) {
                    log.info("User [{}|{}] is request URI: {}",
                            redisProfile.getUsername(),
                            AuthUtils.roleIdToRoleName(redisProfile.getRole()),
                            request.getRequestURI());
                    log.debug("Request session ID: {}", session.getId());
                    // 构建Spring Security认证对象
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            redisProfile.getUsername(),
                            null,
                            AuthorityUtils.createAuthorityList("ROLE_" + AuthUtils.roleIdToRoleName(redisProfile.getRole()))
                    );

                    // 将认证信息存入Security上下文
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.warn("Session verification failed, the info in session and redis are not match");
                    log.warn("Session Info ==> {}", session.getAttribute("account").toString());
                    log.warn("Redis Info ==> {}", redisProfile);
                    userService.logout(sessionId);
                    request.getSession().invalidate();
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("Session is expired.");
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
