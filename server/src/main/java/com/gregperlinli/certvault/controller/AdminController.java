package com.gregperlinli.certvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gregperlinli.certvault.annotation.*;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.ICertificateService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AdminController}
 * @date 2025/3/17 17:46
 */
@Tag(name = "Admin", description = "Admin API")
@InsufficientPrivilegesApiResponse
@NoValidSessionApiResponse
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
@RequestMapping("/api/v1/admin")
@CrossOrigin
@RestController
public class AdminController {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICaBindingService caBindingService;

    @Resource
    ICertificateService certificateService;

    /**
     * Get users
     *
     * @param keyword the keyword to search
     * @param page the page number
     * @param limit the limit of the page
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @return the result
     */
    @Operation(
            summary = "Get users",
            description = "Retrieve all users (paged)"
    )
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/users")
    public ResultVO<PageDTO<UserProfileDTO>> getUsers(@Parameter(name = "keyword", description = "Search keywords (Can be username, display name, and email)", example = "user")
                                                          @RequestParam(value = "keyword", required = false) String keyword,
                                                      @Parameter(name = "page", description = "Page number", example = "1")
                                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @Parameter(name = "limit", description = "Page limit", example = "10")
                                                          @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                      @Parameter(name = "orderBy", description = "Order by field", example = "username")
                                                          @RequestParam(value = "orderBy", required = false) String orderBy,
                                                      @Parameter(name = "isAsc", description = "Ascending or descending", example = "true")
                                                          @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc) {
        PageDTO<UserProfileDTO> result = userService.getUsers(keyword, page, limit, isAsc, orderBy);
        if ( result != null && result.getList() != null && !result.getList().isEmpty()) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get all CA information
     *
     * @param page the page number
     * @param limit the limit of the page
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Get all CA information",
            description = "Retrieve all CA information under this username (paged)"
    )
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
        PageDTO<CaInfoDTO> result = caService.getCas(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), page, limit, isAsc, orderBy);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get all user information bound to a ca
     *
     * @param uuid the uuid of the ca
     * @param keyword the keyword to search
     * @param page the page number
     * @param limit the limit of the page
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Get all user information bound to a ca",
            description = "Retrieve all user information bound to a ca (paged)"
    )
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/{uuid}/bind")
    public ResultVO<PageDTO<UserProfileDTO>> getCaBindUsers(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                                @PathVariable("uuid") String uuid,
                                                            @Parameter(name = "keyword", description = "Search keywords (Can be username, display name, and email)", example = "user")
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
        PageDTO<UserProfileDTO> result = caService.getBoundUsers(keyword,
                uuid,
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
     * Get all user information not bound to a ca
     *
     * @param uuid the uuid of the ca
     * @param keyword the keyword to search
     * @param page the page number
     * @param limit the limit of the page
     * @param orderBy the order by field
     * @param isAsc the ascending or descending
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Get all user information not bound to a ca",
            description = "Retrieve all user information not bound to a ca (paged)"
    )
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/{uuid}/bind/not")
    public ResultVO<PageDTO<UserProfileDTO>> getCaNotBoundUsers(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                                    @PathVariable("uuid") String uuid,
                                                                @Parameter(name = "keyword", description = "Search keywords (Can be username, display name, and email)", example = "user")
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
        PageDTO<UserProfileDTO> result = caService.getNotBoundUsers(keyword,
                uuid,
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
     * Get a CA certificate
     *
     * @deprecated Not implemented
     * @param uuid the uuid of the CA
     * @param isChain whether to get the certificate chain
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Get CA Certificate",
            description = "Obtain the CA certificate allocated to the user"
    )
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/{uuid}/cer")
    @Deprecated(since = "0.4.0")
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
     * Get a CA private key
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
     * @throws Exception if the decrypt is failed
     */
    @Operation(
            summary = "Get CA Private Key",
            description = "Obtain the CA private key allocated to the user (Need user password verify)"
    )
    @PasswordValidationFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @PostMapping(value = "/cert/ca/{uuid}/privkey")
    public ResultVO<String> getCaPrivkey(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
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
        String result = caService.getCaPrivKey(uuid,
                confirmPassword.path("password").asText(),
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Update a CA comment
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Update CA Comment",
            description = "Update the comment of the CA allocated to the user"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PatchMapping(value = "/cert/ca/{uuid}/comment")
    public ResultVO<Void> updateCaComment(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                              @PathVariable("uuid") String uuid,
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                  description = "Update comment",
                                                  content = @Content(
                                                          examples = {@ExampleObject(value =
                                                                  """
                                                                  {
                                                                      "comment": "This is a comment"
                                                                  }
                                                                  """
                                                          )}
                                                 )
                                          )
                                              @RequestBody JsonNode updateComment,
                                          HttpServletRequest request) {
        Boolean result = caService.updateCaComment(uuid,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                updateComment.path("comment").asText());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Modify a CA availability
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Modify CA Availability",
            description = "Modify the availability of the CA allocated to the user"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @SuccessApiResponse
    @PatchMapping(value = "/cert/ca/{uuid}/available")
    public ResultVO<Boolean> modifyCaAvailable(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                                   @PathVariable("uuid") String uuid,
                                               HttpServletRequest request) {
        Boolean result = caService.modifyCaAvailability(uuid,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Enabled", true);
        }
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Disabled", false);
    }

    /**
     * Import a CA certificate
     *
     * @param importCertDTO the import certificate DTO
     * @param request the request
     * @return the result
     * @throws Exception if the encryption is failed
     */
    @Operation(
            summary = "Import CA Certificate",
            description = "Import a CA certificate and private key allocated to the user",
            responses = {
                    @ApiResponse(
                            responseCode = "444",
                            description = "Certificate Invalid",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 444,
                                                "msg": "The certificate is invalid.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02+08:00"
                                            }
                                            """
                                    )}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "444",
                            description = "Certificate is not CA",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 444,
                                                "msg": "The certificate is not a CA.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02+08:00"
                                            }
                                            """
                                    )}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.CertResponseResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "msg": "Success",
                                                "data": {
                                                    "uuid": "bf35ecb1-9b67-4083-9476-e264ba153188",
                                                    "algorithm": "RSA",
                                                    "keySize": 2048,
                                                    "privkey": null,
                                                    "cert": "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUV2QUl...",
                                                    "parentCa": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
                                                    "allowSubCa": true,
                                                    "notBefore": "2025-03-23T12:49:45.733",
                                                    "notAfter": "2025-09-19T12:49:45.733",
                                                    "comment": "Cert Vault Default Intermediate Certificate Authority"
                                                },
                                                "timestamp": "2025-03-19T01:38:31+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @FailedApiResponse
    @PostMapping(value = "/cert/ca/import")
    public ResultVO<ResponseCaDTO> importCa(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Import certificate entity")
                                                @RequestBody ImportCertDTO importCertDTO,
                                            HttpServletRequest request) throws Exception {
        ResponseCaDTO result = caService.importCa(importCertDTO,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Request a new CA certificate
     *
     * @param requestCertDTO the request certificate DTO
     * @param request the request
     * @return the result
     * @throws Exception if the encrypt is failed
     */
    @Operation(
            summary = "Request CA Certificate",
            description = "Request a new CA certificate",
            responses = {
                    @ApiResponse(
                            responseCode = "403",
                            description = "Parent CA Does Not Allow Sub CA",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 403,
                                                "msg": "The CA does not allow sub CA.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @SuccessAndFailedApiResponse
    @DoesNotExistApiResponse
    @PostMapping(value = "/cert/ca")
    public ResultVO<ResponseCaDTO> requestCa(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     description = "Request certificate entity",
                                                     content = @Content(
                                                             examples = @ExampleObject(value =
                                                                     """
                                                                     {
                                                                         "caUuid": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
                                                                         "allowSubCa": true,
                                                                         "country": "China",
                                                                         "province": "Guangdong",
                                                                         "city": "Canton",
                                                                         "organization": "CertVault Develop Org",
                                                                         "organizationalUnit": "CertVault Dev",
                                                                         "commonName": "CertVault Intermediate CA",
                                                                         "expiry": 180,
                                                                         "comment": "Cert Vault Default Intermediate Certificate Authority"
                                                                     }
                                                                     """
                                                             )
                                                     )
                                             )
                                                 @RequestBody RequestCertDTO requestCertDTO,
                                             HttpServletRequest request) throws Exception {
        ResponseCaDTO result = caService.requestCa(requestCertDTO,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Renew a CA certificate
     *
     * @param uuid the uuid of the CA
     * @param expiry the expiry of the CA
     * @param request the request
     * @return the result
     * @throws Exception if the decrypt is failed
     */
    @Operation(
            summary = "Renew CA Certificate",
            description = "Renew the specified CA certificate",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Renew CA certificate successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.CertResponseResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "msg": "Success",
                                                "data": {
                                                    "uuid": "bf35ecb1-9b67-4083-9476-e264ba153188",
                                                    "algorithm": "RSA",
                                                    "keySize": 2048,
                                                    "privkey": null,
                                                    "cert": "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUV2QUl...",
                                                    "parentCa": "3885be11-4084-4538-9fa0-70ffe4c4cbe0",
                                                    "allowSubCa": true,
                                                    "notBefore": "2025-03-23T12:49:45.733",
                                                    "notAfter": "2025-09-19T12:49:45.733",
                                                    "comment": "Cert Vault Default Intermediate Certificate Authority"
                                                },
                                                "timestamp": "2025-03-19T01:38:31+08:00"
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
    @PutMapping(value = "/cert/ca/{uuid}")
    public ResultVO<ResponseCaDTO> renewCa(@Parameter(name = "uuid", description = "CA UUID", example = "bf35ecb1-9b67-4083-9476-e264ba153188")
                                               @PathVariable("uuid") String uuid,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                   description = "New expiry",
                                                   content = @Content(
                                                           examples = @ExampleObject(value =
                                                                   """
                                                                   {
                                                                       "expiry": 3650
                                                                   }
                                                                   """
                                                           )
                                                   )
                                           )
                                               @RequestBody JsonNode expiry,
                                           HttpServletRequest request) throws Exception {
        ResponseCaDTO result = caService.renewCa(uuid, expiry.get("expiry").asInt(),
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Delete a CA certificate
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Delete CA Certificate",
            description = "Delete the specified CA certificate"
    )
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @DeleteMapping(value = "/cert/ca/{uuid}")
    public ResultVO<Void> deleteCa(@Parameter(name = "uuid", description = "CA UUID", example = "bf35ecb1-9b67-4083-9476-e264ba153188")
                                       @PathVariable("uuid") String uuid,
                                   HttpServletRequest request) {
        Boolean result = caService.deleteCa(uuid,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Bind a CA certificate to a user
     *
     * @param caBindingDTO the CA binding DTO
     * @return the result
     */
    @Operation(
            summary = "Bind CA Certificate to User",
            description = "Bind a specified CA for user to sign SSL certificates"
    )
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PostMapping(value = "/cert/ca/bind/create")
    public ResultVO<Void> bindCaToUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "CA-User binding entity")
                                           @RequestBody CaBindingDTO caBindingDTO) {
        Boolean result = caBindingService.newBinding(caBindingDTO);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Bind CA certificate to users
     *
     * @param caBindingDTOs the CA binding DTOs
     * @return the result
     * @throws Exception if the decrypt is failed
     */
    @Operation(
            summary = "Bind CA Certificate to Users",
            description = "Batch bind users to specified CA certificate"
    )
    @ParamNotNullApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PostMapping(value = "/cert/ca/binds/create")
    public ResultVO<Void> bindCasToUsers(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of CA-User binding entities")
                                             @RequestBody List<CaBindingDTO> caBindingDTOs) throws Exception {
        Boolean result = caBindingService.newBindings(caBindingDTOs);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Unbind a CA certificate from a user
     *
     * @param caBindingDTO the CA binding DTO
     * @return the result
     */
    @Operation(
            summary = "Unbind CA Certificate from User",
            description = "Unbind a specified CA certificate from a user"
    )
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PostMapping(value = "/cert/ca/bind/delete")
    public ResultVO<Void> unbindCaFromUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "CA-User binding entity")
                                               @RequestBody CaBindingDTO caBindingDTO) {
        Boolean result = caBindingService.deleteBinding(caBindingDTO);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Unbind CA certificate from users
     *
     * @param caBindingDTOs the CA binding DTOs
     * @return the result
     * @throws Exception if the decrypt is failed
     */
    @Operation(
            summary = "Unbind CA Certificate from Users",
            description = "Batch unbind users from specified CA certificate"
    )
    @ParamNotNullApiResponse
    @DoesNotExistApiResponse
    @NullSuccessApiResponse
    @FailedApiResponse
    @PostMapping(value = "/cert/ca/binds/delete")
    public ResultVO<Void> unbindCasFromUsers(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of CA-User binding entities")
                                                 @RequestBody List<CaBindingDTO> caBindingDTOs) throws Exception {
        Boolean result = caBindingService.deleteBindings(caBindingDTOs);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

    /**
     * Count the number of user requested CA certificates
     *
     * @param condition the condition of the ca
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Count Requested CA Certificates",
            description = "Count the number of user requested CA certificates"
    )
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/count")
    public ResultVO<Long> countRequestedCa(@Parameter(name = "condition", description = "Condition of the CA certificate", example = "none")
                                               @RequestParam(value = "condition", defaultValue = "none", required = false) String condition,
                                           HttpServletRequest request) {
        if ( "available".equals(condition) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countCa(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), 1));
        } else if ( "unavailable".equals(condition) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countCa(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), 0));
        } else {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Success",
                    caService.countCa(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), -1));
        }
    }

    /**
     * Count the number of SSL/CA certificates signed by a CA certificate
     *
     * @param uuid the uuid of the CA
     * @param caOrSsl the flag of the CA or SSL
     * @param request the request
     * @return the result
     */
    @Operation(
            summary = "Count Signed CA Certificates",
            description = "Count the number of SSL/CA certificates signed by a CA certificate"
    )
    @SuccessApiResponse
    @GetMapping(value = "/cert/ca/{uuid}/count")
    public ResultVO<Long> countCaSigned(@Parameter(name = "uuid", description = "CA UUID", example = "3885be11-4084-4538-9fa0-70ffe4c4cbe0")
                                            @PathVariable("uuid") String uuid,
                                        @Parameter(name = "caOrSsl", description = "Flag of CA or SSL (true if count ca certificates, false if count ssl certificates)", example = "false")
                                            @RequestParam(value = "caOrSsl", defaultValue = "false", required = false) Boolean caOrSsl,
                                        HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                certificateService.countCaSigned(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), uuid, caOrSsl));
    }

    /**
     * Count all users
     *
     * @param role the role of the user
     * @return count result
     */
    @Operation(
            summary = "Count all users",
            description = "Calculate the total number of users"
    )
    @SuccessApiResponse
    @GetMapping(value = "/users/count")
    public ResultVO<Long> countAllUser(@Parameter(name = "role", description = "Role of the user (0: all user, 1: user, 2: admin, 3: superadmin)", example = "0")
                                       @RequestParam(value = "role", defaultValue = "0", required = false) Integer role) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                userService.countAllUser(role));
    }

}
