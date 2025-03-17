package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ICaService;
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
@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @Resource
    ICaService caService;

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
        return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "No data");
    }

    /**
     * Get a CA certificate
     *
     * @param uuid the uuid of the CA
     * @param request the request
     * @return the result
     */
    @GetMapping(value = "/cert/ca_cert/{uuid}")
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
    @PostMapping(value = "/cert/ca_privkey")
    public ResultVO<String> getCaPrivKey(@RequestBody RequestPrivkeyDTO requestPrivkeyDTO,
                                         HttpServletRequest request) throws Exception {
        String result = caService.getCaPrivKey(requestPrivkeyDTO.getUuid(), request.getSession().getAttribute("username").toString(), requestPrivkeyDTO.getPassword());
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
    @PatchMapping(value = "/cert/ca_comment")
    public ResultVO<Void> updateCaComment(@RequestBody UpdateCommentDTO updateCommentDTO,
                                             HttpServletRequest request) {
        Boolean result = caService.updateCaComment(updateCommentDTO.getUuid(), request.getSession().getAttribute("username").toString(), updateCommentDTO.getComment());
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Success");
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
    @PostMapping(value = "/cert/request_ca")
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
    @PutMapping(value = "/cert/renew_ca/{uuid}/{expiry}")
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

}
