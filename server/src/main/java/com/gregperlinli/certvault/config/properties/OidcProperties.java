package com.gregperlinli.certvault.config.properties;

import com.gregperlinli.certvault.domain.dto.OidcProviderDTO;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenID Connect Properties
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code OidcProperties}
 * @date 2025/4/13 19:23
 */
@ConfigurationProperties(prefix = "oidc")
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@Getter
@Setter
@Data
public class OidcProperties {

    private boolean enabled;

    private Map<String, Provider> providers = new HashMap<>();

    public List<OidcProviderDTO> toDtoList() {
        return providers.entrySet().stream()
                .map(entry -> new OidcProviderDTO(
                        entry.getKey(),          // provider ID（如github）
                        entry.getValue().getName(),
                        entry.getValue().getLogo()
                )).toList();
    }

    @Getter
    @Setter
    @Data
    public static class Provider {
        private String name;
        private String logo;
        // Getter & Setter
    }

}
