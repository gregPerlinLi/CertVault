import type {
  CertInfoDTO,
  PaginationVO,
  ResponseCertDTO,
  ResultVO
} from "@/api/types";
import { createURLSearchParams } from "@/utils";

export interface RequestCertRequestPayload {
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
export const requestCert = async (payload: RequestCertRequestPayload) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify(payload)
  } satisfies RequestInit;

  const resp = await fetch("/api/v1/user/cert/cert", req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<ResponseCertDTO> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const renewCert = async (uuid: string, expiry: number) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "PUT",
    body: JSON.stringify({ expiry })
  } satisfies RequestInit;

  const resp = await fetch(`/api/v1/user/cert/cert/${uuid}`, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<ResponseCertDTO> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const getCerts = async (
  page: number,
  limit: number,
  keyword?: string
) => {
  const params = createURLSearchParams({ page, limit, keyword });
  const resp = await fetch(`/api/v1/user/cert/cert?${params.toString()}`);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<PaginationVO<CertInfoDTO>> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const getCert = async (uuid: string) => {
  const resp = await fetch(`/api/v1/user/cert/cert/cer/${uuid}`);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<string> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const getPrivKey = async (uuid: string, password: string) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify({ password })
  } satisfies RequestInit;

  const resp = await fetch(`/api/v1/user/cert/cert/privkey/${uuid}`, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<string> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const updateCertComment = async (uuid: string, comment: string) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "PATCH",
    body: JSON.stringify({ comment })
  } satisfies RequestInit;

  const resp = await fetch(`/api/v1/user/cert/cert/comment/${uuid}`, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }
};

export const deleteCert = async (uuid: string) => {
  const req = { method: "DELETE" } satisfies RequestInit;

  const resp = await fetch(`/api/v1/user/cert/cert/${uuid}`, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }
};
