import { callRestfulApi } from "@/api";

export const getOidcProvider = () =>
  callRestfulApi<string | null>({
    method: "GET",
    baseUrl: "/api/v1/auth/oauth/provider"
  });

export const oidcLogin = async () => {
  window.open("/api/v1/auth/oauth/login", "_self", "noopener=true");
};
