import { callRestfulApi } from "@/api";

export const countAllCaCerts = (
  condition: "available" | "unavailable" | "none" = "none"
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ca/count",
    searchParams: { condition }
  });

export const countAllSslCerts = () =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ssl/count"
  });

export const countAllUsrs = (role: 1 | 2 | 3 | 0 = 0) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/count",
    searchParams: { role }
  });
