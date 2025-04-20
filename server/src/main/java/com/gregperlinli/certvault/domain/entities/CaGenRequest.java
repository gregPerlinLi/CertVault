package com.gregperlinli.certvault.domain.entities;

import com.gregperlinli.certvault.domain.dto.RequestCertDTO;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * CA Generate Request DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaGenRequestDTO}
 * @date 2025/3/15 11:52
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class CaGenRequest {

    /**
     * Parent CA Key
     */
    private String parentCaPrivkey;

    /**
     * Parent CA
     */
    private String parentCa;

    /**
     * Algorithm
     */
    private String algorithm = "RSA";

    /**
     * Key Size
     */
    private Integer keySize = 2048;

    /**
     * Allow Signing Sub CA
     */
    private Boolean allowSubCa = false;

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
     * Expiry (day)
     */
    private Integer expiry;

    /**
     * Comment
     */
    private String comment;

    public CaGenRequest(RequestCertDTO requestCertDTO, String ownerEmail) {
        this.algorithm = requestCertDTO.getAlgorithm();
        this.keySize = requestCertDTO.getKeySize();
        this.country = requestCertDTO.getCountry();
        this.province = requestCertDTO.getProvince();
        this.city = requestCertDTO.getCity();
        this.organization = requestCertDTO.getOrganization();
        this.organizationalUnit = requestCertDTO.getOrganizationalUnit();
        this.commonName = requestCertDTO.getCommonName();
        this.emailAddress = ownerEmail;
        this.expiry = requestCertDTO.getExpiry();
        this.comment = requestCertDTO.getComment();
    }

    public CaGenRequest(RequestCertDTO requestCertDTO, String parentCaPrivkey, String parentCa, Boolean allowSubCa, String ownerEmail) {
        this.parentCaPrivkey = parentCaPrivkey;
        this.parentCa = parentCa;
        this.algorithm = requestCertDTO.getAlgorithm();
        this.keySize = requestCertDTO.getKeySize();
        this.allowSubCa = allowSubCa;
        this.country = requestCertDTO.getCountry();
        this.province = requestCertDTO.getProvince();
        this.city = requestCertDTO.getCity();
        this.organization = requestCertDTO.getOrganization();
        this.organizationalUnit = requestCertDTO.getOrganizationalUnit();
        this.commonName = requestCertDTO.getCommonName();
        this.emailAddress = ownerEmail;
        this.expiry = requestCertDTO.getExpiry();
        this.comment = requestCertDTO.getComment();
    }

}
