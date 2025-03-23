import type {
  CertDetailDTO,
  CertInfoDTO,
  PaginationVO,
  ResultVO
} from "@/api/types";
import JSONbig from "json-bigint";

export const getUserCerts = async (page: number, limit: number) => {
  const resp = await fetch(`/api/v1/user/cert/cert/${page}/${limit}`);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<PaginationVO<CertInfoDTO>> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const analyzeCert = async (cert: string) => {
  const resp = await fetch(
    `/api/v1/user/cert/analyze/${encodeURIComponent(cert)}`
  );
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<CertDetailDTO> = JSONbig({
    useNativeBigInt: true,
    alwaysParseAsBig: true
  }).parse(await resp.text());
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
