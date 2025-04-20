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

    /**
     * Certificate UUID
     */
    @Schema(
            name = "uuid",
            description = "Unique identifier for the certificate",
            example = "72267ce5-e94a-4cdb-b35b-63f1f385b253",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    /**
     * Certificate CA UUID
     */
    @Schema(
            name = "caUuid",
            description = "Unique identifier for the Certificate Authority that issued the certificate",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String caUuid;

    /**
     * Certificate algorithm
     */
    @Schema(
            name = "algorithm",
            description = "Algorithm used to generate the certificate",
            example = "RSA",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String algorithm;

    /**
     * Certificate key size
     */
    @Schema(
            name = "keySize",
            description = "Size of the key in bits",
            example = "2048",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Integer"
    )
    private Integer keySize;

    /**
     * Certificate owner
     */
    @Schema(
            name = "owner",
            description = "Username of the certificate owner",
            example = "gregPerlinLi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String owner;

    /**
     * Certificate comment
     */
    @Schema(
            name = "comment",
            description = "Optional description or notes about the certificate",
            example = "Cert Vault SSL Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String comment;

    /**
     * Certificate validity period start date
     */
    @Schema(
            name = "notBefore",
            description = "Start date of the certificate's validity period (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notBefore;

    /**
     * Certificate validity period end date
     */
    @Schema(
            name = "notAfter",
            description = "End date of the certificate's validity period (ISO 8601 format)",
            example = "2025-06-17T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notAfter;

    /**
     * Certificate creation date
     */
    @Schema(
            name = "createdAt",
            description = "Date when the certificate was created (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime createdAt;

    /**
     * Certificate modification date
     */
    @Schema(
            name = "modifiedAt",
            description = "Date when the certificate was last modified (ISO 8601 format)",
            example = "2025-03-19T01:38:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime modifiedAt;

}
