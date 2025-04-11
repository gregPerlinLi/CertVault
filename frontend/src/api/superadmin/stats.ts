import type { AbortOption } from "@/api";
import { callRestfulApi } from "@/api";

export const countAllCaCerts = (
  condition: "available" | "unavailable" | "none" = "none",
  abort?: AbortOption
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ca/count",
    searchParams: { condition },
    abort
  });

export const countAllSslCerts = (abort?: AbortOption) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ssl/count",
    abort
  });
