package com.gregperlinli.certvault.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring Doc Configuration
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SpringDocConfig}
 * @date 2025/4/2 10:09
 */
@Configuration
public class SpringDocConfig {

    @Value(value = "${spring.application.version}")
    private String appVersion;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(this.getApiInfo())
                .components(new Components()
                        .addSecuritySchemes("Session Auth", new SecurityScheme()
                                .name("Session Auth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }

    private Info getApiInfo() {
        return new Info()
                .title("CertVault API")
                .description("CertVault Self-Signed SSL Certificate Management Platform.")
                .contact(new Contact()
                        .name("gregPerlinLi")
                        .url("https://github.com/gregPerlinLi")
                        .email("lihaolin13@outlook.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://github.com/gregPerlinLi/CertVault/blob/main/LICENSE"))
                .summary("CertVault RESTful API Doc")
                .version(appVersion);
    }

}
