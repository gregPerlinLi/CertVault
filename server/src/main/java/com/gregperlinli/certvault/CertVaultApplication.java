package com.gregperlinli.certvault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Application entry point
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertVaultApplication}
 * @date 2025/3/3 15:50
 */
@MapperScan("com.gregperlinli.certvault.mapper")
@SpringBootApplication
public class CertVaultApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CertVaultApplication.class);
        String springFrameworkVersion = SpringVersion.getVersion();
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.version", springFrameworkVersion);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
