import type { CertInfoDTO, PaginationVO, ResponseCertDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllSslCertInfo = (
  page: number,
  limit: number,
  keyword?: string
) =>
  callRestfulApi<PaginationVO<CertInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl",
    searchParams: { page, limit, keyword }
  });

export const getSslCert = (uuid: string) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ssl/cer",
    pathNames: [uuid]
  });

export const getSslPrivKey = (uuid: string, password: string) =>
  callRestfulApi<string>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl/privkey",
    pathNames: [uuid],
    payload: { password }
  });

export interface RequestCertPayload {
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
export const requestSslCert = (payload: RequestCertPayload) =>
  callRestfulApi<ResponseCertDTO>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/ssl",
    payload
  });

export const renewSslCert = (uuid: string, expiry: number) =>
  callRestfulApi<ResponseCertDTO>({
    method: "PUT",
    baseUrl: "/api/v1/user/cert/ssl",
    pathNames: [uuid],
    payload: { expiry }
  });

export const updateSslCertComment = (uuid: string, comment: string) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/cert/ssl/comment",
    pathNames: [uuid],
    payload: { comment }
  });

export const deleteSslCert = (uuid: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/cert/ssl",
    pathNames: [uuid]
  });
