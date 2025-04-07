import type { PaginationVO, UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllUsrInfo = (page: number, limit: number, keyword?: string) =>
  callRestfulApi<PaginationVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/users",
    searchParams: { page, limit, keyword }
  });
