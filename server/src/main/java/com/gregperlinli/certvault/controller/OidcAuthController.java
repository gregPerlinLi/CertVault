package com.gregperlinli.certvault.controller;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gregperlinli.certvault.annotation.OidcDisabledApiResponse;
import com.gregperlinli.certvault.constant.RedisKeyConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginRecordDTO;
import com.gregperlinli.certvault.domain.dto.OidcProviderDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.gregperlinli.certvault.utils.AuthUtils;
import com.gregperlinli.certvault.utils.IpUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * OpenID Connect AuthController
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code OidcAuthController}
 * @date 2025/3/28 12:32
 */
@Tag(name = "OpenID Connect", description = "OpenID Connect Integration API")
@RequestMapping("/api/v1/auth/oauth")
@CrossOrigin
@RestController
@Slf4j
public class OidcAuthController {

    @Resource
    private ClientRegistrationRepository clientRegistrationRepository;

    @Resource
    private OAuth2AuthorizedClientService authorizedClientService;

    @Resource
    IUserService userService;

    @Resource
    ILoginRecordService loginRecordService;

    @Resource
    RedisTemplate redisTemplate;

    @Value("${oidc.enabled}")
    Boolean isEnabled;

    @Value("${oidc.provider}")
    String provider;

    @Value("${oidc.logo}")
    String logo;

    /**
     * Get OIDC provider
     *
     * @return {@link ResultVO}
     */
    @Operation(
            summary = "OIDC provider",
            description = "Get OIDC is Enabled",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OIDC Enabled",
                            content = @Content(
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 200,
                                                "msg": "OIDC Enabled",
                                                "data": {
                                                    "provider": "OpenID Connect",
                                                    "logo": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGhlaWdodD0iNTEycHgiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDUxMiA1MTI7IiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA1MTIgNTEyIiB3aWR0aD0iNTEycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxnIGlkPSJfeDMyXzM5LW9wZW5pZCI+PGc+PHBhdGggZD0iTTIzNC44NDksNDE5djYuNjIzYy03OS4yNjgtOS45NTgtMTM5LjMzNC01My4zOTMtMTM5LjMzNC0xMDUuNzU3ICAgIGMwLTM5LjMxMywzMy44NzMtNzMuNTk1LDg0LjQ4NS05Mi41MTFMMTc4LjAyMywxODBDODguODkyLDIwMi40OTcsMjYuMDAxLDI1Ni42MDcsMjYuMDAxLDMxOS44NjYgICAgYzAsNzYuMjg4LDkwLjg3MSwxMzkuMTI4LDIwOC45NSwxNDkuNzA1bDAuMDE4LTAuMDA5VjQxOUgyMzQuODQ5eiIgc3R5bGU9ImZpbGw6I0IyQjJCMjsiLz48cG9seWdvbiBwb2ludHM9IjMwNC43NzIsNDM2LjcxMyAzMDQuNjcsNDM2LjcxMyAzMDQuNjcsMjIxLjY2NyAzMDQuNjcsMjEzLjY2NyAzMDQuNjcsNDIuNDI5ICAgICAyMzQuODQ5LDc4LjI1IDIzNC44NDksMjIxLjY2NyAyMzQuOTY5LDIyMS42NjcgMjM0Ljk2OSw0NjkuNTYzICAgIiBzdHlsZT0iZmlsbDojRjc5MzFFOyIvPjxwYXRoIGQ9Ik00ODUuOTk5LDI5MS45MzhsLTkuNDQ2LTEwMC4xMTRsLTM1LjkzOCwyMC4zMzFDNDE1LjA4NywxOTYuNjQ5LDM4Mi41LDE3Ny41LDM0MCwxNzcuMjYxICAgIGwwLjAwMiwzNi40MDZ2Ny40OThjMy41MDIsMC45NjgsNi45MjMsMi4wMjQsMTAuMzAxLDMuMTI1YzE0LjE0NSw0LjYxMSwyNy4xNzYsMTAuMzUyLDM4LjY2NiwxNy4xMjhsLTM3Ljc4NiwyMS4yNTQgICAgTDQ4NS45OTksMjkxLjkzOHoiIHN0eWxlPSJmaWxsOiNCMkIyQjI7Ii8+PC9nPjwvZz48ZyBpZD0iTGF5ZXJfMSIvPjwvc3ZnPg=="
                                                },
                                                "timestamp": "2025-03-29T00:59:00.06971"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @OidcDisabledApiResponse
    @GetMapping(value = "/provider")
    public ResultVO<OidcProviderDTO> getOidcProvider() {
        if ( isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "OIDC Enabled",
                   new OidcProviderDTO(provider, logo));
        } else {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
    }

    /**
     * OIDC Login
     *
     * @param response {@link HttpServletResponse}
     * @return {@link ResultVO}
     * @throws Exception if an error occurs during the OIDC login process
     */
    @Operation(
            summary = "OIDC Login",
            description = "Redirect to OpenID Connect IdP Login",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to OIDC Login Page",
                            content = @Content(
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 302,
                                                "msg": "Redirect to OIDC Login Page",
                                                "data": null,
                                                "timestamp": "2025-03-29T00:59:00.06971"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @OidcDisabledApiResponse
    @GetMapping(value = "/login")
    public ResultVO<Void> login(HttpServletResponse response) throws Exception {
        // 这里直接重定向到 Spring Security 的 OAuth2 登录页面
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        response.sendRedirect("/oauth2/authorization/oidc");
        return new ResultVO<>(ResultStatusCodeConstant.REDIRECT.getResultCode(), "Redirect to OIDC Login Page");
    }

    /**
     * OIDC Callback
     *
     * @param code      {@link String}
     * @param state     {@link String}
     * @param request   {@link HttpServletRequest}
     * @param response  {@link HttpServletResponse}
     * @return {@link ResultVO}
     * @throws Exception if an error occurs during the OIDC callback process
     */
    @Operation(
            summary = "OIDC Callback",
            description = "OpenID Connect IdP Login Success Callback Endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to OIDC Login Page",
                            content = @Content(
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 302,
                                                "msg": "Redirect to OIDC Login Page",
                                                "data": {
                                                    "username": "testadmin",
                                                    "displayName": "测试管理员",
                                                    "email": "admin@test.com",
                                                    "role": 2
                                                },
                                                "timestamp": "2025-03-29T00:59:00.06971"
                                            }
                                            """
                                    )}
                            )
                    ),
                    @ApiResponse(
                            responseCode = "444",
                            description = "OIDC User is Null",
                            content = @Content(
                                    examples = {@ExampleObject(value =
                                            """
                                            {
                                                "code": 444,
                                                "msg": "OIDC User is Null",
                                                "data": null,
                                                "timestamp": "2025-03-29T00:59:00.06971"
                                            }
                                            """
                                    )}
                            )
                    )
            }
    )
    @OidcDisabledApiResponse
    @GetMapping(value = "/callback")
    public ResultVO<UserProfileDTO> oidcCallback(@Parameter(name = "code", description = "OpenID Connect IdP Response JWT Code", example = "c1ds5v1...")
                                                     @RequestParam("code") String code,
                                                 @Parameter(name = "state", description = "OpenID Connect IdP Response State", example = "s1ds5v1...")
                                                     @RequestParam("state") String state,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("oidc");

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(clientRegistration.getRedirectUri())
                .state(state)
                .build();

        log.debug("Redirect URI: {}", clientRegistration.getRedirectUri());

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(code)
                .redirectUri(clientRegistration.getRedirectUri())
                .state(state)
                .build();

        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        OAuth2AuthorizationCodeGrantRequest grantRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, authorizationExchange);

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(grantRequest);

        UserProfileDTO userProfileDTO = null;

        // 2. 解析ID Token, Access Token并创建OidcIdToken
        String idTokenString = (String) tokenResponse.getAdditionalParameters().get("id_token");
        if ( idTokenString != null ) {
            log.debug("OIDC ID Token: {}", idTokenString);

            // 解析JWT获取声明参数
            SignedJWT signedJWT = SignedJWT.parse(idTokenString);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // 获取issuedAt和expiredAt（单位：秒）
            Instant issuedAt = claimsSet.getIssueTime().toInstant();
            Instant expiredAt = claimsSet.getExpirationTime().toInstant();

            // 创建OidcIdToken实例
            OidcIdToken oidcIdToken = new OidcIdToken(
                    idTokenString, // ID Token原始字符串
                    issuedAt,      // issuedAt时间
                    expiredAt,     // expiredAt时间
                    claimsSet.getClaims() // ID Token的声明参数（claims）
            );
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new OAuth2UserAuthority(tokenResponse.getAdditionalParameters()));

            log.debug("OIDC ID Token: {}, {}, {}", oidcIdToken.getEmail(), oidcIdToken.getPreferredUsername(), oidcIdToken.getFullName());

            OidcUser oidcUser = new DefaultOidcUser(authorities, oidcIdToken);

            if (oidcUser == null) {
                // 如果 oidcUser 为空，返回错误信息
                return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "OIDC user is null.");
            }
            // 保存 OIDC 用户信息到数据库
            userProfileDTO = userService.integrateOidcUser(oidcUser.getEmail(), oidcUser.getAttributes());
        } else {
            // 针对 Github 不使用 ID Token 的问题进行适配
            // 获取access_token
            String accessToken = tokenResponse.getAccessToken().getTokenValue();

            // 使用access_token调用GitHub的/user API获取用户信息
            String userInfoString = AuthUtils.getGitHubUserInfo(accessToken);

            log.debug("GitHub User Info: {}", userInfoString);
            // 解析用户信息
            // 根据实际响应结构解析email、username等字段
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> userInfo = mapper.readValue(userInfoString, Map.class);

            // 保存用户信息到数据库
            userProfileDTO = userService.integrateGitHubUser(userInfo);

        }

        request.getSession().setAttribute("account", userProfileDTO);
        redisTemplate.opsForValue().set(RedisKeyConstant.USER.joinLoginPrefix(request.getSession().getId()), userProfileDTO, 60, TimeUnit.MINUTES);

        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(userAgent);
        log.info("User-Agent: {}", userAgent);
        log.info("User: [{}|{}], Session ID: {} login with IP: {}, Browser: {}, OS: {}, Platform: {}",
                userProfileDTO.getUsername(),
                AuthUtils.roleIdToRoleName(userProfileDTO.getRole()),
                request.getSession().getId(),
                IpUtils.getIpAddress(),
                ua.getBrowser().getName(),
                ua.getOs().getName(),
                ua.getPlatform().getName());
        loginRecordService.addLoginRecord(
                new LoginRecordDTO(
                        UUID.randomUUID().toString(),
                        userProfileDTO.getUsername(),
                        IpUtils.getIpAddress(),
                        ua.getBrowser().getName(),
                        ua.getOs().getName(),
                        ua.getPlatform().getName(),
                        LocalDateTime.now(),
                        true
                ),
                request.getSession().getId()
        );

        response.sendRedirect("/");
        return new ResultVO<>(ResultStatusCodeConstant.REDIRECT.getResultCode(), "OIDC login successful.", userProfileDTO);
    }

    /**
     * OIDC Login Success
     *
     * @return {@link ResultVO}
     */
    @Operation(
            summary = "OIDC Login Success",
            description = "OIDC Login Success Endpoint",
            hidden = true
    )
    @GetMapping(value = "/success")
    public ResultVO<Void> success() {
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Login successful.");
    }

    /**
     * OIDC Login Failure
     *
     * @return {@link ResultVO}
     */
    @Operation(
            summary = "OIDC Login Failure",
            description = "OIDC Login Failure Endpoint",
            hidden = true
    )
    @GetMapping(value = "/failure")
    public ResultVO<Void> failure() {
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Login failed.");
    }

}
