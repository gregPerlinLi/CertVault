import type { LoginRecordDTO, PaginationVO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getAllLoginRecs = (
  page: number,
  limit: number,
  status: -1 | 0 | 1 = -1,
  keyword?: string
) =>
  callRestfulApi<PaginationVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/session",
    searchParams: { page, limit, status, keyword }
  });

export interface CreateMultiUsrsPayload {
  username: string;
  displayName: string;
  email: string;
  password: string;
  role: 1 | 2 | 3;
}
export const createMultiUsrs = (payload: CreateMultiUsrsPayload[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/superadmin/users",
    payload
  });

export interface UpdateUsrInfoPayload {
  displayName?: string;
  email?: string;
  newPassword?: string;
}
export const updateUsrInfo = (
  username: string,
  payload: UpdateUsrInfoPayload
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/user/{username}",
    pathNames: { username },
    payload
  });

export const updateMultiUsrRoles = (role: 1 | 2 | 3, usernames: string[]) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/users/role",
    payload: usernames.map((username) => ({ username, role }))
  });

export const deleteMultiUsrs = (usernames: string[]) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/users",
    payload: usernames
  });

export const forceLogoutUsr = (username: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/user/{username}/logout",
    pathNames: { username }
  });
