import type { AbortOption } from "@/api";
import type { PaginationVO, UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllCaBindedUsrs = (
  uuid: string,
  page: number,
  limit: number,
  keyword?: string,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/bind",
    pathNames: { uuid },
    searchParams: { page, limit, keyword },
    abort
  });

export const bindCaToUsrs = (
  caUuid: string,
  usernames: string[],
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds/create",
    payload: usernames.map((username) => ({ caUuid, username })),
    abort
  });

export const unbindCaFromUsrs = (
  caUuid: string,
  usernames: string[],
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds/delete",
    payload: usernames.map((username) => ({ caUuid, username })),
    abort
  });
