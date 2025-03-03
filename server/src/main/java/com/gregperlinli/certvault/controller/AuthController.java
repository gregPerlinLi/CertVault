package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.GeneralConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginDTO;
import com.gregperlinli.certvault.domain.dto.UserDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authorization Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AuthController}
 * @date 2025/3/3 20:37
 */
@RestController("/api/auth")
public class AuthController {

    @Resource
    IUserService userService;

    @PostMapping(value =  "/login")
    public ResultVO<UserDTO> login(@RequestBody LoginDTO loginDTO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        UserDTO loginResult = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), request.getSession().getId());
        if ( loginResult != null ) {
            request.getSession().setAttribute("username", loginDTO.getUsername());
            request.getSession().setAttribute("account_type", loginResult.getRoles());
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Login Success!", loginResult);
        }
        response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.FAILED.getResultCode()));
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Login failed, username or password error");
    }

}
