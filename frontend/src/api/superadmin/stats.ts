import type { BaseParams } from "@api/types";
import { callRestfulApi } from "@api/index";

export interface CountAllCaCertsParams extends BaseParams {
  condition?: "available" | "unavailable" | "none";
}
export const countAllCaCerts = (params: CountAllCaCertsParams = {}) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ca/count",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export const countAllSslCerts = (params: BaseParams = {}) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/cert/ssl/count",
    abort: params.abort
  });
