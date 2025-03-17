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

    public static Ca toCa(GenResponse response, Integer userId, LocalDateTime createdAt, LocalDateTime modifiedAt) throws Exception {
        Ca ca = new Ca();
        ca.setUuid(response.getUuid());
        ca.setPrivkey(EncryptAndDecryptUtils.encrypt(response.getPrivkey()));
        ca.setCert(response.getCert());
        ca.setOwner(userId);
        ca.setComment(response.getComment());
        ca.setAvailable(true);
        ca.setNotBefore(response.getNotBefore());
        ca.setNotAfter(response.getNotAfter());
        ca.setCreatedAt(createdAt);
        ca.setModifiedAt(modifiedAt);
        ca.setDeleted(false);
        return ca;
    }

}
