package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.SubjectAltName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Request CA Certificate DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code RequestCaDTO}
 * @date 2025/3/17 16:49
 */
@Schema(
        name = "Request Certificate DTO",
        description = "Data transfer object for requesting SSL certificates or Certificate Authority (CA)",
        example = """
        {
            "caUuid": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            "country": "CN",
            "province": "Guangdong",
            "city": "Canton",
            "organization": "GregPerlinLi",
            "organizationalUnit": "Cert Vault",
            "commonName": "a.b.c",
            "expiry": 30,
            "comment": "CertVault SSL Certificate",
            "subjectAltNames": [
                {
                    "type": "DNS_NAME",
                    "value": "a.b.c"
                },
                {
                    "type": "IP_ADDRESS",
                    "value": "10.18.0.1"
                },
                {
                    "type": "URI",
                    "value": "https://a.b.c"
                }
            ]
        }
        """
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RequestCertDTO {

    /**
     * CA UUID (Only required when applying for an SSL certificate or Intermediate CA)
     */
    @Schema(
            name = "CA UUID",
            description = "CA UUID",
            example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
            type = "UUID"
    )
    private String caUuid;

    /**
     * Whether to allow sub-CA (Only required when applying for an Intermediate CA)
     */
    @Schema(
            name = "Allow Sub-CA",
            description = "Whether to allow sub-CA",
            example = "true",
            type = "Boolean"
    )
    private Boolean allowSubCa;

    /**
     * Country
     */
    @Schema(
            name = "Country",
            description = "Country",
            example = "CN",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String country;

    /**
     * Province
     */
    @Schema(
            name = "Province",
            description = "Province",
            example = "Guangdong",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String province;

    /**
     * City
     */
    @Schema(
            name = "City",
            description = "City",
            example = "Canton",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String city;

    /**
     * Organization
     */
    @Schema(
            name = "Organization",
            description = "Organization",
            example = "GregPerlinLi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String organization;

    /**
     * Organizational Unit
     */
    @Schema(
            name = "Organizational Unit",
            description = "Organizational Unit",
            example = "Cert Vault",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String organizationalUnit;

    /**
     * Common Name
     */
    @Schema(
            name = "Common Name",
            description = "Common Name",
            example = "a.b.c",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "String"
    )
    private String commonName;

    /**
     * Expiry (day)
     */
    @Schema(
            name = "Expiry",
            description = "Expiry (day)",
            example = "30",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "Integer"
    )
    private Integer expiry;

    /**
     * Subject Alternative Name (Only required when applying for an SSL certificate)
     */
    @Schema(
            name = "Subject Alternative Name",
            description = "Subject Alternative Name",
            example = """
            [
                {
                    "type": "DNS_NAME",
                    "value": "a.b.c"
                },
                {
                    "type": "IP_ADDRESS",
                    "value": "10.18.0.1"
                },
                {
                    "type": "URI",
                    "value": "https://a.b.c"
                }
            ]
            """
    )
    private List<SubjectAltName> subjectAltNames;

    /**
     * Comment
     */
    @Schema(
            name = "Comment",
            description = "Comment",
            example = "CertVault SSL Certificate",
            type = "String"
    )
    private String comment;

}
