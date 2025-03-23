import type { UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const getProfile = () =>
  callRestfulApi<UserProfileDTO>({
    method: "GET",
    baseUrl: "/api/v1/user/profile"
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
