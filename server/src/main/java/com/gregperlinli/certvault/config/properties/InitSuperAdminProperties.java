package com.gregperlinli.certvault.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Init Super Admin Properties
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code InitSuperAdminProperties}
 * @date 2025/3/23 15:36
 */
@ConfigurationProperties(prefix = "init.superadmin")
@Data
@Component
public class InitSuperAdminProperties {
    /**
     * Super Admin username
     */
    private String username = "superadmin";

    /**
     * Super Admin password
     */
    private String password = "CertVault@2025";

    /**
     * Super Admin email
     */
    private String email = "superadmin@certvault.example";

    /**
     * Super Admin display name
     */
    private String displayName = "Default Superadmin";
}
