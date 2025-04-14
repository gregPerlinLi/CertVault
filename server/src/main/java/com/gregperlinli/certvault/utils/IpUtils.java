package com.gregperlinli.certvault.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;

/**
 * IP Utils
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code IpUtils}
 * @date 2025/4/5 19:42
 */
@Component
@Slf4j
public class IpUtils {

    @Value("${geoip.type}")
    private String injectGeoIpType;

    @Value("${geoip.file-path}")
    private String geoIpFilePath;

    private static String geoIpType;

    private static DatabaseReader reader;

    @PostConstruct
    public void init() {
        geoIpType = injectGeoIpType;
        if ( "mmdb".equals(geoIpType) ) {
            try {
                reader = new DatabaseReader.Builder(new File(geoIpFilePath)).build();
            } catch (IOException e) {
                log.warn("Failed to load GeoIP database, fallback to use local geo-ip data");
                try {
                    reader = new DatabaseReader.Builder(
                            new File(Objects.requireNonNull(IpUtils.class.getClassLoader().getResource("geoip/GeoLite2-City.mmdb")).getFile())
                    ).build();
                } catch ( IOException ex ) {
                    throw new RuntimeException("Failed to load GeoIP database", e);
                }
            }
        }
    }

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

    public static Map<String, String> getLocation(String ip) {

        if ( "mmdb".equals(geoIpType) ) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                CityResponse response = reader.city(ipAddress);

                log.debug("Get location information from geo-ip: {}", response.toJson());
                return Map.of(
                        "region", response.getCountry().getName(),
                        "province", response.getLeastSpecificSubdivision().getName(),
                        "city", response.getCity().getName()
                );
            } catch (Exception e) {
                log.debug("Failed to get location information from geo-ip");
                return Map.of(
                        "region", "Unknown",
                        "province", "Unknown",
                        "city", "Unknown"
                );
            }
        } else {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        "http://ip-api.com/json/" + ip,
                        HttpMethod.GET,
                        request,
                        String.class
                );
                String jsonString = response.getBody();
                JsonNode json = new ObjectMapper().readTree(jsonString);

                if ( "success".equals( json.get("status").asText() ) ) {
                    log.debug("Get location information from ip-api.com: {}", jsonString);
                    return Map.of(
                            "region", json.get("country").asText(),
                            "province", json.get("regionName").asText(),
                            "city", json.get("city").asText()
                    );
                } else {
                    throw new RuntimeException("Failed to get location information from ip-api.com");
                }
            } catch (Exception e) {
                log.debug("Failed to get location information from ip-api.com");
                return Map.of(
                        "region", "Unknown",
                        "province", "Unknown",
                        "city", "Unknown"
                );
            }
        }
    }

}
