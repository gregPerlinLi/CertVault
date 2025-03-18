package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.Certificate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * SSL Certificate Service
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
public interface ICertificateService extends IService<Certificate> {

    /**
     * Get own SSL Certificates
     *
     * @param owner  Owner of the Certificate
     * @param page   Page number
     * @param limit  Number of certificates per page
     * @return {@link PageDTO<CertificateDetailsDTO>}
     */
    PageDTO<CertInfoDTO> getCertificates(String owner, Integer page, Integer limit);

    /**
     * Get SSL Certificate
     *
     * @param uuid   Certificate UUID
     * @param owner  Owner of the Certificate
     * @return {@link String}
     */
    String getCertificateCert(String uuid, String owner);

    /**
     * Get SSL Certificate Private Key
     *
     * @param requestPrivkeyDTO Certificate Private Key Request DTO
     * @param owner             Owner of the Certificate
     * @return {@link String}
     * @throws Exception Decryption error
     */
    String getCertificatePrivkey(RequestPrivkeyDTO requestPrivkeyDTO, String owner) throws Exception;

    /**
     * Update SSL Certificate Comment
     *
     * @param updateCommentDTO  Certificate Comment DTO
     * @param owner             Owner of the Certificate
     * @return {@link Boolean}
     */
    Boolean updateCertComment(UpdateCommentDTO updateCommentDTO, String owner);

    /**
     * Request a SSL Certificate
     *
     * @param requestCertDTO Certificate Request DTO
     * @param owner          Owner of the Certificate
     * @return {@link ResponseCertDTO}
     * @throws Exception
     */
    ResponseCertDTO requestCert(RequestCertDTO requestCertDTO, String owner) throws Exception;

    /**
     * Renew a SSL Certificate
     *
     * @param oldCertUuid Certificate UUID
     * @param expiry      Expiration time
     * @param owner       Owner of the Certificate
     * @return {@link ResponseCertDTO}
     * @throws Exception e exception
     */
    ResponseCertDTO renewCert(String oldCertUuid, Integer expiry, String owner) throws Exception;

    /**
     * Delete a SSL Certificate
     *
     * @param uuid  Certificate UUID
     * @param owner Owner of the Certificate
     * @return {@link Boolean}
     */
    Boolean deleteCert(String uuid, String owner);

}
