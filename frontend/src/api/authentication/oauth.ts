import type { AbortOption } from "@/api";
import type { OidcProviderDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getOidcProvider = (abort?: AbortOption) =>
  callRestfulApi<OidcProviderDTO | null>({
    method: "GET",
    baseUrl: "/api/v1/auth/oauth/provider",
    abort
  });

export const oidcLogin = () => {
  window.open("/api/v1/auth/oauth/login", "_self", "noopener=true");
};
