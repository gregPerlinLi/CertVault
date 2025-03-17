package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.certificate.CertDecoder;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.CertificateDetailsDTO;
import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
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
        if ( userService.updateOwnProfile(request.getSession().getAttribute("username").toString(), updateUserProfileDTO) ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

    /**
     * Get certificate details
     *
     * @param certBase64 Certificate Base64
     * @return {@link ResultVO} Result
     * @throws Exception e exception
     */
    @GetMapping(value = "/cert/decode/{cert}")
    public ResultVO<CertificateDetailsDTO> getCertificateDetails(@PathVariable("cert") String certBase64) throws Exception {
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "success", new CertificateDetailsDTO(CertDecoder.decodeCertificate(certBase64)));
    }

}
