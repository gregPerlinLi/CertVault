package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.GenResponse;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ResponseCaDTO {

    /**
     * UUID
     */
    private String uuid;

    /**
     * Private key with BASE64 encoding
     */
    private String privkey;

    /**
     * Certificate with BASE64 encoding
     */
    private String cert;

    /**
     * Parent CA UUID
     */
    private String parentCa;

    /**
     * Whether to allow issue subordinate CA
     */
    private Boolean allowSubCa;

    /**
     * Effective Date
     */
    private LocalDateTime notBefore;

    /**
     * Expiration Date
     */
    private LocalDateTime notAfter;

    /**
     * Comment
     */
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

}
