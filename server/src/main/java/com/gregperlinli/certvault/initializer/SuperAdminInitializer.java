package com.gregperlinli.certvault.initializer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gregperlinli.certvault.config.properties.InitSuperAdminProperties;
import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * Superadmin Initializer
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SuperAdminInitializer}
 * @date 2025/3/23 15:51
 */
@Component
@Order
public class SuperAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    @Resource
    private InitSuperAdminProperties config;

    @Resource
    private IUserService userService;

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.hasText(config.getUsername())) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username", config.getUsername());
            if ( userService.getOne(userQueryWrapper) == null ) {
                LocalDateTime now = LocalDateTime.now();
                User user = new User();
                user.setUsername(config.getUsername());
                user.setPassword(AuthUtils.encryptPassword(config.getPassword()));
                user.setEmail(config.getEmail());
                user.setDisplayName(config.getDisplayName());
                user.setRole(AccountTypeConstant.SUPERADMIN.getAccountType());
                user.setCreatedAt(now);
                user.setModifiedAt(now);
                userService.save(user);
                log.info("Super admin account created successfully");
            } else {
                log.info("Super admin account already exists, skipping creation");
            }
        } else {
            log.warn("Super admin configuration is missing");
        }
    }
}
