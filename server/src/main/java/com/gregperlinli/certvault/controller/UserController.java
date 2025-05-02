package com.gregperlinli.certvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gregperlinli.certvault.annotation.*;
import com.gregperlinli.certvault.certificate.CertAnalyzer;
import com.gregperlinli.certvault.certificate.CertConverter;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code UserController}
 * @date 2025/3/10 20:37
 */
@Tag(name = "User", description = "Common User API")
@NoValidSessionApiResponse
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
@RequestMapping("/api/v1/user")
@CrossOrigin
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

    @Resource
    ILoginRecordService loginRecordService;

    /**
     * Get user profile
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Get User Profile",
            description = "Retrieve user's own personal information"
    )
    @ParamNotNullApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/profile")
    public ResultVO<UserProfileDTO> getProfile(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success",
                userService.getOwnProfile(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

    /**
     * Get user's login records
     *
     * @param status Login status (-1: all, 0: offline, 1:online)
     * @param page Page number
     * @param limit Page limit
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Get User Login Records",
            description = "Retrieve user's own login records (paged retrieval)"
    )
    @DoesNotExistApiResponse
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/session")
    public ResultVO<PageDTO<LoginRecordDTO>> getLoginRecords(@Parameter(name = "status", description = "Login status (-1: all, 0: offline, 1:online)", example = "-1")
                                                                 @RequestParam(value = "status", defaultValue = "-1") Integer status,
                                                             @Parameter(name = "page", description = "Page number", example = "1")
                                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                             @Parameter(name = "limit", description = "Page limit", example = "10")
                                                                 @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                             @Parameter(name = "orderBy", description = "Order by field", example = "username")
                                                                 @RequestParam(value = "orderBy", required = false) String orderBy,
                                                             @Parameter(name = "isAsc", description = "Ascending or descending", example = "true")
                                                                 @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc,
                                                             HttpServletRequest request) {
        PageDTO<LoginRecordDTO> result = loginRecordService.getUserLoginRecords(
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                status,
                request.getSession().getId(),
                page,
                limit,
                isAsc,
                orderBy);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Update user profile by their own
     *
     * @param updateUserProfileDTO {@link UpdateUserProfileDTO} New user profile entity
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Update User Profile",
            description = "Update user's own personal information\nWrite the specific part that needs to be modified, do not include any part that does not need to be changed in the body (including keys and values).",
            responses = {
                    @ApiResponse(
                            responseCode = "422",
                            description = "Password validation failed",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 422,
                                                "msg": "The old password is incorrect.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PatchMapping(value = "/profile")
    public ResultVO<Void> updateProfile(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Update user profile entity")
                                            @RequestBody UpdateUserProfileDTO updateUserProfileDTO,
                                        HttpServletRequest request) {
        if (
                userService.updateUserProfile(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                        updateUserProfileDTO,
                        false)
        ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Force logout user's session
     *
     * @param uuid Login record UUID
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "User Session Logout",
            description = "Force logout user's session"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @DeleteMapping(value = "/session/{uuid}/logout")
    public ResultVO<Void> forceLogoutSession(@Parameter(name = "uuid", description = "Login record UUID", example = "ce9aa242-796e-4baa-9f59-c82a918a9380")
                                                 @PathVariable("uuid") String uuid,
                                             HttpServletRequest request) {
        if ( loginRecordService.sessionForceLogout(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), uuid) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Force logout user themselves
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Force Logout User",
            description = "Force logout user's all sessions"
    )
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @DeleteMapping(value = "/logout")
    public ResultVO<Void> forceLogoutUser(HttpServletRequest request) {
        if ( loginRecordService.userForceLogout(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Get user's CA list
     *
     * @param keyword Search keywords
     * @param page Page number
     * @param limit Page limit
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Get User CA List",
            description = "Retrieve all CA information bound to your own account (paged retrieval)"
    )
    @DoesNotExistApiResponse
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca")
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@Parameter(name = "keyword", description = "Search keywords (Can be UUID, comments)", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                               @Parameter(name = "page", description = "Page number", example = "1")
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @Parameter(name = "limit", description = "Page limit", example = "10")
                                                   @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                               @Parameter(name = "orderBy", description = "Order by field", example = "username")
                                                   @RequestParam(value = "orderBy", required = false) String orderBy,
                                               @Parameter(name = "isAsc", description = "Ascending or descending", example = "true")
                                                   @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc,
                                               HttpServletRequest request) {
        PageDTO<CaInfoDTO> result = caService.getBoundCas(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                page,
                limit,
                isAsc,
                orderBy);
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
    @Operation(
            summary = "Get CA Certificate",
            description = "Obtain the CA certificate allocated to the user"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @NoDataApiResponse
    @GetMapping(value = "/cert/ca/{uuid}/cer")
    public ResultVO<String> getCaCert(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                          @PathVariable("uuid") String uuid,
                                      @Parameter(name = "isChain", description = "Whether to get the certificate chain", example = "true")
                                          @RequestParam(value = "isChain", defaultValue = "false", required = false) Boolean isChain,
                                      @Parameter(name = "needRootCa", description = "Whether to get the root CA certificate in the chain", example = "true")
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
     * @param keyword Search keywords
     * @param page Page number
     * @param limit Page limit
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Get User Certificate List",
            description = "Retrieve all certificate information for the user (paged retrieval)",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Admin with no CA",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 404,
                                                "msg": "The admin user does not have any CA.",
                                                "data": null,
                                                "timestamp": "2025-04-04T09:45:34+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @DoesNotExistApiResponse
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/cert/ssl")
    public ResultVO<PageDTO<CertInfoDTO>> getCerts(@Parameter(name = "keyword", description = "Search keywords (Can be UUID, comments)", example = "72267ce5-e94a-4cdb-b35b-63f1f385b253")
                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                   @Parameter(name = "page", description = "Page number", example = "1")
                                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @Parameter(name = "limit", description = "Page limit", example = "10")
                                                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                   @Parameter(name = "orderBy", description = "Order by field", example = "username")
                                                       @RequestParam(value = "orderBy", required = false) String orderBy,
                                                   @Parameter(name = "isAsc", description = "Ascending or descending", example = "true")
                                                       @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc,
                                                   HttpServletRequest request) {
        PageDTO<CertInfoDTO> result = certificateService.getCertificates(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                page,
                limit,
                isAsc,
                orderBy);
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
    @Operation(
            summary = "Get SSL Certificate",
            description = "Retrieve the SSL certificate allocated to the user"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @NoDataApiResponse
    @GetMapping(value = "/cert/ssl/{uuid}/cer")
    public ResultVO<String> getCertificateCert(@Parameter(name = "uuid", description = "SSL certificate UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                   @PathVariable("uuid") String uuid,
                                               @Parameter(name = "isChain", description = "Whether to get the certificate chain", example = "true")
                                                   @RequestParam(value = "isChain", defaultValue = "false") Boolean isChain,
                                               @Parameter(name = "needRootCa", description = "Whether to get the root CA certificate in the chain", example = "true")
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
    @Operation(
            summary = "Get SSL Certificate Private Key",
            description = "Retrieve the private key of the SSL certificate (Need user password verify)"
    )
    @PasswordValidationFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @NoDataApiResponse
    @PostMapping(value = "/cert/ssl/{uuid}/privkey")
    public ResultVO<String> getCertificatePrivkey(@Parameter(name = "uuid", description = "SSL certificate UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                      @PathVariable("uuid") String uuid,
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                          description = "Confirm password",
                                                          content = @Content(
                                                                  examples = {@ExampleObject(value =
                                                                          """
                                                                          {
                                                                              "password": "123456"
                                                                          }
                                                                          """
                                                                  )}
                                                          )
                                                  )
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
    @Operation(
            summary = "Update Certificate Comment",
            description = "Update the comment of the certificate"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PatchMapping(value = "/cert/ssl/{uuid}/comment")
    public ResultVO<Void> updateCertComment(@Parameter(name = "uuid", description = "SSL certificate UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                @PathVariable("uuid") String uuid,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Update comment",
                                                    content = @Content(
                                                            examples = {@ExampleObject(value =
                                                                    """
                                                                    {
                                                                        "comment": "New comment of the cert"
                                                                    }
                                                                    """
                                                            )}
                                                    )
                                            )
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
    @Operation(
            summary = "Request Certificate",
            description = "Request a new SSL certificate",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.CertResponseResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "message": "Success",
                                                "data": {
                                                    "uuid": "2f2d63a8-b29c-4404-ae10-81f5ff023a69",
                                                    "algorithm": "RSA",
                                                    "keySize": 2048,
                                                    "privkey": "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQU...",
                                                    "cert": "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk...",
                                                    "caUuid": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
                                                    "notBefore": "2025-03-22T23:05:54.773",
                                                    "notAfter": "2035-03-20T23:05:54.773",
                                                    "comment": "CertVault Website SSL Certificate"
                                                }
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @FailedApiResponse
    @PostMapping(value = "/cert/ssl")
    public ResultVO<ResponseCertDTO> requestCert(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request certificate entity")
                                                     @RequestBody RequestCertDTO requestCertDTO,
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
    @Operation(
            summary = "Renew Certificate",
            description = "Renew the SSL certificate",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.CertResponseResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "message": "Success",
                                                "data": {
                                                    "uuid": "2f2d63a8-b29c-4404-ae10-81f5ff023a69",
                                                    "algorithm": "RSA",
                                                    "keySize": 2048,
                                                    "privkey": null,
                                                    "cert": "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk...",
                                                    "caUuid": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
                                                    "notBefore": "2025-03-22T23:05:54.773",
                                                    "notAfter": "2035-03-20T23:05:54.773",
                                                    "comment": "CertVault Website SSL Certificate"
                                                }
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @SuccessAndFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @PutMapping(value = "/cert/ssl/{uuid}")
    public ResultVO<ResponseCertDTO> renewCert(@Parameter(name = "uuid", description = "SSL certificate UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                   @PathVariable("uuid") String oldCertUuid,
                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                       description = "New expiry",
                                                       content = @Content(
                                                               examples = {@ExampleObject(value =
                                                                       """
                                                                       {
                                                                           "expiry": 365
                                                                       }
                                                                       """
                                                               )}
                                                       )
                                               )
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
    @Operation(
            summary = "Delete Certificate",
            description = "Delete the SSL certificate"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @DeleteMapping(value = "/cert/ssl/{uuid}")
    public ResultVO<Void> deleteCert(@Parameter(name = "uuid", description = "SSL certificate UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                         @PathVariable("uuid") String uuid,
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
    @Operation(
            summary = "Analyze Certificate",
            description = "Certificate parser, used for parsing certificate information"
    )
    @SuccessApiResponse
    @PostMapping(value = "/cert/analyze")
    public ResultVO<CertificateDetailsDTO> analyzeCertificate(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                         description = "Certificate Base64",
                                                                         content = @Content(
                                                                                 examples = {@ExampleObject(value =
                                                                                         """
                                                                                         {
                                                                                             "cert": "MIIB+zCCAVigAwIBAgIQJz+JlZg97kbB6ZnzUfe8pYDANBgk..."
                                                                                         }
                                                                                         """
                                                                                 )}
                                                                         )
                                                                 )
                                                                     @RequestBody JsonNode certBase64) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success",
                new CertificateDetailsDTO(CertAnalyzer.analyzeCertificate(certBase64.get("cert").asText())));
    }

    @Operation(
            summary = "Analyze Private Key",
            description = "Private key parser, used for parsing private key information"
    )
    @SuccessApiResponse
    @PostMapping(value = "/cert/privkey/analyze")
    public ResultVO<PrivkeyDetailsDTO> analyzePrivateKey(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                 description = "Private key Base64",
                                                                 content = @Content(
                                                                         examples = {@ExampleObject(value =
                                                                                 """
                                                                                 {
                                                                                     "privkey": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1GAQWvotGCPu1QAB14hzKF5C2bc9WRecF..."
                                                                                 }
                                                                                 """
                                                                         )}
                                                                 )
                                                           )
                                                             @RequestBody JsonNode privkeyBase64) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "success",
                new PrivkeyDetailsDTO(CertAnalyzer.analyzePrivkey(privkeyBase64.get("privkey").asText())));
    }

    /**
     * Convert PEM to PKCS12
     *
     * @param pemCertPrivkeyPassword {@link JsonNode} PEM certificate and private key and password
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @Operation(
            summary = "Convert PEM to PFX(PKCS12)",
            description = "Convert PEM format certificate and private key to PFX(PKCS12) format"
    )
    @ParamNotNullApiResponse
    @SuccessApiResponse
    @PostMapping(value = "/cert/convert/pem/to/pfx")
    public ResultVO<String> convertPemToPfx(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Certificate Base64",
                                                    content = @Content(
                                                            examples = {@ExampleObject(value =
                                                                    """
                                                                    {
                                                                        "cert": "MIIB+zCCAVigAwIBAgIQJz+JlZg97kbB6ZnzUfe8pYDANBgk...",
                                                                        "privkey": "LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQU...",
                                                                        "password": "password"
                                                                    }
                                                                    """
                                                            )}
                                                    )
                                            )
                                                @RequestBody JsonNode pemCertPrivkeyPassword) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                CertConverter.convertFromPemToPfx(
                        pemCertPrivkeyPassword.get("cert").asText(),
                        ( pemCertPrivkeyPassword.get("privkey") != null ) ? pemCertPrivkeyPassword.get("privkey").asText() : null,
                        ( pemCertPrivkeyPassword.get("password") != null) ? pemCertPrivkeyPassword.get("password").asText() : null
                )
        );
    }

    /**
     * Convert PEM to DER
     *
     * @param certPrivkeyDTO {@link CertPrivkeyDTO} Certificate and private key
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @Operation(
            summary = "Convert PEM to DER",
            description = "Convert PEM format certificate and private key to DER format"
    )
    @ParamNotNullApiResponse
    @SuccessApiResponse
    @PostMapping(value = "/cert/convert/pem/to/der")
    public ResultVO<CertPrivkeyDTO> convertPemToDer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Base64 encoded Certificate and Private Key Information")
                                                        @RequestBody CertPrivkeyDTO certPrivkeyDTO) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                new CertPrivkeyDTO(
                        CertConverter.convertFromPemToDer(
                                certPrivkeyDTO.getCert(),
                                certPrivkeyDTO.getPrivkey()
                        )
                )
        );
    }

    /**
     * Convert DER to PEM
     *
     * @param certPrivkeyDTO {@link CertPrivkeyDTO} Certificate and private key
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @Operation(
            summary = "Convert DER to PEM",
            description = "Convert DER format certificate and private key to PEM format"
    )
    @ParamNotNullApiResponse
    @SuccessApiResponse
    @PostMapping(value = "/cert/convert/der/to/pem")
    public ResultVO<CertPrivkeyDTO> convertDerToPem(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Base64 encoded Certificate and Private Key Information")
                                                        @RequestBody CertPrivkeyDTO certPrivkeyDTO) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                new CertPrivkeyDTO(
                        CertConverter.convertFromDerToPem(
                                certPrivkeyDTO.getCert(),
                                certPrivkeyDTO.getPrivkey()
                        )
                )
        );
    }

    /**
     * Count bound CA
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Count Bound CA",
            description = "Count the number of CA bound to the current user"
    )
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/count")
    public ResultVO<Long> countBoundCa(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                caBindingService.countBoundCa(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

    /**
     * Count the number of user requested certificates
     *
     * @param request {@link HttpServletRequest} Request
     * @return {@link ResultVO} Result
     */
    @Operation(
            summary = "Count the number of user requested certificates",
            description = "Count the number of user requested certificates"
    )
    @SuccessApiResponse
    @GetMapping(value = "/cert/ssl/count")
    public ResultVO<Long> countRequestedCertificates(HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                certificateService.countCertificates(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername()));
    }

}
