import type { CaInfoDTO, PaginationVO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllBindedCaInfo = (
  page: number,
  limit: number,
  keyword?: string
) =>
  callRestfulApi<PaginationVO<CaInfoDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca",
    searchParams: { page, limit, keyword }
  });

export const getCaCert = (uuid: string, isChain?: boolean) =>
  callRestfulApi<string>({
    method: "GET",
    baseUrl: "/api/v1/user/cert/ca/cer",
    pathNames: [uuid],
    searchParams: { isChain }
  });
