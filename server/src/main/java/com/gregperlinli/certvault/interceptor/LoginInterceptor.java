package com.gregperlinli.certvault.interceptor;

import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.exception.LoginException;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

/**
 * Login Interceptor
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LoginInterceptor}
 * @date 2025/3/10 17:28
 * @deprecated Old login verification method with traditional session.
 */
@Deprecated
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    // 定义路径与角色权限的映射关系
    private static final Map<String, List<Integer>> PATH_ROLE_MAP = new LinkedHashMap<>();

    static {
        // 路径模式需按优先级从高到低排列（如更具体的路径放前面）
        PATH_ROLE_MAP.put("/api/*/superadmin/**", List.of(AccountTypeConstant.SUPERADMIN.getAccountType()));
        PATH_ROLE_MAP.put("/api/*/admin/**", Arrays.asList(AccountTypeConstant.ADMIN.getAccountType(), AccountTypeConstant.SUPERADMIN.getAccountType()));
        PATH_ROLE_MAP.put("/api/*/users/**", Arrays.asList(AccountTypeConstant.USER.getAccountType(), AccountTypeConstant.ADMIN.getAccountType(), AccountTypeConstant.SUPERADMIN.getAccountType()));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        IUserService userService = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext())
                .getBean(IUserService.class);

        // 忽略OPTIONS请求（CORS预检）
        if (HttpMethod.OPTIONS.matches(request.getMethod().toUpperCase())) {
            return true;
        }

        // 获取会话（不自动创建新会话）
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "No valid session found.");
        }

        // 提取用户信息
        // Object username = session.getAttribute("username");
        UserProfileDTO account = (UserProfileDTO) session.getAttribute("account");
        if ( account == null ) {
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "No user information found in the session.");
        }
        // String account = username.toString();
        // Integer accountType = (Integer) session.getAttribute("account_type");

        // 路径权限校验
        String requestURI = request.getRequestURI();
        log.info("User: {}, Request URI: {}", account.getUsername(), requestURI);
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<Integer> requiredRoles = null;

        for (Map.Entry<String, List<Integer>> entry : PATH_ROLE_MAP.entrySet()) {
            boolean matched = pathMatcher.match(entry.getKey(), requestURI);
            log.debug("Path Check: {} {} {}", requestURI, matched ? "matched" : "missed", entry.getKey());
            if (matched) {
                requiredRoles = entry.getValue();
                break;
            }
        }

        // 如果匹配到需要权限的路径，检查用户角色
        if (requiredRoles != null) {
            boolean hasPermission = requiredRoles.contains(account.getRole());
            if (!hasPermission) {
                log.info("Insufficient privileges: Path [{}] requires role [{}], user role is [{}].", requestURI, requiredRoles, account.getRole());
                throw new LoginException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "Insufficient privileges");
            }
        }

        // 验证会话有效性
        // if (account == null) {
        //     log.info("No user information found in the session.");
        //     throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "Not logged in or session expired");
        // }

        String sessionId = session.getId();
        UserProfileDTO redisAccount = userService.loginVerify(sessionId);
        if ( redisAccount == null ) {
            log.info("User [{}] Session [{}] does not exist in Redis.", account, sessionId);
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "The session has expired.");
        }
        if ( !Objects.equals(account.getUsername(), redisAccount.getUsername()) || !Objects.equals(account.getRole(), redisAccount.getRole()) ) {
            throw new LoginException(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), "The session information does not match.");
        }

        return true;
    }

}
