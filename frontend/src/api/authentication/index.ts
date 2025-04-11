import type { AbortOption } from "@/api";
import type { UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const login = (
  username: string,
  password: string,
  abort?: AbortOption
) =>
  callRestfulApi<UserProfileDTO>({
    method: "POST",
    baseUrl: "/api/v1/auth/login",
    payload: { username, password },
    abort
  });

export const logout = (abort?: AbortOption) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/auth/logout",
    abort
  });
