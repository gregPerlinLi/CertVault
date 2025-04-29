import type {
  BaseParams,
  PageParams,
  PageVO,
  UserProfileDTO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export interface GetAllCaBindedUsrsParams
  extends BaseParams,
    PageParams<"username" | "displayName" | "email" | "role"> {
  uuid: string;
}
export const getAllCaBindedUsrs = (params: GetAllCaBindedUsrsParams) =>
  callRestfulApi<PageVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/bind",
    pathNames: params,
    searchParams: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface GetAllCaNotBindedUsrsParams
  extends BaseParams,
    PageParams<"username" | "displayName" | "email" | "role"> {
  uuid: string;
}
export const getAllCaNotBindedUsrs = (params: GetAllCaNotBindedUsrsParams) =>
  callRestfulApi<PageVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/cert/ca/{uuid}/bind/not",
    pathNames: params,
    searchParams: { ...params, uuid: undefined, abort: undefined },
    abort: params.abort
  });

export interface BindCaToUsrsParams extends BaseParams {
  caUuid: string;
  usernames: string[];
}
export const bindCaToUsrs = (params: BindCaToUsrsParams) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds/create",
    payload: params.usernames.map((username) => ({
      caUuid: params.caUuid,
      username
    })),
    abort: params.abort
  });

export interface UnbindCaFromUsrsParams extends BaseParams {
  caUuid: string;
  usernames: string[];
}
export const unbindCaFromUsrs = (params: UnbindCaFromUsrsParams) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds/delete",
    payload: params.usernames.map((username) => ({
      caUuid: params.caUuid,
      username
    })),
    abort: params.abort
  });
