import type { UserProfileDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const login = (username: string, password: string) =>
  callRestfulApi<UserProfileDTO>({
    method: "POST",
    baseUrl: "/api/v1/auth/login",
    payload: { username, password }
  });

export const logout = () =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/auth/logout"
  });
