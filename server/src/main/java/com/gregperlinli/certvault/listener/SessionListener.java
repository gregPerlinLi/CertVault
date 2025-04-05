package com.gregperlinli.certvault.listener;

import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Session Listener
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SessionListener}
 * @date 2025/4/5 21:31
 */
@Component
@Slf4j
public class SessionListener implements HttpSessionListener {

    @Resource
    IUserService userService;

    @Resource
    ILoginRecordService loginRecordService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("Session destroyed: {}", se.getSession().getId());
        userService.logout(se.getSession().getId());
        SecurityContextHolder.clearContext();
        loginRecordService.setRecordOffline(se.getSession().getId());
    }
}
