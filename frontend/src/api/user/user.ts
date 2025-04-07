import type { LoginRecordDTO, PaginationVO, UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getProfile = () =>
  callRestfulApi<UserProfileDTO>({
    method: "GET",
    baseUrl: "/api/v1/user/profile"
  });

export const getUsrLoginRecs = (
  page: number,
  limit: number,
  status: number = -1
) =>
  callRestfulApi<PaginationVO<LoginRecordDTO>>({
    method: "GET",
    baseUrl: "/api/v1/user/session",
    searchParams: { page, limit, status }
  });

export interface UpdateProfileRequestPayload {
  displayName?: string;
  email?: string;
  oldPassword?: string;
  newPassword?: string;
}
export const updateProfile = (payload: UpdateProfileRequestPayload) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/user/profile",
    payload
  });

export const forceLogoutSession = (uuid: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/session/{uuid}/logout",
    pathNames: { uuid }
  });

export const forceLogoutUsr = () =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/user/logout"
  });
