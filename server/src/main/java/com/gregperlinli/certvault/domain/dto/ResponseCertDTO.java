package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.GenResponse;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ResponseCertDTO {

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
     * CA UUID
     */
    private String caUuid;

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
