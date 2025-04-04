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
@RequestMapping("/api/v1/admin")
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
     * @return the result
     */
    @Operation(
            summary = "Get users",
            description = "Retrieve all users (paged)"
    )
    @NoDataListApiResponse
    @SuccessApiResponse
    @GetMapping(value = "/users")
    public ResultVO<PageDTO<UserProfileDTO>> getUsers(@Parameter(name = "keyword", description = "Search keywords (Can be username, display name, and email)")
                                                          @RequestParam(value = "keyword", required = false) String keyword,
                                                      @Parameter(name = "page", description = "Page number")
                                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @Parameter(name = "limit", description = "Page limit")
                                                          @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        PageDTO<UserProfileDTO> result = userService.getUsers(keyword, page, limit);
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
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@Parameter(name = "keyword", description = "Search keywords (Can be UUID, comments)")
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                               @Parameter(name = "page", description = "Page number")
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @Parameter(name = "limit", description = "Page limit")
                                                   @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                               HttpServletRequest request) {
        PageDTO<CaInfoDTO> result = caService.getCas(keyword,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), page, limit);
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
    public ResultVO<PageDTO<UserProfileDTO>> getCaBindUsers(@Parameter(name = "uuid", description = "CA UUID")
                                                                @PathVariable("uuid") String uuid,
                                                            @Parameter(name = "keyword", description = "Search keywords (Can be username, display name, and email)")
                                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                            @Parameter(name = "page", description = "Page number")
                                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @Parameter(name = "limit", description = "Page limit")
                                                                @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                            HttpServletRequest request) {
        PageDTO<UserProfileDTO> result = caService.getBoundUsers(keyword,
                uuid,
                ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(),
                page,
                limit);
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
    public ResultVO<String> getCaCert(@Parameter(name = "uuid", description = "CA UUID")
                                          @PathVariable("uuid") String uuid,
                                      @Parameter(name = "isChain", description = "Whether to get the certificate chain")
                                          @RequestParam(value = "isChain", defaultValue = "false", required = false) Boolean isChain,
                                      @Parameter(name = "needRootCa", description = "Whether to get the root CA certificate in the chain")
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
    public ResultVO<String> getCaPrivkey(@Parameter(name = "uuid", description = "CA UUID")
                                             @PathVariable("uuid") String uuid,
                                         @Parameter(name = "confirmPassword", description = "Confirm password", schema = @Schema(
                                                 type = "application/json", example = "{\"password\": \"123456\"}"
                                         ))
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
    @SuccessAndFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @PatchMapping(value = "/cert/ca/{uuid}/comment")
    public ResultVO<Void> updateCaComment(@Parameter(name = "uuid", description = "CA UUID")
                                              @PathVariable("uuid") String uuid,
                                          @Parameter(name = "updateComment", description = "Update comment entity", schema = @Schema(
                                                  type = "application/json", example = "{\"comment\": \"new comment of the ca\"}"
                                          ))
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
    public ResultVO<Boolean> modifyCaAvailable(@PathVariable("uuid") String uuid,
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
                                    examples = {@ExampleObject(value = """
                                            {
                                                "code": 444,
                                                "msg": "The certificate is invalid.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02.5641+08:00"
                                            }
                                            """
                                    )}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "444",
                            description = "Certificate is not CA",
                            content = @Content(
                                    examples = {@ExampleObject(value = """
                                            {
                                                "code": 444,
                                                "msg": "The certificate is not a CA.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02.5641+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @SuccessAndFailedApiResponse
    @PostMapping(value = "/cert/ca/import")
    public ResultVO<ResponseCaDTO> importCa(@RequestBody ImportCertDTO importCertDTO,
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
                                    examples = {@ExampleObject(value = """
                                            {
                                                "code": 403,
                                                "msg": "The CA does not allow sub CA.",
                                                "data": null,
                                                "timestamp": "2025-04-04T16:16:02.5641+08:00"
                                            }
                                            """)}
                            )
                    )
            }
    )
    @SuccessAndFailedApiResponse
    @DoesNotExistApiResponse
    @PostMapping(value = "/cert/ca")
    public ResultVO<ResponseCaDTO> requestCa(@Parameter(name = "RequestCertDTO", description = "Request certificate entity")
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
            description = "Renew the specified CA certificate"
    )
    @SuccessAndFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @PutMapping(value = "/cert/ca/{uuid}")
    public ResultVO<ResponseCaDTO> renewCa(@Parameter(name = "uuid", description = "CA UUID")
                                               @PathVariable("uuid") String uuid,
                                           @Parameter(name = "expiry", description = "New expiry", schema = @Schema(
                                                   type = "application/json", example = "{\"expiry\": 3650}"
                                           ))
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
    @SuccessAndFailedApiResponse
    @NotYourResourceApiResponse
    @DoesNotExistApiResponse
    @DeleteMapping(value = "/cert/ca/{uuid}")
    public ResultVO<Void> deleteCa(@Parameter(name = "uuid", description = "CA UUID")
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
    @SuccessAndFailedApiResponse
    @DoesNotExistApiResponse
    @PostMapping(value = "/cert/ca/bind")
    public ResultVO<Void> bindCaToUser(@Parameter(name = "CaBindingDTO", description = "CA binding entity")
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
    @SuccessAndFailedApiResponse
    @ParamNotNullApiResponse
    @DoesNotExistApiResponse
    @PostMapping(value = "/cert/ca/binds")
    public ResultVO<Void> bindCasToUsers(@Parameter(name = "CaBindingDTOs", description = "CA binding entities list")
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
    @SuccessAndFailedApiResponse
    @DoesNotExistApiResponse
    @DeleteMapping(value = "/cert/ca/bind")
    public ResultVO<Void> unbindCaFromUser(@Parameter(name = "CaBindingDTO", description = "CA binding entity")
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
    @SuccessAndFailedApiResponse
    @ParamNotNullApiResponse
    @DoesNotExistApiResponse
    @DeleteMapping(value = "/cert/ca/binds")
    public ResultVO<Void> unbindCasFromUsers(@Parameter(name = "CaBindingDTOs", description = "CA binding entities list")
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
    public ResultVO<Long> countRequestedCa(@Parameter(name = "condition", description = "Condition of the CA certificate")
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
    public ResultVO<Long> countCaSigned(@Parameter(name = "uuid", description = "CA UUID")
                                            @PathVariable("uuid") String uuid,
                                        @Parameter(name = "caOrSsl", description = "Flag of CA or SSL")
                                            @RequestParam(value = "caOrSsl", defaultValue = "false", required = false) Boolean caOrSsl,
                                        HttpServletRequest request) {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                "Success",
                certificateService.countCaSigned(((UserProfileDTO) request.getSession().getAttribute("account")).getUsername(), uuid, caOrSsl));
    }

}
