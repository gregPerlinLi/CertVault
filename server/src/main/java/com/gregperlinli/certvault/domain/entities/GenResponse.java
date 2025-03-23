package com.gregperlinli.certvault.domain.entities;

import com.gregperlinli.certvault.utils.EncryptAndDecryptUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Generic Certificate Generate Response DTO
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code CaGenResponseDTO}
 * @date 2025/3/15 11:52
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class GenResponse {

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

    public Ca toCa(Integer userId, LocalDateTime createdAt, LocalDateTime modifiedAt) throws Exception {
        Ca ca = new Ca();
        ca.setId(null);
        ca.setUuid(this.getUuid());
        ca.setPrivkey(EncryptAndDecryptUtils.encrypt(this.getPrivkey()));
        ca.setCert(this.getCert());
        ca.setOwner(userId);
        ca.setComment(this.getComment());
        ca.setAvailable(true);
        ca.setNotBefore(this.getNotBefore());
        ca.setNotAfter(this.getNotAfter());
        ca.setCreatedAt(createdAt);
        ca.setModifiedAt(modifiedAt);
        ca.setDeleted(false);
        return ca;
    }

    public Ca toIntCa(String parentCa, Boolean allowSubCa, Integer userId, LocalDateTime createdAt, LocalDateTime modifiedAt) throws Exception {
        Ca ca = new Ca();
        ca.setId(null);
        ca.setUuid(this.getUuid());
        ca.setPrivkey(EncryptAndDecryptUtils.encrypt(this.getPrivkey()));
        ca.setCert(this.getCert());
        ca.setParentCa(parentCa);
        ca.setAllowSubCa(allowSubCa);
        ca.setOwner(userId);
        ca.setComment(this.getComment());
        ca.setAvailable(true);
        ca.setNotBefore(this.getNotBefore());
        ca.setNotAfter(this.getNotAfter());
        ca.setCreatedAt(createdAt);
        ca.setModifiedAt(modifiedAt);
        ca.setDeleted(false);
        return ca;
    }

    public Certificate toCert(String caUuid, Integer userId, LocalDateTime createdAt, LocalDateTime modifiedAt) throws Exception {
        Certificate certificate = new Certificate();
        certificate.setId(null);
        certificate.setUuid(this.getUuid());
        certificate.setPrivkey(EncryptAndDecryptUtils.encrypt(this.getPrivkey()));
        certificate.setCert(this.getCert());
        certificate.setCaUuid(caUuid);
        certificate.setOwner(userId);
        certificate.setComment(this.getComment());
        certificate.setNotBefore(this.getNotBefore());
        certificate.setNotAfter(this.getNotAfter());
        certificate.setCreatedAt(createdAt);
        certificate.setModifiedAt(modifiedAt);
        certificate.setDeleted(false);
        return certificate;
    }

}
