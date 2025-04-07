import { callRestfulApi } from "@/api";

export const countBindedCa = () =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/count"
  });

export const countRequestedSslCert = () =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/count"
  });
