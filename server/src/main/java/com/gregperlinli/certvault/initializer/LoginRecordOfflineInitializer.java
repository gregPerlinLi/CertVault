package com.gregperlinli.certvault.initializer;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gregperlinli.certvault.config.properties.InitSuperAdminProperties;
import com.gregperlinli.certvault.domain.entities.LoginRecord;
import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Login Record Offline Initializer <br/>
 * (Set all login record status offline)
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SuperAdminInitializer}
 * @date 2025/3/23 15:51
 */
@Component
@Order
public class LoginRecordOfflineInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LoginRecordOfflineInitializer.class);

    @Resource
    private InitSuperAdminProperties config;

    @Resource
    ILoginRecordService loginRecordService;

    @Override
    public void run(ApplicationArguments args) {
        UpdateWrapper<LoginRecord> loginRecordUpdateWrapper = new UpdateWrapper<>();
        loginRecordUpdateWrapper.eq("online", true)
                .set("online", false);
        loginRecordService.update(loginRecordUpdateWrapper);
        log.debug("Login Record Offline Initializer: Set all login record status offline");
    }
}
