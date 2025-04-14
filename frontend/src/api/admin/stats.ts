import type { AbortOption } from "@/api";
import { callRestfulApi } from "@/api";

export const countRequestedCaCerts = (
  condition: "available" | "unavailable" | "none" = "none",
  abort?: AbortOption
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/count",
    searchParams: { condition },
    abort
  });

export const countCaRequestedCerts = (
  uuid: string,
  caOrSsl: boolean,
  abort?: AbortOption
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/count",
    pathNames: { uuid },
    searchParams: { caOrSsl },
    abort
  });

export const countAllUsrs = (role: 1 | 2 | 3 | 0 = 0, abort?: AbortOption) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/count",
    searchParams: { role },
    abort
  });
