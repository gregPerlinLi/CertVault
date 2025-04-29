import type { BaseParams, CaInfoDTO, PageParams, PageVO } from "@api/types";
import { callRestfulApi } from "@api/index";

export const getAllBindedCaInfo = (
  params: BaseParams & PageParams<"uuid" | "comment" | "owner" | "status"> = {}
) =>
  callRestfulApi<PageVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface GetCaCertParams extends BaseParams {
  uuid: string;
  isChain?: boolean;
  needRootCa?: boolean;
}
export const getCaCert = (params: GetCaCertParams) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/{uuid}/cer",
    pathNames: params,
    searchParams: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });
