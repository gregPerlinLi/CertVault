package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.LoginRecord;
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
            type = "String"
    )
    private String uuid;

    /**
     * Username
     */
    @Schema(
            name = "username",
            description = "Username",
            example = "gregPerlinLi"
    )
    private String username;

    /**
     * IP address
     */
    @Schema(
            name = "ipAddress",
            description = "IP address",
            example = "10.18.0.1"
    )
    private String ipAddress;

    /**
     * Browser
     */
    @Schema(
            name = "browser",
            description = "Browser",
            example = "Chrome"
    )
    private String browser;

    /**
     * Operating system
     */
    @Schema(
            name = "os",
            description = "Operating system",
            example = "Windows 10"
    )
    private String os;

    /**
     * Platform
     */
    @Schema(
            name = "platform",
            description = "Platform",
            example = "Windows"
    )
    private String platform;

    /**
     * Login time
     */
    @Schema(
            name = "loginTime",
            description = "Login time",
            example = "2025-04-05 20:34:00"
    )
    private LocalDateTime loginTime;

    /**
     * Online status
     */
    @Schema(
            name = "isOnline",
            description = "Online status",
            example = "true"
    )
    private Boolean isOnline;

}
