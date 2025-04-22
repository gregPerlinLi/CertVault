import type {
  BaseParams,
  LoginRecordDTO,
  PageParams,
  PageVO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export interface GetAllLoginRecsParams extends BaseParams, PageParams {
  status?: -1 | 0 | 1;
}
export const getAllLoginRecs = (params: GetAllLoginRecsParams = {}) =>
  callRestfulApi<PageVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/superadmin/user/session",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface CreateMultiUsrsParams extends BaseParams {
  list: {
    username: string;
    displayName: string;
    email: string;
    password: string;
    role: 1 | 2 | 3;
  }[];
}
export const createMultiUsrs = (params: CreateMultiUsrsParams) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/superadmin/users",
    payload: params.list,
    abort: params.abort
  });

export interface UpdateUsrInfoParams extends BaseParams {
  username: string;
  displayName?: string;
  email?: string;
  newPassword?: string;
}
export const updateUsrInfo = (params: UpdateUsrInfoParams) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/user/{username}",
    pathNames: params,
    payload: { ...params, username: undefined, abort: undefined },
    abort: params.abort
  });

export interface UpdateMultiUsrRolesParams extends BaseParams {
  role: 1 | 2 | 3;
  usernames: string[];
}
export const updateMultiUsrRoles = (params: UpdateMultiUsrRolesParams) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/users/role",
    payload: params.usernames.map((username) => ({
      username,
      role: params.role
    })),
    abort: params.abort
  });

export interface DeleteMultiUsrsParams extends BaseParams {
  usernames: string[];
}
export const deleteMultiUsrs = (params: DeleteMultiUsrsParams) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/users",
    payload: params.usernames,
    abort: params.abort
  });

export interface ForceLogoutUsrParams extends BaseParams {
  username: string;
}
export const forceLogoutUsr = (params: ForceLogoutUsrParams) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/user/{username}/logout",
    pathNames: params,
    abort: params.abort
  });
