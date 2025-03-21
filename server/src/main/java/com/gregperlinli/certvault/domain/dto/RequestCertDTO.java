package com.gregperlinli.certvault.domain.dto;

import com.gregperlinli.certvault.domain.entities.SubjectAltName;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class RequestCertDTO {

    /**
     * CA UUID (Only required when applying for an SSL certificate)
     */
    private String caUuid;

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
     * Expiry (day)
     */
    private Integer expiry;

    /**
     * Subject Alternative Name (Only required when applying for an SSL certificate)
     */
    private List<SubjectAltName> subjectAltNames;

    /**
     * Comment
     */
    private String comment;

}
