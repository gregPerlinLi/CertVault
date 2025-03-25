package com.gregperlinli.certvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gregperlinli.certvault.certificate.CertAnalyzer;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
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
@RequestMapping("/api/v1/user")
@RestController
public class UserController {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICaBindingService caBindingService;

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
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success",
                userService.getOwnProfile(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

    /**
     * Update user profile by their own
     *
     * @param updateUserProfileDTO {@link UpdateUserProfileDTO} New user profile entity
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @PatchMapping(value = "/profile")
    public ResultVO<Void> updateProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO,
                                        HttpServletRequest request) {
        if (
                userService.updateUserProfile(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                        updateUserProfileDTO,
                        false)
        ) {
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
    @GetMapping(value = "/cert/ca")
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@RequestParam(value = "keyword", required = false) String keyword,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                               HttpServletRequest request) {
        PageDTO<CaInfoDTO> result = caService.getBoundCas(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                page,
                limit);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get CA certificate
     *
     * @param uuid CA uuid
     * @param isChain whether to get the certificate chain
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ca/{uuid}/cer")
    public ResultVO<String> getCaCert(@PathVariable("uuid") String uuid,
                                      @RequestParam(value = "isChain", defaultValue = "false", required = false) Boolean isChain,
                                      @RequestParam(value = "needRootCa", defaultValue = "true", required = false) Boolean needRootCa,
                                      HttpServletRequest request) {
        String result = null;
        if ( isChain ) {
            result = caService.getCaCertChain(uuid,
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), needRootCa);
        } else {
            result = caService.getCaCert(uuid,
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        }
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
    @GetMapping(value = "/cert/ssl")
    public ResultVO<PageDTO<CertInfoDTO>> getCerts(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                   HttpServletRequest request) {
        PageDTO<CertInfoDTO> result = certificateService.getCertificates(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                page,
                limit);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get SSL certificate
     *
     * @param uuid Certificate uuid
     * @param isChain whether to get the certificate chain
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ssl/{uuid}/cer")
    public ResultVO<String> getCertificateCert(@PathVariable("uuid") String uuid,
                                               @RequestParam(value = "isChain", defaultValue = "false", required = false) Boolean isChain,
                                               @RequestParam(value = "needRootCa", defaultValue = "true", required = false) Boolean needRootCa,
                                               HttpServletRequest request) {
        String result = null;
        if ( isChain ) {
            result = certificateService.getCertificateCertChain(uuid,
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), needRootCa);
        } else {
            result = certificateService.getCertificateCert(uuid,
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        }
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Get SSL certificate private key
     *
     * @param uuid Certificate uuid
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @PostMapping(value = "/cert/ssl/{uuid}/privkey")
    public ResultVO<String> getCertificatePrivkey(@PathVariable("uuid") String uuid,
                                                  @RequestBody JsonNode confirmPassword,
                                                  HttpServletRequest request) throws Exception {
        String result = certificateService.getCertificatePrivkey(uuid,
                confirmPassword.path("password").asText(),
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Update certificate comment
     *
     * @param uuid Certificate uuid
     * @param updateComment Update comment entity
     * @param request       {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @PatchMapping(value = "/cert/ssl/{uuid}/comment")
    public ResultVO<Void> updateCertComment(@PathVariable("uuid") String uuid,
                                            @RequestBody JsonNode updateComment,
                                            HttpServletRequest request) {
        Boolean result = certificateService.updateCertComment(uuid,
                updateComment.get("comment").asText(), ((UserProfileDTO)
                        request.getSession().getAttribute("account")).getUsername());
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
    @PostMapping(value = "/cert/ssl")
    public ResultVO<ResponseCertDTO> requestCert(@RequestBody RequestCertDTO requestCertDTO,
                                                 HttpServletRequest request) throws Exception {
        ResponseCertDTO result = certificateService.requestCert(requestCertDTO,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @PutMapping(value = "/cert/ssl/{uuid}")
    public ResultVO<ResponseCertDTO> renewCert(@PathVariable("uuid") String oldCertUuid,
                                               @RequestBody JsonNode expiry,
                                               HttpServletRequest request) throws Exception {
        ResponseCertDTO result = certificateService.renewCert(oldCertUuid,
                expiry.get("expiry").asInt(),
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @DeleteMapping(value = "/cert/ssl/{uuid}")
    public ResultVO<Void> deleteCert(@PathVariable("uuid") String uuid,
                                     HttpServletRequest request) {
        Boolean result = certificateService.deleteCert(uuid,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @PostMapping(value = "/cert/analyze")
    public ResultVO<CertificateDetailsDTO> getCertificateDetails(@RequestBody JsonNode certBase64) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success",
                new CertificateDetailsDTO(CertAnalyzer.analyzeCertificate(certBase64.get("cert").asText())));
    }

    /**
     * Count bound CA
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ca/count")
    public ResultVO<Long> countBoundCa(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                caBindingService.countBoundCa(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

    /**
     * Count Count the number of user requested certificates
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @GetMapping(value = "/cert/ssl/count")
    public ResultVO<Long> countRequestedCertificates(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                certificateService.countCertificates(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

}
