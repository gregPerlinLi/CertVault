package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.CertificateDetails;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "Certificate Details DTO",
        description = "Certificate details transfer object containing metadata and cryptographic information",
        example = """
        {
            "subject": "C=CN,ST=Guangdong,L=Canton,O=GregPerlinLi,OU=CertVault,CN=a.b.c,E=test@example.com",
            "issuer": "C=CN,ST=Guangdong,L=Canton,O=GregPerlinLi,OU=CertVault,CN=CertVault,E=test@example.com",
            "notBefore": "2025-03-16T17:47:31+08:00",
            "notAfter": "2025-04-15T17:47:31+08:00",
            "serialNumber": "270437134803127796822084897079779361349",
            "publicKey": {
                "modulus": "27228785212927428737771860203521436191416421175100205454382431265594855967985632309468958148378247733975427437215287450921497030089159042637171587909338104950951468346622011497774866599914499772913232124067048957473507324932111466579075770019006239562097950887578380380351965073168545804309746900931657224661678530372714410855922734561728488370891868763766725189024112843185736499314827869994102612012859480648578770392684920448854880316939200542419448930131201774453139638113733093803960821686343047917906589960134274152756270862641854968301124244102646389827505217344218437902984794140086188049888289041283304310827",
                "publicExponent": "65537",
                "encoded": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA17GK+JlZg97kbB6ZnzUfe8pYBO5qScuo/zKGXe6RCBxcps6GrTsagc9BPCpCpfs0GirzAevxHqrK4doC0B3ZHePmXVinZp7f+6oNL93+1FFqkkT+JcSJQpnxR6dluhPfFtJbNErn1QNKdlYq+kwp/tmBWKOzYM0EdTpYd8qit6lXr9fHWU5C+qHIPxIdurOZBi+tRrBYF5bRusIM3C7bVgX1Lm8Dqvvbeelsdz18sgAgCFo41NiJWW1thqo3MhdLEfINhaOhkftYFuu12ajfAmMJgguJ8ADCCpGR22nlxtIsMvKXpzd/PCMhm83W1MWWRZr2RSAS0oYGD5AndEbIKwIDAQAB",
                "format": "X.509",
                "algorithm": "RSA",
                "params": null
            },
            "extensions": {
                "2.5.29.17": "SAN: DNS: a.b.c, IP: 10.18.0.1, Email: test@example.com, URI: https://a.b.c",
                "2.5.29.15": "KeyUsage: Digital Signature, Key Encipherment",
                "2.5.29.37": "EKU: org.bouncycastle.asn1.x509.ExtendedKeyUsage@7a9e2c00"
            }
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CertificateDetailsDTO {

    /**
     * Subject
     */
    @Schema(
            name = "subject",
            description = "Subject of the certificate",
            example = "C=CN,ST=Guangdong,L=Canton,O=GregPerlinLi,OU=CertVault,CN=a.b.c,E=test@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String subject;

    /**
     * Issuer
     */
    @Schema(
            name = "issuer",
            description = "Issuer of the certificate",
            example = "C=CN,ST=Guangdong,L=Canton,O=GregPerlinLi,OU=CertVault,CN=CertVault,E=test@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String issuer;

    /**
     * Not Before
     */
    @Schema(
            name = "notBefore",
            description = "Not Before of the certificate",
            example = "2025-03-16T17:47:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notBefore;

    /**
     * Not After
     */
    @Schema(
            name = "notAfter",
            description = "Not After of the certificate",
            example = "2025-04-15T17:47:31+08:00",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "OffsetDateTime"
    )
    private LocalDateTime notAfter;

    /**
     * Serial Number
     */
    @Schema(
            name = "serialNumber",
            description = "Serial Number of the certificate",
            example = "270437134803127796822084897079779361349",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String serialNumber;

    /**
     * Public Key
     */
    @Schema(
            name = "publicKey",
            description = "Public Key of the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "PublicKey"
    )
    private PublicKey publicKey;

    /**
     * Extensions
     */
    @Schema(
            name = "extensions",
            description = "Extensions of the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Map<String, String>"
    )
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
