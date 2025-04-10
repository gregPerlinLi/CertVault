import type { OidcProviderDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getOidcProvider = () =>
  callRestfulApi<OidcProviderDTO | null>({
    method: "GET",
    baseUrl: "/api/v1/auth/oauth/provider"
  });

export const oidcLogin = async () => {
  window.open("/api/v1/auth/oauth/login", "_self", "noopener=true");
};
