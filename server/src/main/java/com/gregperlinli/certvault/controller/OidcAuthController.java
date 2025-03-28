package com.gregperlinli.certvault.controller;

import com.gregperlinli.certvault.constant.RedisKeyConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * OpenID Connect AuthController
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code OidcAuthController}
 * @date 2025/3/28 12:32
 */
@RequestMapping("/api/v1/auth/oauth")
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
    RedisTemplate redisTemplate;

    @Value("${oidc.enabled}")
    Boolean isEnabled;

    @Value("${oidc.provider}")
    String provider;

    @GetMapping(value = "/provider")
    public ResultVO<String> getOidcProvider() {
        if ( isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "OIDC Enabled",
                   provider);
        } else {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
    }

    @GetMapping(value = "/login")
    public ResultVO<Void> login(HttpServletResponse response) throws Exception {
        // 这里直接重定向到 Spring Security 的 OAuth2 登录页面
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        response.sendRedirect("/oauth2/authorization/oidc");
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Redirect to OIDC Login Page");
    }

    @GetMapping(value = "/callback")
    public ResultVO<UserProfileDTO> oidcCallback(@RequestParam("code") String code,
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

        // 2. 解析ID Token并创建OidcIdToken
        String idTokenString = (String) tokenResponse.getAdditionalParameters().get("id_token");

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
        UserProfileDTO userProfileDTO = userService.integrateOidcUser(oidcUser.getEmail(), oidcUser.getAttributes());

        request.getSession().setAttribute("account", userProfileDTO);
        redisTemplate.opsForValue().set(RedisKeyConstant.USER.joinLoginPrefix(request.getSession().getId()), userProfileDTO, 60, TimeUnit.MINUTES);

        response.sendRedirect("/");
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "OIDC login successful.", userProfileDTO);
    }

    @GetMapping(value = "/success")
    public ResultVO<Void> success() {
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        return new ResultVO<>(ResultStatusCodeConstant.SUCCESS.getResultCode(), "Login successful.");
    }

    @GetMapping(value = "/failure")
    public ResultVO<Void> failure() {
        if ( !isEnabled ) {
            return new ResultVO<>(ResultStatusCodeConstant.NOT_FIND.getResultCode(), "OIDC Disabled");
        }
        return new ResultVO<>(ResultStatusCodeConstant.FAILED.getResultCode(), "Login failed.");
    }

}
