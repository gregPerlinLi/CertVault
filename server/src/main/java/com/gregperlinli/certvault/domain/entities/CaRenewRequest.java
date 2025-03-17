package com.gregperlinli.certvault.domain.entities;

import com.gregperlinli.certvault.utils.EncryptAndDecryptUtils;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * CA Renewal Request DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaRenewRequestDTO}
 * @date 2025/3/15 13:11
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CaRenewRequest {

    /**
     * The UUID of the old certificate
     */
    private String uuid;

    /**
     * The old private key
     */
    private String oldPrivkey;

    /**
     * The old certificate
     */
    private String oldCert;

    /**
     * The new expiration time
     */
    private Integer newExpiry;

    /**
     * The comment
     */
    private String comment;

    public CaRenewRequest(Ca ca, Integer newExpiry) throws Exception {
        this.uuid = ca.getUuid();
        this.oldPrivkey = EncryptAndDecryptUtils.decrypt(ca.getPrivkey());
        this.oldCert = ca.getCert();
        this.newExpiry = newExpiry;
        this.comment = ca.getComment();
    }

}
