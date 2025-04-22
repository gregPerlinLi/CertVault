import type {
  BaseParams,
  CaInfoDTO,
  PageParams,
  PageVO,
  ResponseCaDTO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export const getAllCaInfo = (
  params: BaseParams & PageParams<"uuid" | "comment" | "owner" | "status"> = {}
) =>
  callRestfulApi<PageVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface GetCaPrivKeyParams extends BaseParams {
  uuid: string;
  password: string;
}
export const getCaPrivKey = (params: GetCaPrivKeyParams) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/privkey",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface RequestCaCertParams extends BaseParams {
  caUuid?: string;
  allowSubCa?: boolean;
  algorithm?: string;
  keySize?: number;
  country: string;
  province: string;
  city: string;
  organization: string;
  organizationalUnit: string;
  commonName: string;
  expiry: number;
  comment: string;
  subjectAltNames?: {
    type: string;
    value: string;
  }[];
}
export const requestCaCert = (params: RequestCaCertParams) =>
  callRestfulApi<ResponseCaDTO>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });

export interface ImportCaParams extends BaseParams {
  certificate: string;
  privkey: string;
  comment: string;
}
export const importCa = (params: ImportCaParams) =>
  callRestfulApi<ResponseCaDTO>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/import",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });

export interface RenewCaCertParams extends BaseParams {
  uuid: string;
  expiry: number;
}
export const renewCaCert = (params: RenewCaCertParams) =>
  callRestfulApi<ResponseCaDTO>({
    method: "PUT",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface ToggleCaAvailabilityParams extends BaseParams {
  uuid: string;
}
export const toggleCaAvailability = (params: ToggleCaAvailabilityParams) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/available",
    pathNames: params,
    abort: params.abort
  });

export interface UpdateCaCommentParams extends BaseParams {
  uuid: string;
  comment: string;
}
export const updateCaComment = (params: UpdateCaCommentParams) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/comment",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface DeleteCaCertParams extends BaseParams {
  uuid: string;
}
export const deleteCaCert = (params: DeleteCaCertParams) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}",
    pathNames: params,
    abort: params.abort
  });
