package com.gregperlinli.certvault.domain.entities;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * Certificate Renewal Request
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertRenewRequest}
 * @date 2025/3/15 15:25
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class CertRenewRequest {

    /**
     * CA Private Key with BASE64 encoding
     */
    private String caKey;

    /**
     * CA Certificate with BASE64 encoding
     */
    private String ca;

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

}
