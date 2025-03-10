package com.gregperlinli.certvault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        org.springframework.boot.SpringApplication.run(CertVaultApplication.class, args);
    }
}
