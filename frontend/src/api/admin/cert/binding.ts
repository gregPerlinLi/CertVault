import type { PaginationVO, UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllCaBindedUsrs = (
  uuid: string,
  page: number,
  limit: number,
  keyword?: string
) =>
  callRestfulApi<PaginationVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/bind",
    pathNames: { uuid },
    searchParams: { page, limit, keyword }
  });

export const bindCaToUsrs = (caUuid: string, usernames: string[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds",
    payload: usernames.map((username) => ({ caUuid, username }))
  });

export const unbindCaFromUsrs = (caUuid: string, usernames: string[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds",
    payload: usernames.map((username) => ({ caUuid, username }))
  });
