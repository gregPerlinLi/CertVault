import type {
  BaseParams,
  CertInfoDTO,
  PageParams,
  PageVO,
  ResponseCertDTO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export const getAllSslCertInfo = (params: BaseParams & PageParams = {}) =>
  callRestfulApi<PageVO<CertInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface GetSslCertParams extends BaseParams {
  uuid: string;
  isChain?: boolean;
  needRootCa?: boolean;
}
export const getSslCert = (params: GetSslCertParams) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/cer",
    pathNames: params,
    searchParams: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface GetSslPrivKeyParams extends BaseParams {
  uuid: string;
  password: string;
}
export const getSslPrivKey = (params: GetSslCertParams) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/privkey",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface RequestSslCertParams extends BaseParams {
  caUuid?: string;
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
export const requestSslCert = (params: RequestSslCertParams) =>
  callRestfulApi<ResponseCertDTO>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });

export interface RenewSslCertParams extends BaseParams {
  uuid: string;
  expiry: number;
}
export const renewSslCert = (params: RenewSslCertParams) =>
  callRestfulApi<ResponseCertDTO>({
    method: "PUT",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface UpdateSslCertCommentParams extends BaseParams {
  uuid: string;
  comment: string;
}
export const updateSslCertComment = (params: UpdateSslCertCommentParams) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/comment",
    pathNames: params,
    payload: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface DeleteSslCertParams extends BaseParams {
  uuid: string;
}
export const deleteSslCert = (params: DeleteSslCertParams) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}",
    pathNames: params,
    abort: params.abort
  });
