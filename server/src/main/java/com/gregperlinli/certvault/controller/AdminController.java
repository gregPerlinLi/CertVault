package com.gregperlinli.certvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
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
@RequestMapping("/api/v1/admin")
@RestController
public class AdminController {

    @Resource
    IUserService userService;

    @Resource
    ICaService caService;

    @Resource
    ICaBindingService caBindingService;

    /**
     * Get users
     *
     * @param keyword the keyword to search
     * @param page the page number
     * @param limit the limit of the page
     * @return the result
     */
    @GetMapping(value = "/users")
    public ResultVO<PageDTO<UserProfileDTO>> getUsers(@RequestParam(value = "keyword", required = false) String keyword,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
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
    @GetMapping(value = "/cert/ca")
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@RequestParam(value = "keyword", required = false) String keyword,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
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
     * Get a CA certificate
     *
     * @deprecated Not implemented
     * @param uuid the uuid of the CA
     * @param isChain whether to get the certificate chain
     * @param request the request
     * @return the result
     */
    @GetMapping(value = "/cert/ca/cer/{uuid}")
    @Deprecated(since = "0.4.0")
    public ResultVO<String> getCaCert(@PathVariable("uuid") String uuid,
                                      @RequestParam(value = "isChain", defaultValue = "false") Boolean isChain,
                                      HttpServletRequest request) {
        String result = null;
        if ( isChain ) {
            result = caService.getCaCertChain(uuid,
                    ((UserProfileDTO) request.getSession().getAttribute("account")).getUsername());
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
    @PostMapping(value = "/cert/ca/{uuid}/privkey")
    public ResultVO<String> getCaPrivkey(@PathVariable("uuid") String uuid,
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
    @PatchMapping(value = "/cert/ca/{uuid}/comment")
    public ResultVO<Void> updateCaComment(@PathVariable("uuid") String uuid,
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
     * Request a new CA certificate
     *
     * @param requestCertDTO the request certificate DTO
     * @param request the request
     * @return the result
     * @throws Exception if the encrypt is failed
     */
    @PostMapping(value = "/cert/ca")
    public ResultVO<ResponseCaDTO> requestCa(@RequestBody RequestCertDTO requestCertDTO,
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
    @PutMapping(value = "/cert/ca/{uuid}")
    public ResultVO<ResponseCaDTO> renewCa(@PathVariable("uuid") String uuid,
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
    @DeleteMapping(value = "/cert/ca/{uuid}")
    public ResultVO<Void> deleteCa(@PathVariable("uuid") String uuid,
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
    @PostMapping(value = "/cert/ca/bind")
    public ResultVO<Void> bindCaToUser(@RequestBody CaBindingDTO caBindingDTO) {
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
    @PostMapping(value = "/cert/ca/binds")
    public ResultVO<Void> bindCasToUsers(@RequestBody List<CaBindingDTO> caBindingDTOs) throws Exception {
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
    @DeleteMapping(value = "/cert/ca/bind")
    public ResultVO<Void> unbindCaFromUser(@RequestBody CaBindingDTO caBindingDTO) {
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
    @DeleteMapping(value = "/cert/ca/binds")
    public ResultVO<Void> unbindCasFromUsers(@RequestBody List<CaBindingDTO> caBindingDTOs) throws Exception {
        Boolean result = caBindingService.deleteBindings(caBindingDTOs);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Failed");
    }

}
