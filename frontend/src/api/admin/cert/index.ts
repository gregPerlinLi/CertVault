import { callRestfulApi } from "@/api";

export const bindCaToUsrs = (caUuid: string, usernames: string[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds",
    payload: usernames.map((username) => ({ caUuid, username }))
  });

export const unbindCaFromUsrs = (caUuid: string, usernames: string[]) =>
  callRestfulApi({
    method: "POST",
    baseUrl: "/api/v1/admin/cert/ca/binds",
    payload: usernames.map((username) => ({ caUuid, username }))
  });
