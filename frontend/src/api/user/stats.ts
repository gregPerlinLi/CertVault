import type { BaseParams } from "@api/types";
import { callRestfulApi } from "@api/index";

export const countBindedCa = (params: BaseParams = {}) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/count",
    abort: params.abort
  });

export const countRequestedSslCerts = (params: BaseParams = {}) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/count",
    abort: params.abort
  });
