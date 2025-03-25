package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.entities.Ca;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * CA列表 服务类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
public interface ICaService extends IService<Ca> {

    /**
     * Get ca certificates
     *
     * @param keyword search keyword
     * @param owner owner of the ca certificates
     * @param page page number
     * @param limit page size
     * @return ca certificates info
     */
    PageDTO<CaInfoDTO> getCas(String keyword, String owner, Integer page, Integer limit);

    /**
     * Get all CA bindings of a user
     *
     * @param keyword search keyword
     * @param username username
     * @param page     page number
     * @param limit    page size
     * @return CA binding information
     */
    PageDTO<CaInfoDTO> getBoundCas(String keyword, String username, Integer page, Integer limit);

    /**
     * Get ca certificate
     *
     * @param uuid ca certificate uuid
     * @param owner owner of the ca certificate
     * @return ca certificate
     */
    String getCaCert(String uuid, String owner);

    /**
     * Get ca certificate chain
     *
     * @param uuid ca certificate uuid
     * @param owner owner of the ca certificate
     * @return ca certificate chain
     */
    String getCaCertChain(String uuid, String owner);

    /**
     * Get ca private key
     *
     * @param uuid              ca certificate uuid
     * @param confirmPassword   confirm password
     * @param owner             owner of the ca private key
     * @return ca private key
     * @throws Exception decryption error
     */
    String getCaPrivKey(String uuid, String confirmPassword, String owner) throws Exception;

    /**
     * Update ca comment
     *
     * @param uuid ca certificate uuid
     * @param owner owner of the ca certificate
     * @param comment new comment
     * @return true if update successfully
     */
    Boolean updateCaComment(String uuid, String owner, String comment);

    /**
     * Modify ca availability
     *
     * @param uuid ca certificate uuid
     * @param owner owner of the ca certificate
     * @return the new ca availability
     */
    Boolean modifyCaAvailability(String uuid, String owner);

    /**
     * Import an existing ca certificate
     *
     * @param importCertDTO ca certificate info
     * @param owner        owner of the new ca certificate
     * @return new ca certificate info
     * @throws Exception encryption error
     */
    ResponseCaDTO importCa(ImportCertDTO importCertDTO, String owner) throws Exception;

    /**
     * Request new ca certificate
     *
     * @param requestCertDTO new ca certificate info
     * @param owner        owner of the new ca certificate
     * @return new ca certificate info
     * @throws Exception encryption error
     */
    ResponseCaDTO requestCa(RequestCertDTO requestCertDTO, String owner) throws Exception;

    /**
     * Renew an existing ca certificate
     *
     * @param oldCaUuid old ca certificate uuid
     * @param expiry new expiry date
     * @param owner owner of the new ca certificate
     * @return renewed ca certificate info
     * @throws Exception decryption error
     */
    ResponseCaDTO renewCa(String oldCaUuid, Integer expiry, String owner) throws Exception;

    /**
     * Delete an existing ca certificate
     *
     * @param uuid ca certificate uuid
     * @param owner owner of the ca certificate
     * @return true if delete successfully
     */
    Boolean deleteCa(String uuid, String owner);

    /**
     * Count ca certificates
     *
     * @param owner owner of the ca certificates
     * @param status ca certificates status: 0 - unavailable 1 - available -1 - not query
     * @return ca certificates count
     */
    Long countCa(String owner, Integer status);

    /**
     * Count all ca certificates
     *
     * @param status ca certificates status: 0 - unavailable 1 - available -1 - not query
     * @return ca certificates count
     */
    Long countAllCa(Integer status);

}
