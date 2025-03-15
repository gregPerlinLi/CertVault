package com.gregperlinli.certvault.domain.entities;

import lombok.*;

/**
 * SSL Certificate Subject Alt Name
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code SubjectAltName}
 * @date 2025/3/15 14:25
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class SubjectAltName {

    /**
     * Supported Subject Alt Name Types
     */
    @Getter
    public enum Type {
        DNS_NAME("DNS"),       // DNS域名
        IP_ADDRESS("IP"),      // IP地址
        URI("URI"),            // 统一资源标识符
        EMAIL("EMAIL"),        // 邮箱地址
        DIRECTORY_NAME("DIRNAME"), // 目录名（X.500格式）
        EDIPartyName("EDIPARTY");  // EDI Party Name

        // 私有构造函数（若需要参数）
        private final String code;

        Type(String code) {
            this.code = code;
        }

    }

    /**
     * SAN类型（使用枚举确保类型安全）
     */
    private Type type;

    /**
     * SAN值（根据类型不同格式不同）
     */
    private String value;

    // 静态工厂方法：根据类型创建对象
    public static SubjectAltName of(Type type, String value) {
        return new SubjectAltName(type, value);
    }

    // 验证值格式（可选，按需实现）
    public void validate() {
        switch (type) {
            case DNS_NAME:
                if (!value.matches("^[a-zA-Z0-9.-]+$")) {
                    throw new IllegalArgumentException("Invalid DNS name format");
                }
                break;
            case IP_ADDRESS:
                if (!value.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$") &&
                        !value.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$")) {
                    throw new IllegalArgumentException("Invalid IP address format");
                }
                break;
            case URI:
                if (!value.startsWith("http://") && !value.startsWith("https://")) {
                    throw new IllegalArgumentException("URI must start with http:// or https://");
                }
                break;
            default:
        }
    }
}
