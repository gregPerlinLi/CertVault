import { callRestfulApi } from "@/api";

export const countRequestedCaCerts = (
  condition: "available" | "unavailable" | "none" = "none"
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/count",
    searchParams: { condition }
  });

export const countCaRequestedCerts = (uuid: string, caOrSsl: boolean) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/count",
    pathNames: { uuid },
    searchParams: { caOrSsl }
  });

export const countAllUsrs = (role: 1 | 2 | 3 | 0 = 0) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/count",
    searchParams: { role }
  });
