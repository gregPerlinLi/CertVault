package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Login Record DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code LoginRecordDTO}
 * @date 2025/4/5 20:34
 */
@Schema(
        name = "Login Record DTO",
        description = "Login Record DTO"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class LoginRecordDTO {

    /**
     * session uuid
     */
    @Schema(
            name = "uuid",
            description = "Session UUID",
            example = "1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String uuid;

    /**
     * Username
     */
    @Schema(
            name = "username",
            description = "Username",
            example = "gregPerlinLi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String username;

    /**
     * IP address
     */
    @Schema(
            name = "ipAddress",
            description = "IP address",
            example = "10.18.0.1",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String ipAddress;

    /**
     * Region
     */
    @Schema(
            name = "region",
            description = "Region",
            example = "China",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String region;

    /**
     * Province
     */
    @Schema(
            name = "province",
            description = "Province",
            example = "Guangdong",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String province;

    /**
     * City
     */
    @Schema(
            name = "city",
            description = "City",
            example = "Canton",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String city;

    /**
     * Browser
     */
    @Schema(
            name = "browser",
            description = "Browser",
            example = "Chrome",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String browser;

    /**
     * Operating system
     */
    @Schema(
            name = "os",
            description = "Operating system",
            example = "Windows 10",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String os;

    /**
     * Platform
     */
    @Schema(
            name = "platform",
            description = "Platform",
            example = "Windows",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string"
    )
    private String platform;

    /**
     * Login time
     */
    @Schema(
            name = "loginTime",
            description = "Login time",
            example = "2025-04-05T20:34:00+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "date-time"
    )
    private LocalDateTime loginTime;

    /**
     * Online status
     */
    @Schema(
            name = "isOnline",
            description = "Online status",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "boolean"
    )
    private Boolean isOnline;

    /**
     * Is current session
     */
    @Schema(
            name = "isCurrentSession",
            description = "This session is current session",
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "boolean"
    )
    private Boolean isCurrentSession = false;

    public LoginRecordDTO(String uuid, String username, String ipAddress, String region, String province, String city, String browser, String os, String platform, LocalDateTime loginTime, Boolean isOnline) {
        this.uuid = uuid;
        this.username = username;
        this.ipAddress = ipAddress;
        this.region = region;
        this.province = province;
        this.city = city;
        this.browser = browser;
        this.os = os;
        this.platform = platform;
       this.loginTime = loginTime;
    }

}
