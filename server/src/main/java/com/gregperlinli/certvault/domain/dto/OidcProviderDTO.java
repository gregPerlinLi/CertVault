package com.gregperlinli.certvault.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * OpenID Connect Provider Data Transfer Object
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code OidcProviderDTO}
 * @date 2025/4/10 15:43
 */
@Schema(
        name = "OIDC Provider DTO",
        description = "OpenID Connect Provider DTO"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class OidcProviderDTO {

    @Schema(
            name = "provider",
            description = "OpenID Connect Provider",
            example = "OpenID Connect"
    )
    String provider;

    @Schema(
            name = "logo",
            description = "OpenID Connect Logo (BASE64 Encoded)",
            example = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGhlaWdodD0iNTEycHgiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDUxMiA1MTI7IiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA1MTIgNTEyIiB3aWR0aD0iNTEycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxnIGlkPSJfeDMyXzM5LW9wZW5pZCI+PGc+PHBhdGggZD0iTTIzNC44NDksNDE5djYuNjIzYy03OS4yNjgtOS45NTgtMTM5LjMzNC01My4zOTMtMTM5LjMzNC0xMDUuNzU3ICAgIGMwLTM5LjMxMywzMy44NzMtNzMuNTk1LDg0LjQ4NS05Mi41MTFMMTc4LjAyMywxODBDODguODkyLDIwMi40OTcsMjYuMDAxLDI1Ni42MDcsMjYuMDAxLDMxOS44NjYgICAgYzAsNzYuMjg4LDkwLjg3MSwxMzkuMTI4LDIwOC45NSwxNDkuNzA1bDAuMDE4LTAuMDA5VjQxOUgyMzQuODQ5eiIgc3R5bGU9ImZpbGw6I0IyQjJCMjsiLz48cG9seWdvbiBwb2ludHM9IjMwNC43NzIsNDM2LjcxMyAzMDQuNjcsNDM2LjcxMyAzMDQuNjcsMjIxLjY2NyAzMDQuNjcsMjEzLjY2NyAzMDQuNjcsNDIuNDI5ICAgICAyMzQuODQ5LDc4LjI1IDIzNC44NDksMjIxLjY2NyAyMzQuOTY5LDIyMS42NjcgMjM0Ljk2OSw0NjkuNTYzICAgIiBzdHlsZT0iZmlsbDojRjc5MzFFOyIvPjxwYXRoIGQ9Ik00ODUuOTk5LDI5MS45MzhsLTkuNDQ2LTEwMC4xMTRsLTM1LjkzOCwyMC4zMzFDNDE1LjA4NywxOTYuNjQ5LDM4Mi41LDE3Ny41LDM0MCwxNzcuMjYxICAgIGwwLjAwMiwzNi40MDZ2Ny40OThjMy41MDIsMC45NjgsNi45MjMsMi4wMjQsMTAuMzAxLDMuMTI1YzE0LjE0NSw0LjYxMSwyNy4xNzYsMTAuMzUyLDM4LjY2NiwxNy4xMjhsLTM3Ljc4NiwyMS4yNTQgICAgTDQ4NS45OTksMjkxLjkzOHoiIHN0eWxlPSJmaWxsOiNCMkIyQjI7Ii8+PC9nPjwvZz48ZyBpZD0iTGF5ZXJfMSIvPjwvc3ZnPg=="
    )
    String logo;

}
