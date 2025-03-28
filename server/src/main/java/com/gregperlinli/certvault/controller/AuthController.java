package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authorization Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AuthController}
 * @date 2025/3/3 20:37
 */
@RequestMapping("/api/v1/auth")
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
     * @return {@link ResultVO}
     */
    @PostMapping(value =  "/login")
    public ResultVO<UserProfileDTO> login(@RequestBody LoginDTO loginDTO,
                                          HttpServletRequest request) {
        UserProfileDTO loginResult = userService.login(loginDTO.getUsername(),
                loginDTO.getPassword(),
                request.getSession().getId());
        if ( loginResult != null ) {
            // request.getSession().setAttribute("username", loginDTO.getUsername());
            // request.getSession().setAttribute("account_type", loginResult.getRole());
            request.getSession().setAttribute("account", loginResult);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            loginResult.getUsername(),
                            null,
                            AuthorityUtils.createAuthorityList(
                                    "ROLE_" +
                                            AuthUtils.roleIdToRoleName(loginResult.getRole())
                            )
                    )
            );
            log.info("User: [{}|{}], Session ID: {} login",
                    loginResult.getUsername(),
                    AuthUtils.roleIdToRoleName(loginResult.getRole()),
                    request.getSession().getId());
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Login Success!",
                    loginResult);
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(),
                "Login failed, username or password error");
    }

    /**
     * Logout
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResultVO}
     */
    @DeleteMapping(value = "/logout")
    public ResultVO<Void> logout(HttpServletRequest request) {
        if ( request.getSession().getAttribute("account") != null ) {
            UserProfileDTO account = (UserProfileDTO) request.getSession().getAttribute("account");
            log.info("User: [{}|{}], Session ID: {} logout",
                    account.getUsername(),
                    AuthUtils.roleIdToRoleName(account.getRole()),
                    request.getSession().getId());
            userService.logout(request.getSession().getId());
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(),
                    "Logout Success!");
        }
        log.warn("User logout failed, please login first!");
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(),
                "Logout failed, please login first!");
    }

}
