import type { AbortOption } from "@/api";
import { callRestfulApi } from "@/api";

export const countBindedCa = (abort?: AbortOption) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/count",
    abort
  });

export const countRequestedSslCert = (abort?: AbortOption) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/count",
    abort
  });
