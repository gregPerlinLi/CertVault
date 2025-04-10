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
