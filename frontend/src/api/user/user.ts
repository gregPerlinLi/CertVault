import type {
  BaseParams,
  LoginRecordDTO,
  PageParams,
  PageVO,
  UserProfileDTO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export const getProfile = (params: BaseParams = {}) =>
  callRestfulApi<UserProfileDTO>({
    method: "GET",
    baseUrl: "/api/v1/user/profile",
    abort: params.abort
  });

export interface GetUsrLoginRecsParams
  extends BaseParams,
    Omit<
      PageParams<
        | "loginTime"
        | "ip"
        | "region"
        | "province"
        | "city"
        | "platform"
        | "os"
        | "browser"
      >,
      "keyword"
    > {
  status?: -1 | 0 | 1;
}
export const getUsrLoginRecs = (params: GetUsrLoginRecsParams = {}) =>
  callRestfulApi<PageVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/session",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });

export interface UpdateProfileRequestParams extends BaseParams {
  displayName?: string;
  email?: string;
  oldPassword?: string;
  newPassword?: string;
}
export const updateProfile = (params: UpdateProfileRequestParams = {}) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/profile",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });

export interface ForceLogoutSessionParams extends BaseParams {
  uuid: string;
}
export const forceLogoutSession = (params: ForceLogoutSessionParams) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/session/{uuid}/logout",
    pathNames: params,
    abort: params.abort
  });

export const forceLogoutUsr = (params: BaseParams = {}) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/logout",
    abort: params.abort
  });
