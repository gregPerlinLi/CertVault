package com.gregperlinli.certvault.controller;

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
     * @param request {@link HttpServletRequest}
     * @return {@link ResultVO}
     */
    @GetMapping(value = "/profile")
    public ResultVO<UserProfileDTO> getProfile(HttpServletRequest request) {
        return new ResultVO<>(200, "success", userService.getOwnProfile(request.getSession().getAttribute("username").toString()));
    }

    @PutMapping(value = "/profile")
    public ResultVO<Void> updateProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO,
                                        HttpServletRequest request) {
        if ( userService.updateOwnProfile(request.getSession().getAttribute("username").toString(), updateUserProfileDTO) ) {
            return new ResultVO<>(200, "update success");
        }
        return new ResultVO<>(400, "update failed");
    }

}
