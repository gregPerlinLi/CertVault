package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaBindingService;
import com.gregperlinli.certvault.service.interfaces.ICaService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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
     * Get all users
     *
     * @param page the page number
     * @param limit the limit of the page
     * @return the result
     */
    @GetMapping(value = "/users/{page}/{limit}")
    public ResultVO<PageDTO<UserProfileDTO>> getAllUsers(@PathVariable("page") Integer page,
                                                         @PathVariable("limit") Integer limit) {
        PageDTO<UserProfileDTO> result = userService.getAllUsers(page, limit);
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
    @GetMapping(value = "/cert/ca/{page}/{limit}")
    public ResultVO<PageDTO<CaInfoDTO>> getCas(@PathVariable("page") Integer page,
                                               @PathVariable("limit") Integer limit,
                                               HttpServletRequest request) {
        PageDTO<CaInfoDTO> result = caService.getCas(request.getSession().getAttribute("username").toString(), page, limit);
        if ( result != null && result.getList() != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data", result);
    }

    /**
     * Get a CA certificate
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
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
     * Get a CA private key
     *
     * @param requestPrivkeyDTO the request private key DTO
     * @param request the request
     * @return the result
     * @throws Exception if the decrypt is failed
     */
    @PostMapping(value = "/cert/ca/privkey")
    public ResultVO<String> getCaPrivkey(@RequestBody RequestPrivkeyDTO requestPrivkeyDTO,
                                         HttpServletRequest request) throws Exception {
        String result = caService.getCaPrivKey(requestPrivkeyDTO, request.getSession().getAttribute("username").toString());
        if ( result != null ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success", result);
        }
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Update a CA comment
     *
     * @param updateCommentDTO the update comment DTO
     * @param request the request
     * @return the result
     */
    @PatchMapping(value = "/cert/ca/comment")
    public ResultVO<Void> updateCaComment(@RequestBody UpdateCommentDTO updateCommentDTO,
                                             HttpServletRequest request) {
        Boolean result = caService.updateCaComment(updateCommentDTO.getUuid(), request.getSession().getAttribute("username").toString(), updateCommentDTO.getComment());
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
    @PatchMapping(value = "/cert/ca/available/{uuid}")
    public ResultVO<Boolean> modifyCaAvailable(@PathVariable("uuid") String uuid,
                                            HttpServletRequest request) {
        Boolean result = caService.modifyCaAvailability(uuid, request.getSession().getAttribute("username").toString());
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
        ResponseCaDTO result = caService.requestCa(requestCertDTO, request.getSession().getAttribute("username").toString());
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
    @PutMapping(value = "/cert/ca/{uuid}/{expiry}")
    public ResultVO<ResponseCaDTO> renewCa(@PathVariable("uuid") String uuid,
                                           @PathVariable("expiry") Integer expiry,
                                           HttpServletRequest request) throws Exception {
        ResponseCaDTO result = caService.renewCa(uuid, expiry, request.getSession().getAttribute("username").toString());
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
        Boolean result = caService.deleteCa(uuid, request.getSession().getAttribute("username").toString());
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

}
