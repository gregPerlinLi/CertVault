import type { AbortOption } from "@/api";
import type { LoginRecordDTO, PaginationVO, UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getProfile = (abort?: AbortOption) =>
  callRestfulApi<UserProfileDTO>({
    method: "GET",
    baseUrl: "/api/v1/user/profile",
    abort
  });

export const getUsrLoginRecs = (
  page: number,
  limit: number,
  status: number = -1,
  abort?: AbortOption
) =>
  callRestfulApi<PaginationVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/session",
    searchParams: { page, limit, status },
    abort
  });

export interface UpdateProfileRequestPayload {
  displayName?: string;
  email?: string;
  oldPassword?: string;
  newPassword?: string;
}
export const updateProfile = (
  payload: UpdateProfileRequestPayload,
  abort?: AbortOption
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/profile",
    payload,
    abort
  });

export const forceLogoutSession = (uuid: string, abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/session/{uuid}/logout",
    pathNames: { uuid },
    abort
  });

export const forceLogoutUsr = (abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/logout",
    abort
  });
