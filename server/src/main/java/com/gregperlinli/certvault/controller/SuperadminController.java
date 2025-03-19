package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.UpdateUserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * Superadmin Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SuperadminController}
 * @date 2025/3/19 19:39
 */
@RequestMapping("/api/superadmin")
@RestController
public class SuperadminController {

    @Resource
    IUserService userService;

    @PutMapping(value = "/user/{username}")
    public ResultVO<Void> updateUserInfo(@PathVariable("username") String username,
                                         @RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        Boolean result = userService.updateUserProfile(username, updateUserProfileDTO, true);
        if ( result ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "update success");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "update failed");
    }

}
