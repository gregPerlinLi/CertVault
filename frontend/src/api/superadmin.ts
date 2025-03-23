import { callRestfulApi } from ".";

export interface CreateUserPayload {
  username: string;
  displayName: string;
  email: string;
  password: string;
  role: 1 | 2;
}
export const createUsr = (payload: CreateUserPayload) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/superadmin/user",
    payload
  });

export const createMultiUsr = (payload: CreateUserPayload[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/superadmin/users",
    payload
  });

export interface UpdateUserInfoPayload {
  displayName?: string;
  email?: string;
  newPassword?: string;
}
export const updateUsrInfo = (
  username: string,
  payload: UpdateUserInfoPayload
) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/user/{username}",
    pathNames: { username },
    payload
  });

export const updateUsrRole = (username: string, role: 1 | 2) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/user/role",
    payload: { username, role }
  });

export const updateMultiUsrRole = (usernames: string[], role: 1 | 2) =>
  callRestfulApi({
    method: "PATCH",
    baseUrl: "/api/v1/superadmin/users/role",
    payload: usernames.map((username) => ({ username, role }))
  });

export const deleteUsr = (username: string) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/user/{username}",
    pathNames: { username }
  });

export const deleteMultiUsr = (usernames: string[]) =>
  callRestfulApi({
    method: "DELETE",
    baseUrl: "/api/v1/superadmin/users",
    payload: usernames
  });
