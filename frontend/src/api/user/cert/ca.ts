import type { AbortOption } from "@/api";
import type { CaInfoDTO, PaginationVO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllBindedCaInfo = (
  page: number,
  limit: number,
  keyword?: string,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca",
    searchParams: { page, limit, keyword },
    abort
  });

export const getCaCert = (
  uuid: string,
  isChain?: boolean,
  needRootCa?: boolean,
  abort?: AbortOption
) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/{uuid}/cer",
    pathNames: { uuid },
    searchParams: { isChain, needRootCa },
    abort
  });
