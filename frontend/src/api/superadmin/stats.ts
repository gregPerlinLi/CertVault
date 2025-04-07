import { callRestfulApi } from "@/api";

export const countAllCaCert = (
  condition: "available" | "unavailable" | "none" = "none"
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ca/count",
    searchParams: { condition }
  });

export const countAllSslCert = () =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ssl/count"
  });

export const countAllUsr = (role: 1 | 2 | 3 | 0 = 0) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/count",
    searchParams: { role }
  });
