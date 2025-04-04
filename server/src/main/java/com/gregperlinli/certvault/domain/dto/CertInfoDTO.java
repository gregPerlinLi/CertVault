package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Certificate Info DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertInfoDTO}
 * @date 2025/3/18 20:42
 */
@Schema(
        name = "Certificate Info DTO",
        description = "Certificate information transfer object containing metadata and lifecycle details"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertInfoDTO {

    @Schema(
            name = "uuid",
            description = "Unique identifier for the certificate",
            example = "72267ce5-e94a-4cdb-b35b-63f1f385b253",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    @Schema(
            name = "caUuid",
            description = "Unique identifier for the Certificate Authority that issued the certificate",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String caUuid;

    @Schema(
            name = "owner",
            description = "Username of the certificate owner",
            example = "gregPerlinLi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String owner;

    @Schema(
            name = "comment",
            description = "Optional description or notes about the certificate",
            example = "Cert Vault SSL Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String comment;

    @Schema(
            name = "notBefore",
            description = "Start date of the certificate's validity period (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notBefore;

    @Schema(
            name = "notAfter",
            description = "End date of the certificate's validity period (ISO 8601 format)",
            example = "2025-06-17T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notAfter;

    @Schema(
            name = "createdAt",
            description = "Date when the certificate was created (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime createdAt;

    @Schema(
            name = "modifiedAt",
            description = "Date when the certificate was last modified (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime modifiedAt;

}
