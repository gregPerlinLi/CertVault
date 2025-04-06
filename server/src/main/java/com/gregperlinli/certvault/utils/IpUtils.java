package com.gregperlinli.certvault.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * IP Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code IpUtils}
 * @date 2025/4/5 19:42
 */
public class IpUtils {

    public static String getIpAddress() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return getRealIp(attributes.getRequest());
        }
        return "unknown";
    }

    private static String getRealIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP"};
        for (String h : headers) {
            String ip = request.getHeader(h);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 处理多个IP的情况（取第一个有效IP）
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

}
