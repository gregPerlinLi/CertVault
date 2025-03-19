package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.certificate.CertDecoder;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.ICertificateService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UserController}
 * @date 2025/3/10 20:37
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICertificateService certificateService;

    /**
     * Get user profile
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/profile")
    public ResultVO<UserProfileDTO> getProfile(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success", userService.getOwnProfile(request.getSession().getAttribute("username").toString()));
    }

    /**
     * Update user profile by their own
     *
     * @param updateUserProfileDTO {@link UpdateUserProfileDTO} New user profile entity
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @PutMapping(value = "/profile")
    public ResultVO<Void> updateProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO,
                                        HttpServletRequest request) {
        if ( userService.updateUserProfile(request.getSession().getAttribute("username").toString(), updateUserProfileDTO, false) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

    /**
     * Get user's CA list
     *
     * @param page Page number
     * @param limit Page limit
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ca/{page}/{limit}")
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@PathVariable("page") Integer page,
                                 @PathVariable("limit") Integer limit,
                                 HttpServletRequest request) {
        PageDTO<CaInfoDTO> result = caService.getBoundCas(request.getSession().getAttribute("username").toString(), page, limit);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get CA certificate
     *
     * @param uuid CA uuid
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ca/cer/{uuid}")
    public ResultVO<String> getCaCert(@PathVariable("uuid") String uuid,
                                      HttpServletRequest request) {
        String result = caService.getCaCert(uuid, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Get user's certificate list
     *
     * @param page Page number
     * @param limit Page limit
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/cert/{page}/{limit}")
    public ResultVO<PageDTO<CertInfoDTO>> getCerts(@PathVariable("page") Integer page,
                                                  @PathVariable("limit") Integer limit,
                                                  HttpServletRequest request) {
        PageDTO<CertInfoDTO> result = certificateService.getCertificates(request.getSession().getAttribute("username").toString(), page, limit);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get SSL certificate
     *
     * @param uuid Certificate uuid
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/cert/cer/{uuid}")
    public ResultVO<String> getCertificateCert(@PathVariable("uuid") String uuid,
                                              HttpServletRequest request) {
        String result = certificateService.getCertificateCert(uuid, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Get SSL certificate private key
     *
     * @param requestPrivkeyDTO {@link RequestPrivkeyDTO} Request private key entity
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @PostMapping(value = "/cert/cert/privkey")
    public ResultVO<String> getCertificatePrivkey(@RequestBody RequestPrivkeyDTO requestPrivkeyDTO,
                                                 HttpServletRequest request) throws Exception {
        String result = certificateService.getCertificatePrivkey(requestPrivkeyDTO, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    @PatchMapping(value = "/cert/cert/comment")
    public ResultVO<Void> updateCertComment(@RequestBody UpdateCommentDTO updateCommentDTO,
                                             HttpServletRequest request) {
        Boolean result = certificateService.updateCertComment(updateCommentDTO, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Request certificate
     *
     * @param requestCertDTO {@link RequestCertDTO} Request certificate entity
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @PostMapping(value = "/cert/cert")
    public ResultVO<ResponseCertDTO> requestCert(@RequestBody RequestCertDTO requestCertDTO,
                                                 HttpServletRequest request) throws Exception {
        ResponseCertDTO result = certificateService.requestCert(requestCertDTO, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Renew certificate
     *
     * @param oldCertUuid Old certificate uuid
     * @param expiry New expiry
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @PutMapping(value = "/cert/cert/{uuid}/{expiry}")
    public ResultVO<ResponseCertDTO> renewCert(@PathVariable("uuid") String oldCertUuid,
                                               @PathVariable("expiry") Integer expiry,
                                               HttpServletRequest request) throws Exception {
        ResponseCertDTO result = certificateService.renewCert(oldCertUuid, expiry, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Delete certificate
     *
     * @param uuid Certificate uuid
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @DeleteMapping(value = "/cert/cert/{uuid}")
    public ResultVO<Void> deleteCert(@PathVariable("uuid") String uuid,
                                     HttpServletRequest request) {
        Boolean result = certificateService.deleteCert(uuid, request.getSession().getAttribute("username").toString());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Get certificate details
     *
     * @param certBase64 Certificate Base64
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @GetMapping(value = "/cert/analyze/{cert}")
    public ResultVO<CertificateDetailsDTO> getCertificateDetails(@PathVariable("cert") String certBase64) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success", new CertificateDetailsDTO(CertDecoder.decodeCertificate(certBase64)));
    }

}
