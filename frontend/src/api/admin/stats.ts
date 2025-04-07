import { callRestfulApi } from "@/api";

export const countRequestedCa = (
  condition: "available" | "unavailable" | "none" = "none"
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/count",
    searchParams: { condition }
  });

export const countCaRequestedCert = (uuid: string, caOrSsl: boolean) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/count",
    pathNames: { uuid },
    searchParams: { caOrSsl }
  });
