package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.GenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response Certificate DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ResponseCertDTO}
 * @date 2025/3/18 16:04
 */
@Schema(
        name = "Response Certificate DTO",
        description = "Certificate response data transfer object containing generated certificate and private key details"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ResponseCertDTO {

    /**
     * UUID
     */
    @Schema(
            name = "uuid",
            description = "Unique identifier for the certificate",
            example = "9777b56b-90c3-4315-91c6-79ea658b0d9a",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String uuid;

    /**
     * Private key with BASE64 encoding
     */
    @Schema(
            name = "privkey",
            description = "Private key with BASE64 encoding",
            example = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQU...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String privkey;

    /**
     * Certificate with BASE64 encoding
     */
    @Schema(
            name = "cert",
            description = "Certificate with BASE64 encoding",
            example = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUV2Z0lCQU...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String cert;

    /**
     * CA UUID
     */
    @Schema(
            name = "caUuid",
            description = "CA UUID",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "UUID"
    )
    private String caUuid;

    /**
     * Effective Date
     */
    @Schema(
            name = "notBefore",
            description = "Start date of certificate validity period (ISO 8601 format)",
            example = "2025-03-18T17:54:38.021",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notBefore;

    /**
     * Expiration Date
     */
    @Schema(
            name = "notAfter",
            description = "End date of certificate validity period (ISO 8601 format)",
            example = "2025-04-17T17:54:38.021",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notAfter;

    /**
     * Comment
     */
    @Schema(
            name = "comment",
            description = "Optional description or notes about the certificate",
            example = "CertVault SSL Certificate",
            type = "String"
    )
    private String comment;

    public ResponseCertDTO(GenResponse response, String caUuid) {
        this.uuid = response.getUuid();
        this.privkey = response.getPrivkey();
        this.cert = response.getCert();
        this.caUuid = caUuid;
        this.notBefore = response.getNotBefore();
        this.notAfter = response.getNotAfter();
        this.comment = response.getComment();
    }

}
