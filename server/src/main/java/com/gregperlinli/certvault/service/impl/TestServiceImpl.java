package com.gregperlinli.certvault.service.impl;

import com.gregperlinli.certvault.domain.dto.TestDTO;
import com.gregperlinli.certvault.service.interfaces.ITestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;

/**
 * Test Service Impl
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code TestServiceImpl}
 * @date 2025/3/10 16:22
 */
@Service
public class TestServiceImpl implements ITestService {

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Override
    public TestDTO testGet() {
        return new TestDTO(HttpMethod.GET.toString(), "Hello, This is CertVault SSL Certificate Management Platform", applicationVersion, getTimezoneOffset());
    }

    @Override
    public TestDTO testPost() {
        return new TestDTO(HttpMethod.POST.toString(), "Hello, This is CertVault SSL Certificate Management Platform", applicationVersion, getTimezoneOffset());
    }

    @Override
    public TestDTO testPut() {
        return new TestDTO(HttpMethod.PUT.toString(), "Hello, This is CertVault SSL Certificate Management Platform", applicationVersion, getTimezoneOffset());
    }

    @Override
    public TestDTO testDelete() {
        return new TestDTO(HttpMethod.DELETE.toString(), "Hello, This is CertVault SSL Certificate Management Platform", applicationVersion, getTimezoneOffset());
    }

    @Override
    public TestDTO testPatch() {
        return new TestDTO(HttpMethod.PATCH.toString(), "Hello, This is CertVault SSL Certificate Management Platform", applicationVersion, getTimezoneOffset());
    }

    @Override
    public String getBCryptedPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        return encoder.encode(password);
    }

    private String getTimezoneOffset() {
        GregorianCalendar calendar = new GregorianCalendar();
        int rawOffset = calendar.get(GregorianCalendar.ZONE_OFFSET);
        return String.format("+%02d:%02d", rawOffset / 3600000, (rawOffset / 60000) % 60);
    }
}
