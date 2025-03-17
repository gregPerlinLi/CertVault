package com.gregperlinli.certvault.domain.entities;

import lombok.*;
import lombok.experimental.Accessors;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Certificate Details
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertificateDetails}
 * @date 2025/3/15 16:02
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class CertificateDetails {

    /**
     * Subject
     */
    private String subject;

    /**
     * Issuer
     */
    private String issuer;

    /**
     * Not Before
     */
    private LocalDateTime notBefore;

    /**
     * Not After
     */
    private LocalDateTime notAfter;

    /**
     * Serial Number
     */
    private String serialNumber;

    /**
     * Public Key
     */
    private PublicKey publicKey;

    /**
     * Extensions
     */
    private Map<String, String> extensions;


}
