package com.gregperlinli.certvault.controller;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.gregperlinli.certvault.annotation.NoValidSessionApiResponse;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginDTO;
import com.gregperlinli.certvault.domain.dto.LoginRecordDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import com.gregperlinli.certvault.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Authentication Controller
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code AuthController}
 * @date 2025/3/3 20:37
 */
@Tag(name = "Authentication", description = "Authentication API")
@RequestMapping("/api/v1/auth")
@CrossOrigin
@RestController
@Slf4j
public class AuthController {

    @Resource
    IUserService userService;

    @Resource
    ILoginRecordService loginRecordService;

    /**
     * Login
     *
     * @param loginDTO {@link LoginDTO}
     * @param request {@link HttpServletRequest}
     * @return {@link ResultVO}
     */
    @Operation(
            summary = "Login",
            description = "User login",
            responses = {
                    @ApiResponse(
                            responseCode = "444",
                            description = "Login Failed",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 444,
                                                "msg": "Login failed, username or password error",
                                                "data": null,
                                                "timestamp": "2025-04-04T09:45:34+08:00"
                                            }
                                            """)}
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "Login Success")
            }
    )
    @PostMapping(value =  "/login")
    public ResultVO<UserProfileDTO> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login entity")
                                              @RequestBody LoginDTO loginDTO,
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
            String userAgent = request.getHeader("User-Agent");
            UserAgent ua = UserAgentUtil.parse(userAgent);
            log.info("User-Agent: {}", userAgent);
            log.info("User: [{}|{}], Session ID: {} login with IP: {}, Browser: {}, OS: {}, Platform: {}",
                    loginResult.getUsername(),
                    AuthUtils.roleIdToRoleName(loginResult.getRole()),
                    request.getSession().getId(),
                    IpUtils.getIpAddress(),
                    ua.getBrowser().getName(),
                    ua.getOs().getName(),
                    ua.getPlatform().getName());
            Map<String, String> location = IpUtils.getLocation(IpUtils.getIpAddress());
            loginRecordService.addLoginRecord(
                    new LoginRecordDTO(
                            UUID.randomUUID().toString(),
                            loginResult.getUsername(),
                            IpUtils.getIpAddress(),
                            location.get("region"),
                            location.get("province"),
                            location.get("city"),
                            ua.getBrowser().getName(),
                            ua.getOs().getName(),
                            ua.getPlatform().getName(),
                            LocalDateTime.now(),
                            true
                    ),
                    request.getSession().getId()
            );
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
    @Operation(
            summary = "Logout",
            description = "User logout",
            responses = {
                    @ApiResponse(
                            responseCode = "444",
                            description = "Logout Failed",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 444,
                                                "msg": "Logout failed, please login first!",
                                                "data": null,
                                                "timestamp": "2025-04-04T09:45:34+08:00"
                                            }
                                            """
                                    )}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout Success",
                            content = @Content(
                                    schema = @Schema(implementation = ResultVO.NullResult.class),
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "msg": "Logout Success",
                                                "data": null,
                                                "timestamp": "2025-04-04T09:45:34+08:00"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @NoValidSessionApiResponse
    @DeleteMapping(value = "/logout")
    public ResultVO<Void> logout(HttpServletRequest request) {
        if ( request.getSession().getAttribute("account") != null ) {
            UserProfileDTO account = (UserProfileDTO) request.getSession().getAttribute("account");
            log.info("User: [{}|{}], Session ID: {} logout",
                    account.getUsername(),
                    AuthUtils.roleIdToRoleName(account.getRole()),
                    request.getSession().getId());
            userService.logout(request.getSession().getId());
            loginRecordService.setRecordOffline(request.getSession().getId());
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
