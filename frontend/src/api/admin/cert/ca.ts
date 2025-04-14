import type { AbortOption } from "@/api";
import type { CaInfoDTO, PaginationVO, ResponseCaDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllCaInfo = (
  page: number,
  limit: number,
  keyword?: string,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca",
    searchParams: { page, limit, keyword },
    abort
  });

export const getCaPrivKey = (
  uuid: string,
  password: string,
  abort?: AbortOption
) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/privkey",
    pathNames: { uuid },
    payload: { password },
    abort
  });

export interface RequestCaCertPayload {
  caUuid?: string;
  allowSubCa?: boolean;
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
export const requestCaCert = (
  payload: RequestCaCertPayload,
  abort?: AbortOption
) =>
  callRestfulApi<ResponseCaDTO>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca",
    payload,
    abort
  });

export const importCa = (
  certificate: string,
  privkey: string,
  comment: string,
  abort?: AbortOption
) =>
  callRestfulApi<ResponseCaDTO>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/import",
    payload: { certificate, privkey, comment },
    abort
  });

export const renewCaCert = (
  uuid: string,
  expiry: number,
  abort?: AbortOption
) =>
  callRestfulApi<ResponseCaDTO>({
    method: "PUT",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}",
    pathNames: { uuid },
    payload: { expiry },
    abort
  });

export const toggleCaAvailability = (uuid: string, abort?: AbortOption) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/available",
    pathNames: { uuid },
    abort
  });

export const updateCaComment = (
  uuid: string,
  comment: string,
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/comment",
    pathNames: { uuid },
    payload: { comment },
    abort
  });

export const deleteCaCert = (uuid: string, abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}",
    pathNames: { uuid },
    abort
  });
