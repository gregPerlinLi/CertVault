import type { AbortOption } from "@/api";
import type { CertInfoDTO, PaginationVO, ResponseCertDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllSslCertInfo = (
  page: number,
  limit: number,
  keyword?: string,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<CertInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl",
    searchParams: { page, limit, keyword },
    abort
  });

export const getSslCert = (
  uuid: string,
  isChain?: boolean,
  abort?: AbortOption
) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/cer",
    pathNames: { uuid },
    searchParams: { isChain },
    abort
  });

export const getSslPrivKey = (
  uuid: string,
  password: string,
  abort?: AbortOption
) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/privkey",
    pathNames: { uuid },
    payload: { password },
    abort
  });

export interface RequestSslCertPayload {
  caUuid?: string;
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
export const requestSslCert = (
  payload: RequestSslCertPayload,
  abort?: AbortOption
) =>
  callRestfulApi<ResponseCertDTO>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl",
    payload,
    abort
  });

export const renewSslCert = (
  uuid: string,
  expiry: number,
  abort?: AbortOption
) =>
  callRestfulApi<ResponseCertDTO>({
    method: "PUT",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}",
    pathNames: { uuid },
    payload: { expiry },
    abort
  });

export const updateSslCertComment = (
  uuid: string,
  comment: string,
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}/comment",
    pathNames: { uuid },
    payload: { comment },
    abort
  });

export const deleteSslCert = (uuid: string, abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/cert/ssl/{uuid}",
    pathNames: { uuid },
    abort
  });
