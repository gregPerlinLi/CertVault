import type { CaInfoDTO, PaginationVO, ResponseCaDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllCaInfo = (page: number, limit: number, keyword?: string) =>
  callRestfulApi<PaginationVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca",
    searchParams: { page, limit, keyword }
  });

export const getCaPrivKey = (uuid: string, password: string) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/privkey",
    pathNames: [uuid],
    payload: { password }
  });

export interface RequestCaCertPayload {
  caUuid?: string;
  allowSubCa: boolean;
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
export const requestCaCert = (payload: RequestCaCertPayload) =>
  callRestfulApi<ResponseCaDTO>({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca",
    payload
  });

export const renewCaCert = (uuid: string, expiry: number) =>
  callRestfulApi<ResponseCaDTO>({
    method: "PUT",
    baseUrl: "/api/v1/admin/cert/ca",
    pathNames: [uuid],
    payload: { expiry }
  });

export const toggleCaAvailability = (uuid: string) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/available",
    pathNames: [uuid]
  });

export const updateCaComment = (uuid: string, comment: string) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/admin/cert/ca/comment",
    pathNames: [uuid],
    payload: { comment }
  });

export const deleteCa = (uuid: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/admin/cert/ca",
    pathNames: [uuid]
  });

export const bindCaToUsr = (caUuid: string, username: string) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/bind",
    payload: { caUuid, username }
  });

export const unbindCaFromUsr = (caUuid: string, username: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/admin/cert/ca/bind",
    payload: { caUuid, username }
  });
