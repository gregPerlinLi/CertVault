package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.Ca;
import com.gregperlinli.certvault.domain.entities.GenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response CA Certificate DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code ResponseCaDTO}
 * @date 2025/3/17 16:54
 */
@Schema(
        name = "Response CA DTO",
        description = "Certificate Authority (CA) response data transfer object containing generated certificate and private key details"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ResponseCaDTO {

    /**
     * UUID
     */
    @Schema(
            name = "uuid",
            description = "Unique identifier for the CA certificate",
            example = "bf35ecb1-9b67-4083-9476-e264ba153188",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String uuid;

    /**
     * Private key with BASE64 encoding
     */
    @Schema(
            name = "privkey",
            description = "Private key with BASE64 encoding",
            example = "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2QUl...",
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
            example = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUV2QUl...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String cert;

    /**
     * Parent CA UUID
     */
    @Schema(
            name = "parentCa",
            description = "Parent CA UUID",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            type = "String"
    )
    private String parentCa;

    /**
     * Whether to allow issue subordinate CA
     */
    @Schema(
            name = "allowSubCa",
            description = "Whether to allow issue subordinate CA",
            example = "true",
            type = "Boolean"
    )
    private Boolean allowSubCa;

    /**
     * Effective Date
     */
    @Schema(
            name = "notBefore",
            description = "Start date of CA validity period (ISO 8601 format)",
            example = "2025-03-23T12:49:45.733",
            type = "OffsetDateTime"
    )
    private LocalDateTime notBefore;

    /**
     * Expiration Date
     */
    @Schema(
            name = "notAfter",
            description = "End date of CA validity period (ISO 8601 format)",
            example = "2025-09-19T12:49:45.733",
            type = "OffsetDateTime"
    )
    private LocalDateTime notAfter;

    /**
     * Comment
     */
    @Schema(
            name = "comment",
            description = "Optional description or notes about the CA",
            example = "Cert Vault Default Intermediate Certificate Authority",
            type = "String"
    )
    private String comment;

    public ResponseCaDTO(GenResponse response) {
        this.uuid = response.getUuid();
        this.privkey = response.getPrivkey();
        this.cert = response.getCert();
        this.notBefore = response.getNotBefore();
        this.notAfter = response.getNotAfter();
        this.comment = response.getComment();
    }

    public ResponseCaDTO(GenResponse response, String parentCa, Boolean allowSubCa) {
        this.uuid = response.getUuid();
        this.privkey = response.getPrivkey();
        this.cert = response.getCert();
        this.parentCa = parentCa;
        this.allowSubCa = allowSubCa;
        this.notBefore = response.getNotBefore();
        this.notAfter = response.getNotAfter();
        this.comment = response.getComment();
    }

    public ResponseCaDTO(Ca ca) {
        this.uuid = ca.getUuid();
        this.privkey = ca.getPrivkey();
        this.cert = ca.getCert();
        this.parentCa = ca.getParentCa();
        this.allowSubCa = ca.getAllowSubCa();
        this.notBefore = ca.getNotBefore();
        this.notAfter = ca.getNotAfter();
        this.comment = ca.getComment();
    }

}
