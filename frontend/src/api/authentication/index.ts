import type { BaseParams, UserProfileDTO } from "@api/types";
import { callRestfulApi } from "@api/index";

export interface LoginParams extends BaseParams {
  username: string;
  password: string;
}
export const login = (params: LoginParams) =>
  callRestfulApi<UserProfileDTO>({
    method: "POST",
    baseUrl: "/api/v1/auth/login",
    payload: { ...params, abort: undefined },
    abort: params.abort
  });

export const logout = (params: BaseParams = {}) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/auth/logout",
    abort: params.abort
  });
