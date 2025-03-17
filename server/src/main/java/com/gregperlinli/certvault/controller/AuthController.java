package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.GeneralConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Authorization Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AuthController}
 * @date 2025/3/3 20:37
 */
@RequestMapping("/api/auth")
@RestController
@Slf4j
public class AuthController {

    @Resource
    IUserService userService;

    /**
     * Login
     *
     * @param loginDTO {@link LoginDTO}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link ResultVO}
     */
    @PostMapping(value =  "/login")
    public ResultVO<UserProfileDTO> login(@RequestBody LoginDTO loginDTO,
                                          HttpServletRequest request) {
        UserProfileDTO loginResult = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), request.getSession().getId());
        if ( loginResult != null ) {
            request.getSession().setAttribute("username", loginDTO.getUsername());
            request.getSession().setAttribute("account_type", loginResult.getRole());
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Login Success!", loginResult);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Login failed, username or password error");
    }

    /**
     * Logout
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResultVO}
     */
    @DeleteMapping(value = "/logout")
    public ResultVO<Void> logout(HttpServletRequest request) {
        if ( request.getSession().getAttribute("username") != null ) {
            log.info("User {} logout", request.getSession().getAttribute("username").toString());
            userService.logout(request.getSession().getId());
            request.getSession().invalidate();
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Logout Success!");
        }
        log.warn("User logout failed, please login first!");
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Logout failed, please login first!");
    }

}
