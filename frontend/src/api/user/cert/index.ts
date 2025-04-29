import type { BaseParams, CertDetailDTO } from "@api/types";
import { callRestfulApi } from "@api/index";

export interface AnalyzeCertParams extends BaseParams {
  cert: string;
}
export const analyzeCert = (params: AnalyzeCertParams) =>
  callRestfulApi<CertDetailDTO>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/analyze",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });
