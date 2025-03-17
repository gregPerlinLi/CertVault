package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.CertificateDetails;
import lombok.*;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Certificate Details DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertificateDetailsDTO}
 * @date 2025/3/17 15:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertificateDetailsDTO {

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

    public CertificateDetailsDTO(CertificateDetails certificateDetails) {
        this.subject = certificateDetails.getSubject();
        this.issuer = certificateDetails.getIssuer();
        this.notBefore = certificateDetails.getNotBefore();
        this.notAfter = certificateDetails.getNotAfter();
        this.serialNumber = certificateDetails.getSerialNumber();
        this.publicKey = certificateDetails.getPublicKey();
        this.extensions = certificateDetails.getExtensions();
    }

}
