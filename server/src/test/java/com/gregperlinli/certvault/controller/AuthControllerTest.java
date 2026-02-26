package com.gregperlinli.certvault.controller;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.gregperlinli.certvault.domain.dto.LoginDTO;
import com.gregperlinli.certvault.domain.dto.LoginRecordDTO;
import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthController 测试类
 *
 * @author gregPerlinLi
 */
@SpringBootTest
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private IUserService userService;

    @Mock
    private ILoginRecordService loginRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 清除安全上下文
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLoginWithCertVaultCLIClient() {
        // 准备测试数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("testpass");

        UserProfileDTO userProfile = new UserProfileDTO();
        userProfile.setUsername("testuser");
        userProfile.setRole(1); // 假设是普通用户角色

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        
        // 设置 CertVaultCLI 的 User-Agent
        String certVaultCliUserAgent = "Mozilla/5.0 (X11; Linux x86_64) CertVaultCLI/2.0.0";
        request.addHeader("User-Agent", certVaultCliUserAgent);

        // Mock 服务方法
        when(userService.login(anyString(), anyString(), anyString())).thenReturn(userProfile);
        doNothing().when(loginRecordService).addLoginRecord(any(LoginRecordDTO.class), anyString());

        // 执行登录
        authController.login(loginDTO, request);

        // 验证 LoginRecordDTO 中的浏览器信息
        ArgumentCaptor<LoginRecordDTO> loginRecordCaptor = ArgumentCaptor.forClass(LoginRecordDTO.class);
        verify(loginRecordService).addLoginRecord(loginRecordCaptor.capture(), anyString());
        
        LoginRecordDTO capturedLoginRecord = loginRecordCaptor.getValue();
        assertEquals("CertVaultCLI", capturedLoginRecord.getBrowser(), 
            "浏览器应该被识别为 CertVaultCLI");
    }

    @Test
    void testLoginWithRegularBrowser() {
        // 准备测试数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("testpass");

        UserProfileDTO userProfile = new UserProfileDTO();
        userProfile.setUsername("testuser");
        userProfile.setRole(1);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        
        // 设置普通浏览器的 User-Agent
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        request.addHeader("User-Agent", chromeUserAgent);

        // Mock 服务方法
        when(userService.login(anyString(), anyString(), anyString())).thenReturn(userProfile);
        doNothing().when(loginRecordService).addLoginRecord(any(LoginRecordDTO.class), anyString());

        // 执行登录
        authController.login(loginDTO, request);

        // 验证 LoginRecordDTO 中的浏览器信息
        ArgumentCaptor<LoginRecordDTO> loginRecordCaptor = ArgumentCaptor.forClass(LoginRecordDTO.class);
        verify(loginRecordService).addLoginRecord(loginRecordCaptor.capture(), anyString());
        
        LoginRecordDTO capturedLoginRecord = loginRecordCaptor.getValue();
        assertEquals("Chrome", capturedLoginRecord.getBrowser(), 
            "浏览器应该被识别为 Chrome");
    }

    @Test
    void testUserAgentParsing() {
        // 测试 User-Agent 解析逻辑
        String certVaultCliUserAgent = "Mozilla/5.0 (X11; Linux x86_64) CertVaultCLI/2.0.0";
        UserAgent ua = UserAgentUtil.parse(certVaultCliUserAgent);
        
        // 对于未知的浏览器，Hutool 会返回 "Unknown"
        assertEquals("Unknown", ua.getBrowser().getName());
        assertTrue(certVaultCliUserAgent.contains("CertVaultCLI/"));
    }
}