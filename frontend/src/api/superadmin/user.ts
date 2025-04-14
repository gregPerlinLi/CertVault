import type { AbortOption } from "@/api";
import type { LoginRecordDTO, PaginationVO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllLoginRecs = (
  page: number,
  limit: number,
  status: -1 | 0 | 1 = -1,
  keyword?: string,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/session",
    searchParams: { page, limit, status, keyword },
    abort
  });

export interface CreateMultiUsrsPayload {
  username: string;
  displayName: string;
  email: string;
  password: string;
  role: 1 | 2 | 3;
}
export const createMultiUsrs = (
  payload: CreateMultiUsrsPayload[],
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/superadmin/users",
    payload,
    abort
  });

export interface UpdateUsrInfoPayload {
  displayName?: string;
  email?: string;
  newPassword?: string;
}
export const updateUsrInfo = (
  username: string,
  payload: UpdateUsrInfoPayload,
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/user/{username}",
    pathNames: { username },
    payload,
    abort
  });

export const updateMultiUsrRoles = (
  role: 1 | 2 | 3,
  usernames: string[],
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/users/role",
    payload: usernames.map((username) => ({ username, role })),
    abort
  });

export const deleteMultiUsrs = (usernames: string[], abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/users",
    payload: usernames,
    abort
  });

export const forceLogoutUsr = (username: string, abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/user/{username}/logout",
    pathNames: { username },
    abort
  });
