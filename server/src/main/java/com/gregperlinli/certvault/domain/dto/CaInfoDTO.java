package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CA Information DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaInfoDTO}
 * @date 2025/3/17 22:06
 */
@Schema(
        name = "CA Information DTO",
        description = "Certificate Authority information transfer object"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CaInfoDTO {

    /**
     * UUID of the Certificate Authority
     */
    @Schema(
            name = "uuid",
            description = "Unique identifier of the Certificate Authority",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    /**
     * Algorithm used for generating the CA certificate
     */
    @Schema(
            name = "algorithm",
            description = "Algorithm used for generating the CA certificate",
            example = "RSA",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String algorithm;

    /**
     * Size of the key in bits
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
     * Username of the CA owner
     */
    @Schema(
            name = "owner",
            description = "Username of the CA owner",
            example = "gregPerlinLi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String owner;

    /**
     * UUID of the parent Certificate Authority (if any)
     */
    @Schema(
            name = "parentCa",
            description = "UUID of the parent Certificate Authority (if any)",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0"
    )
    private String parentCa;

    /**
     * Indicates if sub CAs can be created under this CA
     */
    @Schema(
            name = "allowSubCa",
            description = "Whether sub CAs can be created under this CA",
            example = "true",
            type = "Boolean"
    )
    private Boolean allowSubCa;

    /**
     * Optional description or notes about the CA
     */
    @Schema(
            name = "comment",
            description = "Optional description or notes about the CA",
            example = "Cert Vault Default Intermediate Certificate Authority",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String comment;

    /**
     * Indicates if the CA is currently active
     */
    @Schema(
            name = "available",
            description = "Indicates if the CA is currently active",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Boolean"
    )
    private Boolean available;

    /**
     * Start date of CA validity period (ISO 8601 format)
     */
    @Schema(
            name = "notBefore",
            description = "Start date of CA validity period (ISO 8601 format)",
            example = "2025-03-23T10:14:45+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "LocalDateTime"
    )
    private LocalDateTime notBefore;

    /**
     * End date of CA validity period (ISO 8601 format)
     */
    @Schema(
            name = "notAfter",
            description = "End date of CA validity period (ISO 8601 format)",
            example = "2035-03-21T10:14:45+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "LocalDateTime"
    )
    private LocalDateTime notAfter;

    /**
     * Date when the CA was created (ISO 8601 format)
     */
    @Schema(
            name = "createdAt",
            description = "Date when the CA was created (ISO 8601 format)",
            example = "2025-03-18T11:10:14+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime createdAt;

    /**
     * Date when the CA was last modified (ISO 8601 format)
     */
    @Schema(
            name = "modifiedAt",
            description = "Date when the CA was last modified (ISO 8601 format)",
            example = "2025-03-23T10:14:45+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime modifiedAt;

}
