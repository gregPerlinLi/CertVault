package com.gregperlinli.certvault.domain.entities;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Certificate Generate Request DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CertGenRequestDTO}
 * @date 2025/3/15 13:47
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class CertGenRequest {

    /**
     * CA Private Key with BASE64 encoding
     */
    private String caKey;

    /**
     * CA Certificate with BASE64 encoding
     */
    private String ca;

    /**
     * Country
     */
    private String country;

    /**
     * Province
     */
    private String province;

    /**
     * City
     */
    private String city;

    /**
     * Organization
     */
    private String organization;

    /**
     * Organizational Unit
     */
    private String organizationalUnit;

    /**
     * Common Name
     */
    private String commonName;

    /**
     * Email Address
     */
    private String emailAddress;

    /**
     * Subject Alternative Names
     */
    private List<SubjectAltName> subjectAltNames;

    /**
     * Expiry (day)
     */
    private Integer expiry;

    /**
     * Comment
     */
    private String comment;

}
