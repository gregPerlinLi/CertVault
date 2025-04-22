import type { BaseParams, OidcProviderDTO } from "@api/types";
import { callRestfulApi } from "@api/index";

export const getOidcProviders = (params: BaseParams = {}) =>
  callRestfulApi<OidcProviderDTO[] | null>({
    method: "GET",
    baseUrl: "/api/v1/auth/oauth/provider",
    abort: params.abort
  });

export const oidcLogin = (provider: string) => {
  window.open(`/api/v1/auth/oauth/login/${provider}`, "_self", "noopener=true");
};
