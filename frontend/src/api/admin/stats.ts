import type { BaseParams } from "@api/types";
import { callRestfulApi } from "@api/index";

export interface CountAllUsrsParams extends BaseParams {
  role?: 0 | 1 | 2 | 3;
}
export const countAllUsrs = (params: CountAllUsrsParams = {}) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/users/count",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface CountRequestedCaCertsParams extends BaseParams {
  condition?: "available" | "unavailable" | "none";
}
export const countRequestedCaCerts = (
  params: CountRequestedCaCertsParams = {}
) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/count",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface CountCaRequestedCertsParams extends BaseParams {
  uuid: string;
  caOrSsl?: boolean;
}
export const countCaRequestedCerts = (params: CountCaRequestedCertsParams) =>
  callRestfulApi<number>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/count",
    pathNames: params,
    searchParams: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });
